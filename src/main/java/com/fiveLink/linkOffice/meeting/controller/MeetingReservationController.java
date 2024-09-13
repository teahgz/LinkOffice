package com.fiveLink.linkOffice.meeting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.meeting.domain.MeetingDto;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservationDto;
import com.fiveLink.linkOffice.meeting.service.MeetingReservationService;
import com.fiveLink.linkOffice.meeting.service.MeetingService;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.service.DepartmentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MeetingReservationController {

    private final MeetingService meetingService; 
    private final MemberService memberService;
    private final MeetingReservationService meetingReservationService;
    private final DepartmentService departmentService;

    @Autowired
    public MeetingReservationController(MeetingService meetingService, MemberService memberService, MeetingReservationService meetingReservationService, DepartmentService departmentService) {
        this.meetingService = meetingService; 
        this.memberService = memberService;
        this.meetingReservationService = meetingReservationService;
        this.departmentService = departmentService;
    }

    // 사용자 예약 페이지
    @GetMapping("/employee/meeting/reservation")
    public String empListMeetings(Model model) {
        Long memberNo = memberService.getLoggedInMemberNo();  
        List<MemberDto> memberDto = memberService.getMembersByNo(memberNo);
        List<MeetingDto> meetings = meetingService.getAllMeetings();
 
        model.addAttribute("memberdto", memberDto.get(0));  
        model.addAttribute("meetings", meetings);
        return "/employee/meeting/meetingReservation";
    }
     
    // 해당 날짜 예약 정보 
    @GetMapping("/date/reservations")
    @ResponseBody
    public List<MeetingReservationDto> getReservationsByDate(@RequestParam("date") String date) { 
        return meetingReservationService.getReservationsByDate(date);
    }
    
    // 전체 회의실 목록
    @GetMapping("/api/meetings")
    @ResponseBody
    public List<MeetingDto> getAllMeetings() {
        return meetingService.getAllMeetings();
    }
    
    // 특정 회의실 상세 정보 조회
    @GetMapping("/api/meetings/{meetingNo}")
    @ResponseBody
    public MeetingDto getMeetingById(@PathVariable("meetingNo") Long meetingId) {
        return meetingService.getMeetingById(meetingId);
    }  
    
    
    
    // 조직도
    @GetMapping("/meeting/chart")
    @ResponseBody
    public List<Map<String, Object>> getOrganizationChart() {
		List<DepartmentDto> departments = departmentService.getAllDepartments();
		List<MemberDto> members = memberService.getAllMembersChartOut();
		return buildTree(departments, members);
	}

    private List<Map<String, Object>> buildTree(List<DepartmentDto> departments, List<MemberDto> members) {
        Map<Long, Map<String, Object>> departmentMap = new HashMap<>();
        Map<Long, List<MemberDto>> membersByDepartment = new HashMap<>();
        
        // 부서별 구성원 그룹화
        for (MemberDto member : members) {
            List<MemberDto> departmentMembers = membersByDepartment.get(member.getDepartment_no());
            if (departmentMembers == null) {
                departmentMembers = new ArrayList<>();
                membersByDepartment.put(member.getDepartment_no(), departmentMembers);
            }
            departmentMembers.add(member);
        } 
        
        // 부서 노드
        for (DepartmentDto dept : departments) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", "dept_" + dept.getDepartment_no());
            node.put("text", dept.getDepartment_name());
            node.put("type", "department");
            node.put("children", new ArrayList<>());
            departmentMap.put(dept.getDepartment_no(), node);
        }
        
        // 부서 계층 구조
        List<Map<String, Object>> result = new ArrayList<>();
        for (DepartmentDto dept : departments) {
            if (dept.getDepartment_high() == 0) {
                Map<String, Object> departmentNode = buildDepartmentHierarchy(dept, departmentMap, membersByDepartment);
                if (departmentNode != null) {
                    result.add(departmentNode);
                }
            }
        }
        
        return result;
    }

    private Map<String, Object> buildDepartmentHierarchy(DepartmentDto dept,
			Map<Long, Map<String, Object>> departmentMap, Map<Long, List<MemberDto>> membersByDepartment) {
		Map<String, Object> node = departmentMap.get(dept.getDepartment_no());
		List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");

		boolean hasSubDepartments = false;
		boolean hasMembers = false;

		if (dept.getSubDepartments() != null && !dept.getSubDepartments().isEmpty()) {
			for (DepartmentDto subDept : dept.getSubDepartments()) {
				List<MemberDto> subDeptMembers = membersByDepartment.get(subDept.getDepartment_no());
				boolean hasSubDeptMembers = subDeptMembers != null && !subDeptMembers.isEmpty();
				if (hasSubDeptMembers || (subDept.getSubDepartments() != null && !subDept.getSubDepartments().isEmpty())) {
					Map<String, Object> subDeptNode = new HashMap<>();
					subDeptNode.put("id", "subdept_" + subDept.getDepartment_no());
					subDeptNode.put("text", subDept.getDepartment_name());
					subDeptNode.put("type", "subdepartment");
					subDeptNode.put("children", new ArrayList<>());

					// 하위 부서에 속한 구성원 추가
					if (hasSubDeptMembers) {
                        for (MemberDto member : subDeptMembers) {
                            Map<String, Object> memberNode = createMemberNode(member);
                            ((List<Map<String, Object>>) subDeptNode.get("children")).add(memberNode);
                        }
                    }
                    
                    // 하위 부서 추가
                    children.add(subDeptNode);
                    hasSubDepartments = true;
                }
            }
		}

		// 현재 부서의 구성원 확인 및 추가
		List<MemberDto> deptMembers = membersByDepartment.get(dept.getDepartment_no());
		if (deptMembers != null && !deptMembers.isEmpty()) {
			hasMembers = true;
			for (MemberDto member : deptMembers) {
				Map<String, Object> memberNode = createMemberNode(member);
				children.add(memberNode);
			}
		}

		// 부서에 하위 부서나 구성원이 있는 경우에만 노드 반환
		if (hasMembers || hasSubDepartments) {
			return node;
		} else {
			return null;
		}
	}

    private Map<String, Object> createMemberNode(MemberDto member) {
        Map<String, Object> memberNode = new HashMap<>();
        memberNode.put("id", "member_" + member.getMember_no());
        memberNode.put("text", member.getMember_name() + " " + member.getPosition_name());
        memberNode.put("type", "member");
        return memberNode;
    }  
    
    
    // 조직도 확인 버튼 -> 예약 모달 출력
	@PostMapping("/api/meeting/saveSelectedMembers")
	@ResponseBody
	public Map<String, Object> saveSelectedMembers(@RequestBody Map<String, List<String>> selectedMembers) {
		List<String> memberNumbers = selectedMembers.get("members");

		// 조직도에서 선택한 사원 번호 출력
		System.out.println("선택한 사원 번호 목록: " + memberNumbers);

		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "선택한 사원이 성공적으로 저장되었습니다.");
		return response;
	}
	
	
	@PostMapping("/api/reservation/save")
	@ResponseBody
	public void saveReservation(
	    @RequestParam("reservation_room") Long reservationRoom,
	    @RequestParam("reservation_date") String reservationDate,
	    @RequestParam("reservation_start_time") String reservationStartTime,
	    @RequestParam("reservation_end_time") String reservationEndTime, 
	    @RequestParam("reservation_purpose") String reservationPurpose,
	    @RequestParam("selectedMembers") String selectedMembers,
	    HttpServletResponse response) {

	    try { 
 
	        System.out.println("reservationRoom: " + reservationRoom);
	        System.out.println("reservationDate: " + reservationDate);
	        System.out.println("reservationStartTime: " + reservationStartTime);
	        System.out.println("reservationEndTime: " + reservationEndTime); 
	        System.out.println("reservationPurpose: " + reservationPurpose);
	        System.out.println("selectedMembers: " + selectedMembers);

	        response.setStatus(HttpServletResponse.SC_OK);
	    } catch (Exception e) {
	        System.err.println("예약 저장 중 오류 발생: " + e.getMessage()); 
	    }
	}

}

package com.fiveLink.linkOffice.schedule.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.schedule.domain.Schedule;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategoryDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeat;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeatDto;
import com.fiveLink.linkOffice.schedule.service.ScheduleCategoryService;
import com.fiveLink.linkOffice.schedule.service.ScheduleService;

@Controller
public class ScheduleApiController { 
	private final MemberService memberService; 
	private final ScheduleCategoryService scheduleCategoryService; 
	private final ScheduleService scheduleService;
	
	@Autowired
	public ScheduleApiController(MemberService memberService, ScheduleCategoryService scheduleCategoryService, ScheduleService scheduleService) { 
	    this.memberService = memberService; 
	    this.scheduleCategoryService = scheduleCategoryService; 
	    this.scheduleService = scheduleService; 
	} 

	
	@GetMapping("/schedule/category/get/{categoryId}")
	@ResponseBody
	public ScheduleCategoryDto getScheduleCategory(@PathVariable("categoryId") Long id) {
	    System.out.println(id);
	    return scheduleCategoryService.getScheduleCategoryById(id);
	}

	// 수정
	@PostMapping("/schedule/category/update") 
	@ResponseBody
	public Map<String, String> updateScheduleCategory(@RequestBody Map<String, Object> payload) {
	    Long categoryId = Long.valueOf(payload.get("scheduleCategoryId").toString());
	    String categoryName = (String) payload.get("scheduleCategoryName");
	    String categoryColor = (String) payload.get("scheduleCategoryColor");
	    Long onlyAdmin = (long) ((Boolean) payload.get("onlyAdmin") ? 1 : 0);
 
	    Map<String, String> response = scheduleCategoryService.updateScheduleCategory(categoryId, categoryName, categoryColor, onlyAdmin);
 
	    if (response.containsKey("success")) {
	        response.put("res_code", "200"); 
	    } else {
	        response.put("res_code", "400"); 
	    }

	    return response;
	} 

	// 삭제
	@PostMapping("/schedule/category/delete")
	@ResponseBody
	public Map<String, String> deleteScheduleCategory(@RequestBody Map<String, Object> payload) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "카테고리 삭제 중 오류가 발생했습니다.");

	    try {
	        Long categoryId = Long.valueOf(payload.get("scheduleCategoryId").toString());

	        boolean deleteSuccess = scheduleCategoryService.deleteScheduleCategory(categoryId);

	        if (deleteSuccess) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "카테고리가 삭제되었습니다.");
	        } else {
	            resultMap.put("res_code", "400");
	            resultMap.put("res_msg", "해당 카테고리로 등록된 일정이 있어 삭제가 불가능합니다.");
	        }
	    } catch (Exception e) {
	        resultMap.put("res_msg", e.getMessage());
	    } 
	    return resultMap;
	}

	// 등록
	@PostMapping("/schedule/category/add")
	@ResponseBody
	public Map<String, String> addScheduleCategory(@RequestBody Map<String, Object> payload) {
	    String categoryName = (String) payload.get("scheduleCategoryName");
	    String categoryColor = (String) payload.get("scheduleCategoryColor");
	    Long onlyAdmin = (long) ((Boolean) payload.get("onlyAdmin") ? 1 : 0);

	    Map<String, String> response = scheduleCategoryService.addScheduleCategory(categoryName, categoryColor, onlyAdmin);

	    if (response.containsKey("success")) {
	        response.put("res_code", "200");
	    } else {
	        response.put("res_code", "400"); 
	    }

	    return response;
	} 
	
	// 관리자 - 일정 등록 카테고리
    @GetMapping("/categories")
    @ResponseBody
    public List <ScheduleCategoryDto> getAlladminScheduleCategory() {
    	System.out.println(scheduleCategoryService.getAlladminScheduleCategory());
        return scheduleCategoryService.getAlladminScheduleCategory();
    } 
    
    // 관리자 - 일정 등록
    @PostMapping("/company/schedule/save")
    @ResponseBody
    public String saveSchedule(@RequestBody Map<String, Object> request) {
    	Long memberNo = memberService.getLoggedInMemberNo();  
        ScheduleDto scheduleDto = new ScheduleDto();
        ScheduleRepeatDto scheduleRepeatDto = new ScheduleRepeatDto();
         
        scheduleDto.setMember_no(memberNo);
        scheduleDto.setSchedule_title((String) request.get("title"));
        scheduleDto.setSchedule_comment((String) request.get("description"));
        scheduleDto.setSchedule_start_date((String) request.get("startDate"));
        scheduleDto.setSchedule_allday((Boolean) request.get("allDay") ? 1L : 0L); 
        scheduleDto.setSchedule_end_date((String) request.get("endDate"));
        if (request.get("repeat") != null) {
            Object repeatValue = request.get("repeat");
            long repeat = repeatValue instanceof Integer ? ((Integer) repeatValue).longValue() : Long.parseLong(repeatValue.toString());
            scheduleDto.setSchedule_repeat(repeat != 0 ? 1L : 0L);
            
            if (repeat != 0) {
                scheduleRepeatDto.setSchedule_repeat_type(repeat);  
            }
        } 
         
        if (request.get("category") != null) {
            Object category = request.get("category");
            scheduleDto.setSchedule_category_no(category instanceof Integer ? ((Integer) category).longValue() : Long.parseLong((String) category));
        }  

        scheduleDto.setSchedule_start_time((String) request.get("startTime"));
        scheduleDto.setSchedule_end_time((String) request.get("endTime"));
         
        if (request.get("schedule_day_of_week") != null) {
            Object scheduleDayOfWeek = request.get("schedule_day_of_week");
            scheduleRepeatDto.setSchedule_repeat_day(scheduleDayOfWeek instanceof Integer ? ((Integer) scheduleDayOfWeek).longValue() : Long.parseLong((String) scheduleDayOfWeek));
        }
        if (request.get("schedule_week") != null) {
            Object scheduleWeek = request.get("schedule_week");
            scheduleRepeatDto.setSchedule_repeat_week(scheduleWeek instanceof Integer ? ((Integer) scheduleWeek).longValue() : Long.parseLong((String) scheduleWeek));
        }
        if (request.get("schedule_date") != null) {
            Object scheduleDate = request.get("schedule_date");
            scheduleRepeatDto.setSchedule_repeat_date(scheduleDate instanceof Integer ? ((Integer) scheduleDate).longValue() : Long.parseLong((String) scheduleDate));
        }
        if (request.get("schedule_month") != null) {
            Object scheduleMonth = request.get("schedule_month");
            scheduleRepeatDto.setSchedule_repeat_month(scheduleMonth instanceof Integer ? ((Integer) scheduleMonth).longValue() : Long.parseLong((String) scheduleMonth));
        }
        if (request.get("repeatEndDate") != null) {
            scheduleRepeatDto.setSchedule_repeat_end_date((String) request.get("repeatEndDate"));
        }
         
        scheduleService.saveCompanySchedule(scheduleDto, scheduleRepeatDto);
        
        return "success";  
    }

    // 관리자 - 월간 일정 
    @ResponseBody
    @GetMapping("/api/company/schedules")
    public List<ScheduleDto> getSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules(); 
        
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : schedules) {
            ScheduleDto dto = ScheduleDto.toDto(schedule);
            scheduleDtos.add(dto);
        }

        System.out.println("scheduleDtos : " + scheduleDtos);
        return scheduleDtos;
    }

    // 관리자 - 반복 일정  
    @ResponseBody
    @GetMapping("/api/repeat/schedules")
    public List<ScheduleRepeatDto> getRepeatSchedules() {
        List<ScheduleRepeat> scheduleRepeats = scheduleService.getAllRepeatSchedules(); 
        
        List<ScheduleRepeatDto> scheduleRepeatDtos = new ArrayList<>();
        for (ScheduleRepeat scheduleRepeat : scheduleRepeats) {
        	ScheduleRepeatDto dto = ScheduleRepeatDto.toDto(scheduleRepeat);
        	scheduleRepeatDtos.add(dto);
        }

        System.out.println("scheduleRepeatDtos : " + scheduleRepeatDtos);
        return scheduleRepeatDtos;
    }
 

}

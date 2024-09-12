package com.fiveLink.linkOffice.meeting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.meeting.domain.MeetingDto;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservationDto; 
import com.fiveLink.linkOffice.meeting.service.MeetingReservationService;
import com.fiveLink.linkOffice.meeting.service.MeetingService;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class MeetingReservationController {

    private final MeetingService meetingService; 
    private final MemberService memberService;
    private final MeetingReservationService meetingReservationService;

    @Autowired
    public MeetingReservationController(MeetingService meetingService, MemberService memberService, MeetingReservationService meetingReservationService) {
        this.meetingService = meetingService; 
        this.memberService = memberService;
        this.meetingReservationService = meetingReservationService;
    }

    // 사용자 예약 페이지
    @GetMapping("/employee/meeting/reservation")
    public String empListMeetings(Model model) {
        Long memberNo = memberService.getLoggedInMemberNo();  
        List<MemberDto> memberDto = memberService.getMembersByNo(memberNo);
        List<MeetingDto> meetings = meetingService.getAllMeetings();

        model.addAttribute("memberdto", memberDto);
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


}

package com.fiveLink.linkOffice.meeting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fiveLink.linkOffice.meeting.domain.MeetingDto;
import com.fiveLink.linkOffice.meeting.service.MeetingService;

@Controller
public class MeetingController {
    
    private final MeetingService meetingService;

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }
 
    @GetMapping("/meetingroomList")
    public String listMeetings(Model model) { 
    	List<MeetingDto> meetings = meetingService.getAllMeetings();
        model.addAttribute("meetings", meetings);
        
        System.out.println(meetings);
        return "/admin/meeting/meetingroomList";
    }  
}

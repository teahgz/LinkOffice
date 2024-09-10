package com.fiveLink.linkOffice.meeting.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.meeting.domain.Meeting;
import com.fiveLink.linkOffice.meeting.domain.MeetingDto;
import com.fiveLink.linkOffice.meeting.service.MeetingFileService;
import com.fiveLink.linkOffice.meeting.service.MeetingService; 

@Controller
public class MeetingController {
    
    private final MeetingService meetingService;
    private final MeetingFileService meetingFileService;

    @Autowired
    public MeetingController(MeetingService meetingService, MeetingFileService meetingFileService) {
        this.meetingService = meetingService;
        this.meetingFileService = meetingFileService;
    }
 
    @GetMapping("/meetingroomList")
    public String listMeetings(Model model) { 
    	List<MeetingDto> meetings = meetingService.getAllMeetings();
        model.addAttribute("meetings", meetings);
        
        System.out.println(meetings);
        return "/admin/meeting/meetingroomList";
    }   
 
 // 회의실 등록
    @PostMapping("/meetingroomList/add")
    @ResponseBody
    public Map<String, Object> addMeeting(
        @RequestParam("meetingName") String meetingName,
        @RequestParam("meetingMax") Long meetingMax,
        @RequestParam("meetingAvailableStart") String meetingAvailableStart,
        @RequestParam("meetingAvailableEnd") String meetingAvailableEnd,
        @RequestParam("meetingComment") String meetingComment,
        @RequestParam("meetingImage") MultipartFile meetingImage) {

        Map<String, Object> resultMap = new HashMap<>();

        try { 
            if (meetingService.isMeetingNameExists(meetingName)) {
                resultMap.put("res_code", "404");
                resultMap.put("res_msg", "동일한 회의실명이 존재합니다.");
                return resultMap;
            }
 
            String newImageName = meetingFileService.upload(meetingImage);
            if (newImageName == null) {
                resultMap.put("res_code", "404");
                resultMap.put("res_msg", "이미지 업로드 중 오류가 발생했습니다.");
                return resultMap;
            }
 
            MeetingDto meetingDto = MeetingDto.builder()
                .meeting_name(meetingName)
                .meeting_max(meetingMax)
                .meeting_available_start(meetingAvailableStart)
                .meeting_available_end(meetingAvailableEnd)
                .meeting_ori_image(meetingImage.getOriginalFilename())
                .meeting_new_image(newImageName)
                .meeting_comment(meetingComment)
                .meeting_status(0L) 
                .build();
 
            Meeting meeting = meetingService.saveMeeting(meetingDto);
            if (meeting != null) {
                resultMap.put("res_code", "200");
                resultMap.put("res_msg", "회의실 정보가 등록되었습니다.");
            } else {
                resultMap.put("res_code", "500");
                resultMap.put("res_msg", "회의실 정보 등록 중 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        } 
        return resultMap;
    }


}

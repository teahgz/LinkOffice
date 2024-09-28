package com.fiveLink.linkOffice.nofication.controller;

import com.fiveLink.linkOffice.nofication.domain.NoficationDto;
import com.fiveLink.linkOffice.nofication.service.NoficationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NoficationController {
    private final NoficationService noficationService;

    @Autowired
    public NoficationController(NoficationService noficationService){
        this.noficationService = noficationService;
    }

    //현재 사용자의 안읽음 개수 가져오기
    @GetMapping("/api/nofication/unread/{headerCurrentMember}")
    @ResponseBody
    public Map<String, Object> bellUnreadCount(@PathVariable("headerCurrentMember") Long headerCurrentMember) {
        System.out.println("memberNo :"+ headerCurrentMember);
        int unreadCount =  noficationService.bellCount(headerCurrentMember);

        Map<String, Object> response = new HashMap<>();

        response.put("unreadCount", unreadCount);
        return response;

    }

    //현재 사용자의 안읽음 개수 가져오기
    @GetMapping("/api/nofication/unread/list/{headerCurrentMember}")
    @ResponseBody
    public Map<String, Object> selectUnreadList(@PathVariable("headerCurrentMember") Long headerCurrentMember) {
        System.out.println("memberNo :"+ headerCurrentMember);
        List<NoficationDto> unreadList =  noficationService.selectUnreadList(headerCurrentMember);

        Map<String, Object> response = new HashMap<>();
        System.out.println("list: "+unreadList);
        response.put("unreadList", unreadList);

        return response;

    }
}

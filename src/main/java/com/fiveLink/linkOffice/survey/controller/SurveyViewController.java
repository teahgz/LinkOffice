package com.fiveLink.linkOffice.survey.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.notice.controller.NoticeViewController;
import com.fiveLink.linkOffice.survey.domain.SurveyDto;
import com.fiveLink.linkOffice.survey.service.SurveyService;

@Controller
public class SurveyViewController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeViewController.class);
    private final SurveyService surveyService;
    private final MemberService memberService;
    
    @Autowired
    public SurveyViewController(MemberService memberService, SurveyService surveyService) {
        this.memberService = memberService;
        this.surveyService = surveyService;
    }
    
    private Sort getSortOption(String sort) {
        if ("latest".equals(sort)) {
            return Sort.by(Sort.Order.desc("surveyStartDate"));
        } else if ("oldest".equals(sort)) {
            return Sort.by(Sort.Order.asc("surveyStartDate"));
        }
        return Sort.by(Sort.Order.desc("surveyStartDate"));
    }
    
    @GetMapping("/employee/survey/myList")
    public String surveyMyList(
        @PageableDefault(size = 10, sort = "surveyStartDate", direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(value = "sort", defaultValue = "latest") String sort,
        Model model,
        SurveyDto searchDto) {
        
        Long member_no = memberService.getLoggedInMemberNo();
        List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
        
        model.addAttribute("memberdto", memberdto);
        model.addAttribute("currentSort", sort);  
        
        Sort sortOption = getSortOption(sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

        Page<SurveyDto> surveyPage = surveyService.getAllSurveyPage(sortedPageable, searchDto);

        model.addAttribute("surveyList", surveyPage.getContent());
        model.addAttribute("page", surveyPage);
        model.addAttribute("searchDto", searchDto);
        return "employee/survey/survey_my_list";
    }

}

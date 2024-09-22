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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.notice.controller.NoticeViewController;
import com.fiveLink.linkOffice.survey.domain.SurveyDto;
import com.fiveLink.linkOffice.survey.domain.SurveyQuestionDto;
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
        
        Long memberNo = memberService.getLoggedInMemberNo();
        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
        
        model.addAttribute("memberdto", memberdto);
        model.addAttribute("currentSort", sort);  
        
        Sort sortOption = getSortOption(sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

        Page<SurveyDto> surveyPage = surveyService.getAllSurveyPage(sortedPageable, searchDto, memberNo);

        model.addAttribute("surveyList", surveyPage.getContent());
        model.addAttribute("page", surveyPage);
        model.addAttribute("searchDto", searchDto);
        return "employee/survey/survey_my_list";
    }
    
    @GetMapping("/employee/survey/endList")
    public String surveyEndList(
            @PageableDefault(size = 10, sort = "surveyStartDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            Model model,
            SurveyDto searchDto) {

        Long memberNo = memberService.getLoggedInMemberNo();
        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);

        model.addAttribute("memberdto", memberdto);
        model.addAttribute("currentSort", sort);

        Sort sortOption = getSortOption(sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

        Page<SurveyDto> surveyPage = surveyService.getEndAllSurveyPage(sortedPageable, searchDto, memberNo);

        model.addAttribute("surveyEndList", surveyPage.getContent());
        model.addAttribute("page", surveyPage);
        model.addAttribute("searchDto", searchDto);

        return "employee/survey/survey_end_list";
    }
    
    @GetMapping("/employee/survey/ingList")
    public String surveyIngList(
            @PageableDefault(size = 10, sort = "surveyStartDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            Model model,
            SurveyDto searchDto) {

        Long memberNo = memberService.getLoggedInMemberNo();
        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);

        model.addAttribute("memberdto", memberdto);
        model.addAttribute("currentSort", sort);

        Sort sortOption = getSortOption(sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

        Page<SurveyDto> surveyPage = surveyService.getIngAllSurveyPage(sortedPageable, searchDto, memberNo);

        model.addAttribute("surveyIngList", surveyPage.getContent());
        model.addAttribute("page", surveyPage);
        model.addAttribute("searchDto", searchDto);

        return "employee/survey/survey_ing_list";
    }
    
    @GetMapping("/employee/survey/detail/{survey_no}")
    public String selectSurveyOne(Model model, @PathVariable("survey_no") Long surveyNo) {
        SurveyDto dto = surveyService.selectSurveyOne(surveyNo);

        // 질문 및 선택지, 주관식 답변 가져오기
        List<SurveyQuestionDto> questions = surveyService.getSurveyQuestions(surveyNo);

        // 모델에 추가
        model.addAttribute("dto", dto);
        model.addAttribute("questions", questions);

        return "employee/survey/survey_question_detail";
    }

}

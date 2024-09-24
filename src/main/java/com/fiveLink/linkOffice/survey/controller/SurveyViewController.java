package com.fiveLink.linkOffice.survey.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        Long memberNo = memberService.getLoggedInMemberNo();
        SurveyDto dto = surveyService.selectSurveyOne(surveyNo);
        List<SurveyQuestionDto> questions = surveyService.getSurveyQuestions(surveyNo);

        boolean hasParticipated = surveyService.hasUserParticipated(surveyNo, memberNo);
        
        model.addAttribute("dto", dto);
        model.addAttribute("questions", questions);

        if (hasParticipated) {
            int totalParticipants = surveyService.getTotalParticipants(surveyNo);
            int completedParticipants = surveyService.getCompletedParticipants(surveyNo);
            int notParticipatedParticipants = totalParticipants - completedParticipants;

            // 설문 참여율 계산 및 추가
            Map<Long, Integer> participationRates = surveyService.calculateParticipationRates(questions, totalParticipants);
            model.addAttribute("participationRates", participationRates);

            // 선택지별 응답 개수 추가
            Map<Long, List<Object[]>> optionAnswerCounts = surveyService.getOptionAnswerCountsBySurvey(surveyNo);
            model.addAttribute("optionAnswerCounts", optionAnswerCounts);

            model.addAttribute("totalParticipants", totalParticipants);
            model.addAttribute("completedParticipants", completedParticipants);
            model.addAttribute("notParticipatedParticipants", notParticipatedParticipants);

            return "employee/survey/survey_question_result"; 
        } else {
            return "employee/survey/survey_question_detail";
        }
    }
    
    
    @GetMapping("/employee/survey/chartView")
    @ResponseBody
    public List<Map<String, Object>> getChartData() {
        List<Map<String, Object>> chartData = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("label", "Yes");
        data1.put("value", 60);
        chartData.add(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("label", "No");
        data2.put("value", 40);
        chartData.add(data2);

        return chartData; // JSON 데이터 반환
    }







}
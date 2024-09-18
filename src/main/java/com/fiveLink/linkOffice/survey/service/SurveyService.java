package com.fiveLink.linkOffice.survey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.notice.controller.NoticeApiController;
import com.fiveLink.linkOffice.survey.domain.Survey;
import com.fiveLink.linkOffice.survey.domain.SurveyDto;
import com.fiveLink.linkOffice.survey.repository.SurveyRepository;

@Service
public class SurveyService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeApiController.class);
    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;
    
    @Autowired
    public SurveyService(SurveyRepository surveyRepository, MemberRepository memberRepository) {
        this.surveyRepository = surveyRepository;
        this.memberRepository = memberRepository;
    }
    
    public Page<SurveyDto> getAllSurveyPage(Pageable pageable, SurveyDto searchDto) {
        Page<Survey> results = null; // Page<Survey>로 수정

        String searchText = searchDto.getSearch_text();
        if (searchText != null && !searchText.isEmpty()) {
            int searchType = searchDto.getSearch_type();
            switch (searchType) {
                // 제목 또는 내용 검색
                case 1:
                    results = surveyRepository.findSurveyByTitleOrContent(searchText, pageable);
                    break;
                // 제목 검색
                case 2:
                    results = surveyRepository.findSurveyByTitle(searchText, pageable);
                    break;
                // 내용 검색
                case 3:
                    results = surveyRepository.findSurveyByDescription(searchText, pageable);
                    break;
            }
        } else {
            results = surveyRepository.findSurveyAll(pageable);
        }

        List<SurveyDto> surveyDtoList = convertToDtoList(results.getContent()); 
        return new PageImpl<>(surveyDtoList, pageable, results.getTotalElements());
    }
    
    // Object[] 대신 Survey 객체를 직접 처리하도록 변경
    private List<SurveyDto> convertToDtoList(List<Survey> surveys) {
        return surveys.stream().map(survey -> {
            return SurveyDto.builder()
                    .survey_no(survey.getSurveyNo())
                    .survey_title(survey.getSurveyTitle())
                    .survey_start_date(survey.getSurveyStartDate())
                    .survey_end_date(survey.getSurveyEndDate())
                    .survey_status(survey.getSurveyStatus())
                    .member_name(survey.getMember().getMemberName()) 
                    .build();
        }).collect(Collectors.toList());
    }
}

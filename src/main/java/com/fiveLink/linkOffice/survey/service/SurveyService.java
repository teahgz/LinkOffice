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
import com.fiveLink.linkOffice.survey.domain.SurveyParticipant;
import com.fiveLink.linkOffice.survey.domain.SurveyParticipantDto;
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
    
    private List<SurveyDto> convertToDtoList(List<Survey> surveys) {
        return surveys.stream().map(survey -> {
            Integer participantStatus = survey.getSurveyParticipant().isEmpty() ? null
                    : survey.getSurveyParticipant().get(0).getSurveyParticipantStatus();
            
            return SurveyDto.builder()
                    .survey_no(survey.getSurveyNo())
                    .survey_title(survey.getSurveyTitle())
                    .survey_start_date(survey.getSurveyStartDate())
                    .survey_end_date(survey.getSurveyEndDate())
                    .survey_status(survey.getSurveyStatus())
                    .member_name(survey.getMember().getMemberName())
                    .survey_participant_status(participantStatus) 
                    .build();
        }).collect(Collectors.toList());
    }

    
    public Page<SurveyDto> getAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Survey> results = null;

        String searchText = searchDto.getSearch_text();
        if (searchText != null && !searchText.isEmpty()) {
            int searchType = searchDto.getSearch_type();
            switch (searchType) {
                // 제목 또는 내용 검색
                case 1:
                    results = surveyRepository.findSurveyByTitleOrContent(searchText, memberNo, pageable);
                    break;
                // 제목 검색
                case 2:
                    results = surveyRepository.findSurveyByTitle(searchText, memberNo, pageable);
                    break;
                // 내용 검색
                case 3:
                    results = surveyRepository.findSurveyByDescription(searchText, memberNo, pageable);
                    break;
            }
        } else {
            results = surveyRepository.findSurveyAll(memberNo, pageable);
        }
        List<Survey> filteredResults = results.getContent().stream()
            .filter(survey -> survey.getSurveyStatus() == 0 || survey.getSurveyStatus() == 1)
            .collect(Collectors.toList());

        List<SurveyDto> surveyDtoList = convertToDtoList(filteredResults); 
        return new PageImpl<>(surveyDtoList, pageable, filteredResults.size());
    }
    
    
    // 마감된 설문조사 목록을 가져오는 메서드
    public Page<SurveyDto> getEndAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Survey> results = null;

        String searchText = searchDto.getSearch_text();
        if (searchText != null && !searchText.isEmpty()) {
            int searchType = searchDto.getSearch_type();
            switch (searchType) {
                case 1:
                    results = surveyRepository.findSurveyByTitleOrContentForEndList(searchText, memberNo, pageable);
                    break;
                case 2:
                    results = surveyRepository.findSurveyByTitleForEndList(searchText, memberNo, pageable);
                    break;
                case 3:
                    results = surveyRepository.findSurveyByDescriptionForEndList(searchText, memberNo, pageable);
                    break;
                case 4:
                    results = surveyRepository.findSurveyByAuthorForEndList(searchText, memberNo, pageable);
                    break;
            }
        } else {
            results = surveyRepository.findAllEndedSurveysForMember(memberNo, pageable);
        }

        List<SurveyDto> surveyDtoList = convertToDtoList(results.getContent());
        return new PageImpl<>(surveyDtoList, pageable, results.getTotalElements());
    }
    
    // 진행중인 설문조사 목록을 가져오는 메서드
    public Page<SurveyDto> getIngAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Survey> results = null;

        String searchText = searchDto.getSearch_text();
        if (searchText != null && !searchText.isEmpty()) {
            int searchType = searchDto.getSearch_type();
            switch (searchType) {
                case 1:
                    results = surveyRepository.findSurveyByTitleOrContentForIngList(searchText, memberNo, pageable);
                    break;
                case 2:
                    results = surveyRepository.findSurveyByTitleForIngList(searchText, memberNo, pageable);
                    break;
                case 3:
                    results = surveyRepository.findSurveyByDescriptionForIngList(searchText, memberNo, pageable);
                    break;
                case 4:
                    results = surveyRepository.findSurveyByAuthorForIngList(searchText, memberNo, pageable);
                    break;
            }
        } else {
            results = surveyRepository.findAllOngoingSurveysForMember(memberNo, pageable);
        }

        List<SurveyDto> surveyDtoList = convertToDtoList(results.getContent());
        return new PageImpl<>(surveyDtoList, pageable, results.getTotalElements());
    }
}
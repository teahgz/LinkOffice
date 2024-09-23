package com.fiveLink.linkOffice.survey.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.fiveLink.linkOffice.survey.domain.SurveyQuestion;
import com.fiveLink.linkOffice.survey.domain.SurveyQuestionDto;
import com.fiveLink.linkOffice.survey.repository.SurveyOptionRepository;
import com.fiveLink.linkOffice.survey.repository.SurveyParticipantRepository;
import com.fiveLink.linkOffice.survey.repository.SurveyQuestionRepository;
import com.fiveLink.linkOffice.survey.repository.SurveyRepository;
import com.fiveLink.linkOffice.survey.repository.SurveyTextRepository;

@Service
public class SurveyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeApiController.class);
    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;
    private final SurveyOptionRepository surveyOptionRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyTextRepository surveyTextRepository;
    private final SurveyParticipantRepository surveyParticipantRepository;
    @Autowired
    public SurveyService(SurveyRepository surveyRepository, MemberRepository memberRepository, SurveyOptionRepository surveyOptionRepository,
    		SurveyQuestionRepository surveyQuestionRepository, SurveyTextRepository surveyTextRepository, SurveyParticipantRepository surveyParticipantRepository) {
        this.surveyRepository = surveyRepository;
        this.memberRepository = memberRepository;
        this.surveyOptionRepository = surveyOptionRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyTextRepository = surveyTextRepository;
        this.surveyParticipantRepository = surveyParticipantRepository;
    }
    
    private List<SurveyDto> convertToDtoList(List<Object[]> surveys) {
        return surveys.stream().map(objects -> {
            Survey survey = (Survey) objects[0]; // 첫 번째 요소는 Survey 객체
            Integer participantStatus = (Integer) objects[1]; // 두 번째 요소는 참여 상태 (surveyParticipantStatus)

            return SurveyDto.builder()
                    .survey_no(survey.getSurveyNo())
                    .survey_title(survey.getSurveyTitle())
                    .survey_start_date(survey.getSurveyStartDate())
                    .survey_end_date(survey.getSurveyEndDate())
                    .survey_status(survey.getSurveyStatus())
                    .member_name(survey.getMember().getMemberName())
                    .survey_participant_status(participantStatus) // 설문 참여 상태 추가
                    .build();
        }).collect(Collectors.toList());
    }

    
    public Page<SurveyDto> getAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Object[]> results = null;  // Survey와 surveyParticipantStatus를 함께 받음

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
            results = surveyRepository.findSurveyAll(memberNo, pageable);  // 전체 조회
        }

        // filter에서는 survey의 상태를 확인해야 하므로 Object[]에서 Survey로 변환
        List<Object[]> filteredResults = results.getContent().stream()
            .filter(objArr -> {
                Survey survey = (Survey) objArr[0];  // 첫 번째 값은 Survey 객체로 캐스팅
                return survey.getSurveyStatus() == 0 || survey.getSurveyStatus() == 1;  // 상태 필터링
            })
            .collect(Collectors.toList());

        // SurveyDto로 변환하는 단계
        List<SurveyDto> surveyDtoList = convertToDtoList(filteredResults); 
        return new PageImpl<>(surveyDtoList, pageable, results.getTotalElements());
    }

    
    
    // 마감된 설문조사 목록을 가져오는 메서드
 // 마감된 설문조사 목록을 가져오는 메서드
    public Page<SurveyDto> getEndAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Object[]> results = null;  // Object[] 타입으로 변경

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
        Page<Object[]> results = null; // Page<Object[]>로 변경
        String searchText = searchDto.getSearch_text();
        System.out.println(memberNo);
        
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
    
    public SurveyDto selectSurveyOne(Long surveyNo) {
        Survey survey = surveyRepository.findBysurveyNo(surveyNo);
        return SurveyDto.builder()
            .survey_no(survey.getSurveyNo())
            .survey_title(survey.getSurveyTitle())
            .survey_create_date(survey.getSurveyCreateDate())
            .survey_start_date(survey.getSurveyStartDate())
            .survey_end_date(survey.getSurveyEndDate())
            .survey_status(survey.getSurveyStatus())
            .survey_description(survey.getSurveyDescription())
            .member_name(survey.getMember().getMemberName())
            .build();
    }

    public List<SurveyQuestionDto> getSurveyQuestions(Long surveyNo) {
        List<SurveyQuestion> questions = surveyQuestionRepository.findBySurveyNo(surveyNo);
        return questions.stream().map(question -> SurveyQuestionDto.builder()
            .survey_question_no(question.getSurveyQuestionNo())
            .survey_no(question.getSurvey().getSurveyNo())
            .survey_question_text(question.getSurveyQuestionText())
            .survey_question_type(question.getSurveyQuestionType())
            .survey_question_essential(question.getSurveyQuestionEssential())
            .build()).collect(Collectors.toList());
    }

    public boolean hasUserParticipated(Long surveyNo, Long memberNo) {
        SurveyParticipant participant = surveyParticipantRepository.findBySurveyNoAndMemberNo(surveyNo, memberNo);
        return participant != null && participant.getSurveyParticipantStatus() == 1;
    }

    // 설문 전체 참여자 수
    public int getTotalParticipants(Long surveyNo) {
        return surveyParticipantRepository.countBySurveyNo(surveyNo);
    }

    // 설문 참여 완료자 수
    public int getCompletedParticipants(Long surveyNo) {
        return surveyParticipantRepository.countCompletedBySurveyNo(surveyNo);
    }

    // 질문별 참여자 수 계산
    public int getParticipantsByQuestion(Long questionNo) {
        return surveyParticipantRepository.countByQuestionAndStatus(questionNo);
    }

    // 설문 질문 및 통계 데이터를 처리하는 메소드
    public Map<Long, Integer> calculateParticipationRates(List<SurveyQuestionDto> questions, int totalParticipants) {
        Map<Long, Integer> participationRates = new HashMap<>();

        for (SurveyQuestionDto question : questions) {
            int participantsForQuestion = getParticipantsByQuestion(question.getSurvey_question_no());
            int participationRate = (int) ((participantsForQuestion / (double) totalParticipants) * 100);
            participationRates.put(question.getSurvey_question_no(), participationRate);
        }

        return participationRates;
    }
}
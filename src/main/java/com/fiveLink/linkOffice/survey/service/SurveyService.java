package com.fiveLink.linkOffice.survey.service;

import java.util.ArrayList;
import java.util.Arrays;
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
                         SurveyQuestionRepository surveyQuestionRepository, SurveyTextRepository surveyTextRepository, 
                         SurveyParticipantRepository surveyParticipantRepository) {
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
                case 1:
                    results = surveyRepository.findSurveyByTitleOrContent(searchText, memberNo, pageable);
                    break;
                case 2:
                    results = surveyRepository.findSurveyByTitle(searchText, memberNo, pageable);
                    break;
                case 3:
                    results = surveyRepository.findSurveyByDescription(searchText, memberNo, pageable);
                    break;
            }
        } else {
            results = surveyRepository.findSurveyAll(memberNo, pageable);  // 전체 조회
        }

        List<Object[]> filteredResults = results.getContent().stream()
            .filter(objArr -> {
                Survey survey = (Survey) objArr[0];
                return survey.getSurveyStatus() == 0 || survey.getSurveyStatus() == 1;
            })
            .collect(Collectors.toList());

        List<SurveyDto> surveyDtoList = convertToDtoList(filteredResults); 
        return new PageImpl<>(surveyDtoList, pageable, results.getTotalElements());
    }

    public Page<SurveyDto> getEndAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Object[]> results = null; 

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

    public Page<SurveyDto> getIngAllSurveyPage(Pageable pageable, SurveyDto searchDto, Long memberNo) {
        Page<Object[]> results = null; 
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

    // 선택지 답변 포함 설문 질문 조회
    public List<SurveyQuestionDto> getSurveyQuestions(Long surveyNo) {
        List<SurveyQuestion> questions = surveyQuestionRepository.findBySurveyNo(surveyNo);
        return questions.stream().map(question -> {
            // 선택지 번호 가져오기
            List<Long> optionNo = surveyOptionRepository.findByQuestionNo(question.getSurveyQuestionNo())
                    .stream()
                    .map(option -> option.getSurveyOptionNo())
                    .collect(Collectors.toList());

            // 선택지 답변 가져오기
            List<String> optionAnswers = surveyOptionRepository.findByQuestionNo(question.getSurveyQuestionNo())
                    .stream()
                    .map(option -> option.getSurveyOptionAnswer())
                    .collect(Collectors.toList());

            // 주관식 텍스트 번호 가져오기
            List<Long> textNo = surveyTextRepository.findByQuestionNo(question.getSurveyQuestionNo())
                    .stream()
                    .map(text -> text.getSurveyTextNo())
                    .collect(Collectors.toList());

            return SurveyQuestionDto.builder()
                    .survey_question_no(question.getSurveyQuestionNo())
                    .survey_no(question.getSurvey().getSurveyNo())
                    .survey_question_text(question.getSurveyQuestionText())
                    .survey_question_type(question.getSurveyQuestionType())
                    .survey_question_essential(question.getSurveyQuestionEssential())
                    .survey_option_no(optionNo)  // 선택지 번호 추가
                    .survey_option_answer(optionAnswers)  // 선택지 답변 추가
                    .survey_text_no(textNo)  // 주관식 텍스트 번호 추가
                    .build();
        }).collect(Collectors.toList());
    }

    public boolean hasUserParticipated(Long surveyNo, Long memberNo) {
        SurveyParticipant participant = surveyParticipantRepository.findBySurveyNoAndMemberNo(surveyNo, memberNo);
        return participant != null && participant.getSurveyParticipantStatus() == 1;
    }

    public int getTotalParticipants(Long surveyNo) {
        return surveyParticipantRepository.countBySurveyNo(surveyNo);
    }

    public int getCompletedParticipants(Long surveyNo) {
        return surveyParticipantRepository.countCompletedBySurveyNo(surveyNo);
    }

    public int getParticipantsByQuestion(Long questionNo) {
        return surveyParticipantRepository.countByQuestionAndStatus(questionNo);
    }

    public Map<Long, Integer> calculateParticipationRates(List<SurveyQuestionDto> questions, int totalParticipants) {
        Map<Long, Integer> participationRates = new HashMap<>();

        if (totalParticipants == 0) {
            // 참여자가 없을 때 참여율을 0%로 처리
            for (SurveyQuestionDto question : questions) {
                participationRates.put(question.getSurvey_question_no(), 0);
            }
        } else {
            for (SurveyQuestionDto question : questions) {
                int participantsForQuestion = getParticipantsByQuestion(question.getSurvey_question_no());
                int participationRate = (int) ((participantsForQuestion / (double) totalParticipants) * 100);
                participationRates.put(question.getSurvey_question_no(), participationRate);
            }
        }

        return participationRates;
    }
    
 // 설문에 대한 옵션별 응답 수 계산
    public Map<Long, List<Object[]>> getOptionAnswerCountsBySurvey(Long surveyNo) {
        LOGGER.info("옵션별 응답 수 계산 시작 - 설문 번호: {}", surveyNo);

        // 설문에 대한 옵션별 응답 수를 레포지토리에서 조회
        List<Object[]> result = surveyOptionRepository.countAnswersByOptionWithAnswer(surveyNo);

        // 각 행의 데이터를 읽기 쉽게 출력
        for (Object[] row : result) {
            LOGGER.info("조회된 데이터 - 질문 번호: {}, 옵션: {}, 응답 수: {}", row[0], row[1], row[2]);
        }

        // 각 질문별로 응답 수를 정리하여 맵으로 저장
        Map<Long, List<Object[]>> optionAnswerCounts = new HashMap<>();
        for (Object[] row : result) {
            Long questionNo = (Long) row[0];
            String optionAnswer = (String) row[1];
            Long answerCount = (Long) row[2];
            optionAnswerCounts.computeIfAbsent(questionNo, k -> new ArrayList<>())
                              .add(new Object[]{optionAnswer, answerCount});
        }

        // 정리된 데이터의 내용을 쉽게 확인할 수 있도록 로그로 출력
        optionAnswerCounts.forEach((questionNo, options) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("질문 번호: ").append(questionNo).append(" -> ");
            options.forEach(option -> sb.append(Arrays.toString(option)).append(" "));
            LOGGER.info("정리된 옵션 응답 수 데이터: {}", sb.toString());
        });

        return optionAnswerCounts;
    }



}

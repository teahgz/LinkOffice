package com.fiveLink.linkOffice.survey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.notice.domain.Notice;
import com.fiveLink.linkOffice.survey.domain.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    // 검색어(조건, 제목 또는 내용) + 로그인한 사용자의 설문조사만 조회
    @Query("SELECT s FROM Survey s WHERE (s.surveyTitle LIKE %:searchText% OR s.surveyDescription LIKE %:searchText%) AND s.member.memberNo = :memberNo")
    Page<Survey> findSurveyByTitleOrContent(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 검색어(조건, 제목) + 로그인한 사용자의 설문조사만 조회
    @Query("SELECT s FROM Survey s WHERE s.surveyTitle LIKE %:searchText% AND s.member.memberNo = :memberNo")
    Page<Survey> findSurveyByTitle(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 검색어(조건, 내용) + 로그인한 사용자의 설문조사만 조회
    @Query("SELECT s FROM Survey s WHERE s.surveyDescription LIKE %:searchText% AND s.member.memberNo = :memberNo")
    Page<Survey> findSurveyByDescription(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 전체 조회 + 로그인한 사용자의 설문조사만 조회
    @Query("SELECT s FROM Survey s WHERE s.member.memberNo = :memberNo")
    Page<Survey> findSurveyAll(@Param("memberNo") Long memberNo, Pageable pageable);
    
    
    // 제목 또는 내용 또는 작성자 검색 + 로그인한 사용자가 참여한 설문조사만 조회 (마감된 설문만)
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "JOIN Member m ON s.member.memberNo = m.memberNo " +  // Member 조인 추가
           "WHERE (s.surveyTitle LIKE %:searchText% OR s.surveyDescription LIKE %:searchText% OR m.memberName LIKE %:searchText%) " + 
           "AND sp.member.memberNo = :memberNo AND s.surveyStatus = 1")
    Page<Survey> findSurveyByTitleOrContentForEndList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 제목 검색 + 로그인한 사용자가 참여한 마감된 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "WHERE s.surveyTitle LIKE %:searchText% AND sp.member.memberNo = :memberNo AND s.surveyStatus = 1")
    Page<Survey> findSurveyByTitleForEndList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 내용 검색 + 로그인한 사용자가 참여한 마감된 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "WHERE s.surveyDescription LIKE %:searchText% AND sp.member.memberNo = :memberNo AND s.surveyStatus = 1")
    Page<Survey> findSurveyByDescriptionForEndList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 작성자 검색 + 로그인한 사용자가 참여한 마감된 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "JOIN Member m ON s.member.memberNo = m.memberNo " +
           "WHERE m.memberName LIKE %:searchText% AND sp.member.memberNo = :memberNo AND s.surveyStatus = 1")
    Page<Survey> findSurveyByAuthorForEndList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 전체 조회 + 로그인한 사용자가 참여한 마감된 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "WHERE sp.member.memberNo = :memberNo AND s.surveyStatus = 1")
    Page<Survey> findAllEndedSurveysForMember(@Param("memberNo") Long memberNo, Pageable pageable);
    
    
    // 제목 또는 내용 또는 작성자 검색 + 로그인한 사용자가 참여한 설문조사만 조회 (진행 중인 설문만)
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "JOIN Member m ON s.member.memberNo = m.memberNo " +  
           "WHERE (s.surveyTitle LIKE %:searchText% OR s.surveyDescription LIKE %:searchText% OR m.memberName LIKE %:searchText%) " + 
           "AND sp.member.memberNo = :memberNo AND s.surveyStatus = 0")
    Page<Survey> findSurveyByTitleOrContentForIngList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 제목 검색 + 로그인한 사용자가 참여한 진행 중인 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "WHERE s.surveyTitle LIKE %:searchText% AND sp.member.memberNo = :memberNo AND s.surveyStatus = 0")
    Page<Survey> findSurveyByTitleForIngList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 내용 검색 + 로그인한 사용자가 참여한 진행 중인 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "WHERE s.surveyDescription LIKE %:searchText% AND sp.member.memberNo = :memberNo AND s.surveyStatus = 0")
    Page<Survey> findSurveyByDescriptionForIngList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 작성자 검색 + 로그인한 사용자가 참여한 진행 중인 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "JOIN Member m ON s.member.memberNo = m.memberNo " +
           "WHERE m.memberName LIKE %:searchText% AND sp.member.memberNo = :memberNo AND s.surveyStatus = 0")
    Page<Survey> findSurveyByAuthorForIngList(@Param("searchText") String searchText, @Param("memberNo") Long memberNo, Pageable pageable);

    // 전체 조회 + 로그인한 사용자가 참여한 진행 중인 설문조사만 조회
    @Query("SELECT s FROM Survey s JOIN SurveyParticipant sp ON s.surveyNo = sp.survey.surveyNo " +
           "WHERE sp.member.memberNo = :memberNo AND s.surveyStatus = 0")
    Page<Survey> findAllOngoingSurveysForMember(@Param("memberNo") Long memberNo, Pageable pageable);
    
    Survey findBysurveyNo(Long survey_no);

}
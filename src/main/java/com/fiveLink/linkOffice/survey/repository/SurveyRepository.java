package com.fiveLink.linkOffice.survey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.survey.domain.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
	
	// 검색어(조건, 제목 또는 내용) 쿼리
	@Query("SELECT s FROM Survey s WHERE s.surveyTitle LIKE %:searchText% OR s.surveyDescription LIKE %:searchText%")
	Page<Survey> findSurveyByTitleOrContent(@Param("searchText") String searchText, Pageable pageable);

	// 검색어(조건, 제목) 쿼리
	@Query("SELECT s FROM Survey s WHERE s.surveyTitle LIKE %:searchText%")
	Page<Survey> findSurveyByTitle(@Param("searchText") String searchText, Pageable pageable);

	// 검색어(조건, 내용) 쿼리
	@Query("SELECT s FROM Survey s WHERE s.surveyDescription LIKE %:searchText%")
	Page<Survey> findSurveyByDescription(@Param("searchText") String searchText, Pageable pageable);

	// 전체 조회
	@Query("SELECT s FROM Survey s")
	Page<Survey> findSurveyAll(Pageable pageable);
}

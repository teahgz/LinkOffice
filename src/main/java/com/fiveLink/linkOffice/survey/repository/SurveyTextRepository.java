package com.fiveLink.linkOffice.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fiveLink.linkOffice.survey.domain.SurveyText;
import java.util.List;

@Repository
public interface SurveyTextRepository extends JpaRepository<SurveyText, Long> {

    @Query("SELECT t FROM SurveyText t WHERE t.surveyQuestion.surveyQuestionNo = :surveyQuestionNo")
    List<SurveyText> findByQuestionNo(@Param("surveyQuestionNo") Long surveyQuestionNo);
}
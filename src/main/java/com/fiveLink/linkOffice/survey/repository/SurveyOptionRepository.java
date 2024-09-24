package com.fiveLink.linkOffice.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fiveLink.linkOffice.survey.domain.SurveyOption;
import java.util.List;

@Repository
public interface SurveyOptionRepository extends JpaRepository<SurveyOption, Long> {

    @Query("SELECT o FROM SurveyOption o WHERE o.surveyQuestion.surveyQuestionNo = :surveyQuestionNo")
    List<SurveyOption> findByQuestionNo(@Param("surveyQuestionNo") Long surveyQuestionNo);
}
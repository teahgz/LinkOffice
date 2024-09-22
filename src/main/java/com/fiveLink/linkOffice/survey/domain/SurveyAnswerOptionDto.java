package com.fiveLink.linkOffice.survey.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SurveyAnswerOptionDto {
	private Long survey_answer_option_no;
	private Long survey_participant_no;
	private Long survey_option_no;
	
	public SurveyAnswerOption toEntity() {
		return SurveyAnswerOption.builder()
				.surveyAnswerOptionNo(survey_answer_option_no)
		        .build();			
	}
	
	public SurveyAnswerOptionDto toDto(SurveyAnswerOption surveyAnswerOption) {
		return SurveyAnswerOptionDto.builder()
				.survey_answer_option_no(surveyAnswerOption.getSurveyAnswerOptionNo())
				.build();
	}
}

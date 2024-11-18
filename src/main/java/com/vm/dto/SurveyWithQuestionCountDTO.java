package com.vm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyWithQuestionCountDTO {
    private Integer surveyId;
    private String title;
    private String description;
    private Integer priority;
    private Long questionCount;

    public SurveyWithQuestionCountDTO(Integer surveyId, String title, String description, Integer priority, Long questionCount) {
        this.surveyId = surveyId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.questionCount = questionCount;
    }
}

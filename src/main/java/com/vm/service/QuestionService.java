package com.vm.service;

import com.vm.request.NewQuestionObject;
import com.vm.request.QuestionObject;

import java.util.List;

public interface QuestionService {
	public List<QuestionObject> getQuestionBySurveyId(Integer survey_id);
	public List<NewQuestionObject> getQuestionWithNewFormatBySurveyId(Integer survey_id);
	public QuestionObject getQuestionById(Long question_id);
}

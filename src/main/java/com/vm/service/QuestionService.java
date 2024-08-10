package com.vm.service;

import com.vm.request.NewQuestionObject;
import com.vm.request.QuestionObject;

import java.util.List;

public interface QuestionService {
	public List<QuestionObject> getQuestionBySurveyId(Long survey_id);
	public List<NewQuestionObject> getQuestionWithNewFormatBySurveyId(Long survey_id);
	public QuestionObject getQuestionById(Long question_id);
}

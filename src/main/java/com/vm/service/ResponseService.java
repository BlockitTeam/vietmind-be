package com.vm.service;

import com.vm.model.Response;
import com.vm.request.QuestionObject;

import java.util.List;
import java.util.Map;

public interface ResponseService {
//	public List<QuestionObject> getQuestionBySurveyId(Long survey_id);
//	public QuestionObject getQuestionById(Long question_id);
	public List<Response> getResponseBySurveyId(Long survey_id);
	public void saveResponse(List<QuestionObject> request) ;
	public Map<String, String> getResult(String userId);

	public Map<String, String> getResultDetail(String userId);

//	public void deleteResponses(Long survey_id);

	public void deleteResponses(String userId);
}

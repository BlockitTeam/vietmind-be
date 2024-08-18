package com.vm.service.impl;

import com.vm.model.Response;
import com.vm.repo.OptionRepository;
import com.vm.repo.ResponseRepository;
import com.vm.request.QuestionObject;
import com.vm.service.ResponseService;
import com.vm.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResponseServiceImpl implements ResponseService {
	@Autowired
	private ResponseRepository responseRepo;

	@Autowired
	private OptionRepository optionRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserService userService;

	private static final int MAX_SCORE = 10;

	@Override
	public List<Response> getResponseBySurveyId(Long survey_id) {
		return responseRepo.getResponseBySurveyId(survey_id);
	}

	@Override
	public List<Response> saveResponse(List<QuestionObject> request) {
		List<Response> responses = new ArrayList<>();
		for (QuestionObject ele : request) {
			Response response = new Response();
			response.setSurveyId(ele.getSurveyId());
			Object answer = ele.getAnswer();
			if (answer instanceof Number)
				response.setOptionId(((Number) answer).longValue());
			response.setUserId(userService.getStringCurrentUserId());
			responses.add(response);
		}
		return (List<Response>) responseRepo.saveAll(responses);
	}

	@Override
	public Map<String, String> getResult(String userId) {
		List<Map<String, Object>> result = responseRepo.getAggregatedResponses(userId);
		Map<String, Integer> aggregatedScores = result.stream()
				.collect(Collectors.groupingBy(
						map -> (String) map.get("questionType"),
						Collectors.summingInt(map -> (Integer) map.get("score"))
				));

		// Remove "Other" and sort by value in descending order
		Map<String, Integer> sortedScores = aggregatedScores.entrySet().stream()
				.filter(entry -> !entry.getKey().equals("Other"))
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1, // If there are duplicates, keep the existing entry
						LinkedHashMap::new // Preserve the order of insertion
				));

		// Convert to desired format and maintain order
		Map<String, String> formattedScores = sortedScores.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> entry.getValue() + "/" + MAX_SCORE,
						(e1, e2) -> e1,
						LinkedHashMap::new
				));
		return formattedScores;
	}

	@Override
	public Map<String, String> getResultDetail(String userId) {
//		List<Response> response = responseRepo.getResponseBySurveyId(survey_id);

		return null;
	}

	@Override
	public void deleteResponses(Long survey_id) {
		responseRepo.deleteAllByEmployeeIdIn(survey_id);
	}

//    @Override
//	public List<QuestionObject> getQuestionBySurveyId(Long survey_id) {
//		List<Question> questions = questionRepo.getQuestionBySurveyId(survey_id);;
//		List<QuestionObject> result = new ArrayList<>();
//		for (Question ele : questions) {
//			Long questionId = ele.getQuestionId();
//			QuestionObject questionMap = modelMapper.map(ele, QuestionObject.class);
//			List<Option> options = optionRepo.getOptionsByQuestionId(questionId);
//			questionMap.setOptions(options);
//			result.add(questionMap);
//		}
//		return result;
//	}
//
//	@Override
//	public QuestionObject getQuestionById(Long question_id) {
//		Question question = questionRepo.findById(question_id).get();
//		QuestionObject questionMap = modelMapper.map(question, QuestionObject.class);
//		List<Option> options = optionRepo.getOptionsByQuestionId(question_id);
//		questionMap.setOptions(options);
//		return questionMap;
//	}
}

package com.vm.service.impl;

import com.vm.model.Response;
import com.vm.repo.OptionRepository;
import com.vm.repo.ResponseRepository;
import com.vm.repo.SurveyRepository;
import com.vm.request.QuestionObject;
import com.vm.service.ResponseService;
import com.vm.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResponseServiceImpl implements ResponseService {
	@Autowired
	private ResponseRepository responseRepo;

	@Autowired
	private OptionRepository optionRepo;

	@Autowired
	private SurveyRepository surveyRepository;

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
	public void saveResponse(List<QuestionObject> request) {
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
		responseRepo.saveAll(responses);

		//Base on save general -> find survey detail
		int surveyDetailId = logicFindSurveyDetailIdMatched(userService.getStringCurrentUserId());
		userService.markSurveyDetailId(surveyDetailId);
	}

	public int logicFindSurveyDetailIdMatched(String userId) {
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

		Map<String, Integer> surveyWithPriorityMap = getTitleAndPriorityMap();
		String resultSurveyDetail = findHighestScoreKey(surveyWithPriorityMap, sortedScores);
		return surveyRepository.findSurveyIdByTitle(resultSurveyDetail);
	}

	public Map<String, Integer> getTitleAndPriorityMap() {
		List<Object[]> resultList = surveyRepository.findTitleAndPriority();
		Map<String, Integer> titlePriorityMap = new HashMap<>();

		for (Object[] result : resultList) {
			String title = (String) result[0];
			Integer priority = (Integer) result[1];
			titlePriorityMap.put(title, priority);
		}

		return titlePriorityMap;
	}

	public static String findHighestScoreKey(Map<String, Integer> surveyWithPriorityMap, Map<String, Integer> sortedScores) {
		// Bước 1: Tìm giá trị điểm cao nhất trong sortedScores
		int highestScore = Collections.max(sortedScores.values());

		// Bước 2: Tìm tất cả các key có điểm cao nhất
		List<String> candidates = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : sortedScores.entrySet()) {
			if (entry.getValue() == highestScore) {
				candidates.add(entry.getKey());
			}
		}

		// Bước 3: Nếu chỉ có 1 ứng viên, return luôn
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		// Bước 4: Tìm trong các ứng viên key có priority cao nhất từ surveyWithPriorityMap
		String highestPriorityKey = null;
		int highestPriority = Integer.MIN_VALUE;

		for (String key : candidates) {
			Integer priority = surveyWithPriorityMap.get(key);

			// Xử lý trường hợp priority là null (mặc định coi priority = Integer.MIN_VALUE)
			if (priority == null) {
				priority = Integer.MIN_VALUE;
			}

			// Kiểm tra nếu priority lớn hơn thì cập nhật kết quả
			if (priority > highestPriority) {
				highestPriorityKey = key;
				highestPriority = priority;
			}
		}

		return highestPriorityKey;  // Trả về key có priority cao nhất
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

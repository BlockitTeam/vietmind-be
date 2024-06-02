package com.vm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vm.model.Option;
import com.vm.model.Question;
import com.vm.repo.OptionRepository;
import com.vm.repo.QuestionRepository;
import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
	@Autowired
	private QuestionRepository questionRepo;

	@Autowired
	private OptionRepository optionRepo;

	@Autowired
	private ModelMapper modelMapper;

    @Override
	public List<QuestionObject> getQuestionBySurveyId(Long survey_id) {
		List<Question> questions = questionRepo.getQuestionBySurveyId(survey_id);;
		List<QuestionObject> result = new ArrayList<>();
		for (Question ele : questions) {
			Long questionId = ele.getQuestionId();
			QuestionObject questionMap = modelMapper.map(ele, QuestionObject.class);
			List<Option> options = optionRepo.getOptionsByQuestionId(questionId);
			questionMap.setOptions(options);
			result.add(questionMap);
		}
		return result;
	}

	@Override
	public QuestionObject getQuestionById(Long question_id) {
		Question question = questionRepo.findById(question_id).get();
		QuestionObject questionMap = modelMapper.map(question, QuestionObject.class);
		List<Option> options = optionRepo.getOptionsByQuestionId(question_id);
		questionMap.setOptions(options);
		return questionMap;
	}
}

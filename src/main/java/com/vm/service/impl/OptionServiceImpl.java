package com.vm.service.impl;

import com.vm.model.Option;
import com.vm.model.Question;
import com.vm.repo.OptionRepository;
import com.vm.repo.QuestionRepository;
import com.vm.service.OptionService;
import com.vm.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {
	@Autowired
	private OptionRepository repo;

	@Override
	public List<Option> getOptionsByQuestionId(Long question_id) {
		return repo.getOptionsByQuestionId(question_id);
	}
}

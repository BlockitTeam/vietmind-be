package com.vm.service;

import com.vm.model.Option;
import java.util.List;

public interface OptionService {
	public List<Option> getOptionsByQuestionId(Long question_id);
}
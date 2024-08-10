package com.vm.service.impl;

import com.vm.model.Option;
import com.vm.model.Question;
import com.vm.repo.OptionRepository;
import com.vm.repo.QuestionRepository;
import com.vm.request.NewQuestionObject;
import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
	public List<NewQuestionObject> getQuestionWithNewFormatBySurveyId(Long survey_id) {
		// Lấy tất cả các câu hỏi theo survey_id
		List<Question> questions = questionRepo.getQuestionBySurveyId(survey_id);

		// Sắp xếp các câu hỏi theo questionId để giữ đúng thứ tự khi chèn vào ban đầu
		questions.sort(Comparator.comparing(Question::getQuestionId));

		List<NewQuestionObject> result = new ArrayList<>();

		for (Question ele : questions) {
			// Chỉ xử lý các câu hỏi chính (không có parentQuestionId)
			if (ele.getParentQuestionId() == null) {
				// Mapping câu hỏi chính thành NewQuestionObject
				NewQuestionObject questionMap = modelMapper.map(ele, NewQuestionObject.class);

				// Lấy các options cho câu hỏi chính
				List<Option> options = optionRepo.getOptionsByQuestionId(ele.getQuestionId());
				questionMap.setOptions(options);

				// Thêm câu hỏi chính vào danh sách kết quả trước
				result.add(questionMap);

				// Tìm các câu hỏi con (có parentQuestionId trùng với questionId của câu hỏi chính)
				List<Question> subQuestions = questions.stream()
						.filter(sub -> ele.getQuestionId().equals(sub.getParentQuestionId()))
						// Sắp xếp các câu hỏi con theo questionId để đảm bảo thứ tự chèn ban đầu
						.sorted(Comparator.comparing(Question::getQuestionId))
						.collect(Collectors.toList());

				// Mapping và thêm các câu hỏi con vào kết quả sau câu hỏi chính
				for (Question sub : subQuestions) {
					NewQuestionObject subQuestionMap = modelMapper.map(sub, NewQuestionObject.class);
					// Lấy các options cho câu hỏi con
					List<Option> subOptions = optionRepo.getOptionsByQuestionId(sub.getQuestionId());
					subQuestionMap.setOptions(subOptions);
					// Thêm câu hỏi con vào danh sách kết quả
					result.add(subQuestionMap);
				}
			}
		}

		// Trả về danh sách các câu hỏi và câu hỏi con đã được sắp xếp đúng thứ tự
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

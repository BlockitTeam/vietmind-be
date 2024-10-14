package com.vm.service.impl;

import com.vm.model.Option;
import com.vm.model.SpecializedResponse;
import com.vm.model.User;
import com.vm.repo.SpecializedResponseRepository;
import com.vm.repo.UserRepository;
import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import com.vm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SpecializedResponseService {

    @Autowired
    private SpecializedResponseRepository specializedResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Transactional
    public List<SpecializedResponse> saveResponse(List<QuestionObject> request) {
        List<SpecializedResponse> responses = new ArrayList<>();
        User user = userRepository.findById(userService.getCurrentUUID())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Integer newVersion = 1;
        if (user.getLatestSpecializedVersion() != null) {
            newVersion = user.getLatestSpecializedVersion() + 1;
        }
        int surveyId = 0;

        for (QuestionObject ele : request) {
            SpecializedResponse response = new SpecializedResponse();
            surveyId = ele.getSurveyId();
            response.setSurveyId(surveyId);
            Object answer = ele.getAnswer();
            if (answer instanceof Number)
                response.setOptionId(((Number) answer).longValue());
            response.setUserId(userService.getStringCurrentUserId());
            response.setVersion(newVersion);
            responses.add(response);
        }

        // Cập nhật latest_specialized_version trong bảng users
        user.setLatestSpecializedVersion(newVersion);
        user.setSurveyDetailId(surveyId);
        userRepository.save(user);

        return (List<SpecializedResponse>) specializedResponseRepository.saveAll(responses);
    }

    @Transactional
    public SpecializedResponse saveSpecializedResponse(String userId, int surveyId, Long optionId) {
        // Tìm user
        User user = userRepository.findById(null)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Xác định phiên bản mới
        Integer newVersion = 1;
        if (user.getLatestSpecializedVersion() != null) {
            newVersion = user.getLatestSpecializedVersion() + 1;
        }

        // Tạo SpecializedResponse mới
        SpecializedResponse response = new SpecializedResponse();
        response.setUserId(userId);
        response.setSurveyId(surveyId);
        response.setOptionId(optionId);
        response.setVersion(newVersion);
        // Các trường khác có thể được thiết lập nếu cần

        // Lưu SpecializedResponse
        SpecializedResponse savedResponse = specializedResponseRepository.save(response);

        // Cập nhật latest_specialized_version trong bảng users
        user.setLatestSpecializedVersion(newVersion);
        userRepository.save(user);

        return savedResponse;
    }

    public List<QuestionObject> getLatestSpecializedResponse(String userId) {
        User user = userRepository.findById(userService.getCurrentUUID())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        int surveyId = user.getSurveyDetailId();
        List<QuestionObject> questions = questionService.getQuestionBySurveyId(surveyId);
        List<SpecializedResponse> result = specializedResponseRepository.findAllByUserIdAndSurveyIdWithMaxVersion(userId, user.getSurveyDetailId());

        // Gán phản hồi vào các câu hỏi
        assignResponsesToQuestions(questions, result);
        return questions;
    }

    public void assignResponsesToQuestions(List<QuestionObject> questions, List<SpecializedResponse> responses) {
        // Tạo một Map từ optionId đến SpecializedResponse để tăng hiệu suất tìm kiếm
        Map<Long, SpecializedResponse> optionIdToResponseMap = responses.stream()
                .collect(Collectors.toMap(SpecializedResponse::getOptionId, Function.identity()));

        for (QuestionObject question : questions) {
            List<Long> selectedOptionIds = new ArrayList<>();
            for (Option option : question.getOptions()) {
                SpecializedResponse response = optionIdToResponseMap.get(option.getOptionId());
                if (response != null) {
                    selectedOptionIds.add(option.getOptionId());
                }
            }

            if (!selectedOptionIds.isEmpty()) {
                if (question.getQuestionTypeId() == 1) { // Single choice
                    question.setAnswer(selectedOptionIds.get(0));
                } else if (question.getQuestionTypeId() == 2) { // Multiple choice
                    question.setAnswer(selectedOptionIds);
                }
            }
        }
    }
}


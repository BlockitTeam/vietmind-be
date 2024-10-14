package com.vm.service.impl;

import com.vm.model.SpecializedResponse;
import com.vm.model.User;
import com.vm.repo.SpecializedResponseRepository;
import com.vm.repo.UserRepository;
import com.vm.request.NewQuestionObject;
import com.vm.service.QuestionService;
import com.vm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    public List<SpecializedResponse> saveResponse(List<NewQuestionObject> request) {
        List<SpecializedResponse> responses = new ArrayList<>();
        User user = userRepository.findById(userService.getCurrentUUID())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Integer newVersion = 1;
        if (user.getLatestSpecializedVersion() != null) {
            newVersion = user.getLatestSpecializedVersion() + 1;
        }
        int surveyId = 0;

        for (NewQuestionObject ele : request) {
            SpecializedResponse response = new SpecializedResponse();
            surveyId = ele.getSurveyId();
            response.setSurveyId(surveyId);
            response.setQuestionId(ele.getQuestionId());

            String typeResponse = ele.getResponseFormat();
            if (typeResponse != null && "text_input".equals(typeResponse)) {
                response.setResponseText(ele.getAnswer().toString());
                response.setResponseFormat(typeResponse);
            } else if (typeResponse != null && "parent_question".equals(typeResponse)) {
                continue;
            } else {
                Object answer = ele.getAnswer();
                if (answer instanceof Number)
                    response.setOptionId(((Number) answer).longValue());
                response.setResponseFormat(typeResponse);
            }

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

    public List<NewQuestionObject> getLatestSpecializedResponse(String userId) {
        User user = userRepository.findById(userService.getCurrentUUID())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        int surveyId = user.getSurveyDetailId();
        List<NewQuestionObject> questions = questionService.getQuestionWithNewFormatBySurveyId(surveyId);
        List<SpecializedResponse> result = specializedResponseRepository.findAllByUserIdAndSurveyIdWithMaxVersion(userId, user.getSurveyDetailId());

        // Gán phản hồi vào các câu hỏi
        for (NewQuestionObject question : questions) {
            // Lọc danh sách SpecializedResponse theo questionId và surveyId tương ứng
            for (SpecializedResponse response : result) {
                if (response.getQuestionId().equals(question.getQuestionId())
                        && response.getSurveyId().equals(question.getSurveyId())) {
                    // Kiểm tra nếu có Option tương ứng trong question thì set giá trị
                    question.getOptions().stream()
                            .filter(option -> option.getOptionId().equals(response.getOptionId()))
                            .findFirst()
                            .ifPresent(option -> question.setAnswer(option.getOptionId()));
                }
            }
        }
        return questions;
    }
}


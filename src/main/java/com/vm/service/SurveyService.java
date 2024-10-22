package com.vm.service;

import com.vm.model.Survey;
import com.vm.repo.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    // Phương thức tìm Survey dựa trên surveyId
    public Optional<Survey> findSurveyById(Integer surveyId) {
        return surveyRepository.findBySurveyId(surveyId);
    }
}
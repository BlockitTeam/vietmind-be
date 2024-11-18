package com.vm.service;

import com.vm.dto.SurveyWithQuestionCountDTO;
import com.vm.repo.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    // Phương thức tìm Survey dựa trên surveyId
    public Optional<SurveyWithQuestionCountDTO> findSurveyById(Integer surveyId) {
        List<Object[]> result = surveyRepository.findSurveyWithQuestionCountNative(surveyId);

        if (result.isEmpty()) {
            return Optional.empty();
        }
        // Map the result to the DTO
        Object[] row = result.get(0);
        SurveyWithQuestionCountDTO dto = new SurveyWithQuestionCountDTO(
                (Integer) row[0], // surveyId
                (String) row[1],  // title
                (String) row[2],  // description
                (Integer) row[3], // priority
                ((Number) row[4]).longValue() // questionCount
        );
        return Optional.of(dto);
    }
}

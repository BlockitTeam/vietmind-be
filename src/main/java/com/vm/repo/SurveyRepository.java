package com.vm.repo;

import com.vm.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    @Query("SELECT s.title, s.priority FROM Survey s")
    List<Object[]> findTitleAndPriority();

    @Query("SELECT s.surveyId FROM Survey s WHERE s.title = :title")
    Integer findSurveyIdByTitle(String title);

    Optional<Survey> findBySurveyId(Integer surveyId);
}

package com.vm.repo;

import com.vm.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

//    @Query(value = "SELECT s.*, COUNT(q.question_id) AS questionCount " +
//            "FROM surveys s " +
//            "LEFT JOIN questions q ON s.survey_id = q.survey_id " +
//            "WHERE s.survey_id = :surveyId " +
//            "GROUP BY s.survey_id", nativeQuery = true)
//    Optional<Object[]> findSurveyWithQuestionCountNative(Integer surveyId);

    @Query(value = "SELECT s.survey_id AS surveyId, " +
            "       s.title AS title, " +
            "       s.description AS description, " +
            "       s.priority AS priority, " +
            "       COUNT(q.question_id) AS questionCount " +
            "FROM surveys s " +
            "LEFT JOIN questions q ON s.survey_id = q.survey_id " +
            "WHERE s.survey_id = :surveyId " +
            "GROUP BY s.survey_id, s.title, s.description, s.priority",
            nativeQuery = true)
    List<Object[]> findSurveyWithQuestionCountNative(@Param("surveyId") Integer surveyId);
}

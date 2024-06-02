package com.vm.repo;

import com.vm.model.Question;
import com.vm.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
	@Query("SELECT u FROM Question u WHERE u.surveyId = :survey_id")
	public List<Question> getQuestionBySurveyId(@Param("survey_id") Long survey_id);
}

package com.vm.repo;

import com.vm.model.Response;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ResponseRepository extends CrudRepository<Response, Long> {
	@Query("SELECT u FROM Response u WHERE u.surveyId = :survey_id")
	public List<Response> getResponseBySurveyId(@Param("survey_id") Long survey_id);

	@Query(value = "SELECT res.response_id as responseId, res.user_id as userId, res.option_id as optionId, res.created_at as createdAt, res.updated_at as updatedAt, " +
			"opt.score as score, quest.question_type_id as questionTypeId, quest_t.question_type as questionType " +
			"FROM responses res " +
			"JOIN options opt ON res.option_id = opt.option_id " +
			"JOIN questions quest ON quest.question_id = opt.question_id " +
			"JOIN question_type quest_t ON quest_t.question_type_id = quest.question_type_id " +
			"WHERE res.user_id = :userId",
			nativeQuery = true)
	List<Map<String, Object>> getAggregatedResponses(@Param("userId") String userId);

	@Modifying
	@Transactional
	@Query("Delete from Response a where a.userId = :userId")
	void deleteAllByEmployeeIdIn(@Param("userId") String userId);
}

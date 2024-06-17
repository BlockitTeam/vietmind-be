package com.vm.repo;

import com.vm.model.Conversation;
import com.vm.model.Response;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
    @Query("SELECT u FROM Response u WHERE u.surveyId = :survey_id")
    public List<Conversation> getResponseBySurveyId(@Param("survey_id") Long survey_id);
}

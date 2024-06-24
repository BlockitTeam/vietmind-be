package com.vm.repo;

import com.vm.model.Conversation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends CrudRepository<Conversation, Integer> {
    @Query("SELECT u FROM Response u WHERE u.surveyId = :survey_id")
    public List<Conversation> getResponseBySurveyId(@Param("survey_id") Long survey_id);

    @Query("SELECT u FROM Conversation u WHERE u.userId = :user_id AND u.doctorId = :doctor_id")
    public Conversation getConversationByUserIdAndDoctorId(@Param("user_id") String user_id, @Param("doctor_id") String doctor_id);
}

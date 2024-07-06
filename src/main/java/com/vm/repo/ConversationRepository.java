package com.vm.repo;

import com.vm.model.Conversation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ConversationRepository extends CrudRepository<Conversation, Integer> {
    @Query("SELECT u FROM Response u WHERE u.surveyId = :survey_id")
    public List<Conversation> getResponseBySurveyId(@Param("survey_id") Long survey_id);

    @Query("SELECT u FROM Conversation u WHERE (u.userId = :user_id AND u.doctorId = :doctor_id) OR (u.userId = :doctor_id AND u.doctorId = :user_id)")
    public Conversation getConversationByUserIdAndDoctorId(@Param("user_id") String user_id, @Param("doctor_id") String doctor_id);

    @Query("SELECT u FROM Conversation u WHERE u.userId = :person_id OR u.doctorId = :person_id")
    public List<Conversation> getConversationsByPersonId(@Param("person_id") String person_id);

    @Query("SELECT c, m, sender.firstName, sender.lastName, receiver.firstName, receiver.lastName FROM Conversation c " +
            "LEFT JOIN Message m ON c.conversationId = m.conversationId " +
            "LEFT JOIN User sender ON sender.id = UNHEX(REPLACE(m.senderId, '-', '')) " +
            "LEFT JOIN User receiver ON receiver.id = UNHEX(REPLACE(m.receiverId, '-', '')) " +
            "WHERE (c.userId = :userId OR c.doctorId = :userId) " +
            "AND m.createdAt = (SELECT MAX(m2.createdAt) FROM Message m2 WHERE m2.conversationId = c.conversationId)")
    List<Object[]> findAllConversationsWithLastMessageByUserId(@Param("userId") String userId);

    @Query("SELECT c.note FROM Conversation c WHERE c.conversationId = :conversationId")
    String findNoteByConversationId(@Param("conversationId") Integer conversationId);

    @Modifying
    @Transactional
    @Query("UPDATE Conversation c SET c.note = :note WHERE c.conversationId = :conversationId")
    void updateNoteByConversationId(@Param("conversationId") Integer conversationId, @Param("note") String note);
}

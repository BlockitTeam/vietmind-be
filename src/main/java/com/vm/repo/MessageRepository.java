package com.vm.repo;

import com.vm.model.Message;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface MessageRepository extends CrudRepository<Message, Long> {
    @Query("SELECT u FROM Message u WHERE u.conversationId = :conversation_id order by createdAt asc")
    public List<Message> getAllMessByConversationId(@Param("conversation_id") Integer conversation_id);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.messageId <= :messageId AND m.conversationId = (SELECT m2.conversationId FROM Message m2 WHERE m2.messageId = :messageId)")
    void markMessagesAsRead(@Param("messageId") Integer messageId, @Param("receiverId") String receiverId);
}

package com.vm.repo;

import com.vm.model.Message;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface MessageRepository extends CrudRepository<Message, Long> {
    @Query("SELECT u FROM Message u WHERE u.conversationId = :conversation_id order by createdAt asc")
    public List<Message> getAllMessByConversationId(@Param("conversation_id") Integer conversation_id);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.messageId <= :messageId AND m.conversationId = (SELECT m2.conversationId FROM Message m2 WHERE m2.messageId = :messageId)")
    void markMessagesAsRead(@Param("messageId") Integer messageId, @Param("receiverId") String receiverId);

    @Query("SELECT m.conversationId AS conversationId, COUNT(m) AS unreadCount " +
            "FROM Message m " +
            "WHERE m.conversationId IN :conversationIds " +
            "AND m.receiverId = :receiverId " +
            "AND m.isRead = false " +
            "GROUP BY m.conversationId")
    List<Map<String, Object>> countUnreadMessagesByConversationIdsAndReceiverId(
            @Param("conversationIds") List<Integer> conversationIds,
            @Param("receiverId") String receiverId
    );
}

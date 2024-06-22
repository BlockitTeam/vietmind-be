package com.vm.repo;

import com.vm.model.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    @Query("SELECT u FROM Message u WHERE u.conversationId = :conversation_id order by createdAt asc")
    public List<Message> getAllMessByConversationId(@Param("conversation_id") Integer conversation_id);
}

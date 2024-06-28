package com.vm.dto;

import com.vm.model.Conversation;
import com.vm.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ConversationWithLastMessageDTO {
    private Conversation conversation;
    private Message lastMessage;
    private String senderFullName;
    private String receiverFullName;

    public ConversationWithLastMessageDTO(Conversation conversation, Message lastMessage, String senderFullName, String receiverFullName) {
        this.conversation = conversation;
        this.lastMessage = lastMessage;
        this.senderFullName = senderFullName;
        this.receiverFullName = receiverFullName;
    }
}

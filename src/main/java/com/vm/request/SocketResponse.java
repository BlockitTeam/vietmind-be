package com.vm.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketResponse {
    private String fromUserId;
    private int conversationId;
    private String message;
    private int messageId;
    private String type;
    private LocalDateTime createAt;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // Handle the exception as needed, possibly returning a fallback value
            return "{\"error\":\"Unable to convert to JSON\"}";
        }
    }
}

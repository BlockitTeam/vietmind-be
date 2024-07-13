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

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            // Handle the exception as needed, possibly returning a fallback value
            throw ex;
        }
    }
}

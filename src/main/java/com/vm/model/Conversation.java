package com.vm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Integer conversationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "encrypted_session_key_sender", columnDefinition = "TEXT", nullable = false)
    private String encryptedSessionKeySender;

    @Column(name = "encrypted_session_key_recipient", columnDefinition = "TEXT", nullable = false)
    private String encryptedSessionKeyRecipient;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean isFinished = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

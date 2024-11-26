package com.vm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "specialized_responses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpecializedResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "response_format")
    private String responseFormat;

    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "response_text")
    private String responseText;

    @Column(name = "survey_id", nullable = false)
    private Integer surveyId;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

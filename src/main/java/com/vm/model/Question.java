package com.vm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "survey_id")
    private Integer surveyId;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "question_type_id")
    private Integer questionTypeId;

    @Column(name = "response_format")
    private String responseFormat;

    @Column(name = "text_input")
    private String textInput;

    @Column(name = "parent_question_id")
    private Long parentQuestionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

package com.vm.request;

import com.vm.model.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionObject {
    private Long questionId;
    private Long surveyId;
    private String questionText;
    private Integer questionTypeId;
    private List<Option> options;
    private Object answer;
}

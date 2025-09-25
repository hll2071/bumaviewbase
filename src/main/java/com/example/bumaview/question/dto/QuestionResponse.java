package com.example.bumaview.question.dto;

import com.example.bumaview.question.domain.Question;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuestionResponse {
    private Long id;

    private String content;

    private String category;

    private String company;

    private String status;

    private String questionAt;

    private Boolean possible;

    public static QuestionResponse of(Question question) {
        QuestionResponse questionResponse = new QuestionResponse(
                question.getId(),
                question.getContent(),
                question.getCategory(),
                question.getCompany(),
                question.getStatus(),
                question.getQuestionAt(),
                question.getPossible()
        );
        return questionResponse;
    }
}

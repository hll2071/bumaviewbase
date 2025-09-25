package com.example.bumaview.question.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionRequest {
    private String content;

    private String category;

    private String company;

    private String status;

    private String questionAt;
}

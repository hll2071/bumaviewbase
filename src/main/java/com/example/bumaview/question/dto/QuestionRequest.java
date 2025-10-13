package com.example.bumaview.question.dto;

import com.example.bumaview.question.domain.QuestionType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionRequest {
    private String content;

    private String category;

    private String company;

    private QuestionType status;

    private String questionAt;
}

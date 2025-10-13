package com.example.bumaview.question.dto;

import com.example.bumaview.question.domain.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AiQuestionRequest {
    private Long questionId;
    private Difficulty difficulty;
}
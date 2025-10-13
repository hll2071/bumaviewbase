package com.example.bumaview.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AiVerificationRequest {
    private Long questionId;
    private String answer;
}
package com.example.bumaview.question.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {

    SKILL("기술"),
    PERSONALITY("인성");

    private final String description;
}

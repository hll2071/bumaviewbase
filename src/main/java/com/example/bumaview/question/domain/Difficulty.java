package com.example.bumaview.question.domain;

import lombok.Getter;

@Getter
public enum Difficulty {
    VERY_HARD("매우 어려움"),
    HARD("어려움"),
    NORMAL("보통"),
    EASY("쉬움");

    private final String description;

    Difficulty(String description) {
        this.description = description;
    }
}
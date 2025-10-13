package com.example.bumaview.question.dto;

import com.example.bumaview.question.domain.Question;
import com.example.bumaview.question.domain.QuestionType;
import com.example.bumaview.user.domain.UserAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuestionResponse {
    private Long id;

    private String content;

    private String category;

    private String company;

    private QuestionType status;

    private String questionAt;

    private Boolean possible;

    private Boolean mark;

    private String answer;

    public static QuestionResponse of(Question question, boolean isBookmarked) {
        QuestionResponse questionResponse = new QuestionResponse(
                question.getId(),
                question.getContent(),
                question.getCategory(),
                question.getCompany(),
                question.getStatus(),
                question.getQuestionAt(),
                question.getPossible(),
                isBookmarked,
                question.getAnswer()
        );
        return questionResponse;
    }

    // 기존 메서드와의 호환성을 위한 오버로드 (북마크 상태 = false)
    public static QuestionResponse of(Question question) {
        return of(question, false);
    }

    // 사용자별 데이터(답변, possible)를 포함한 응답 생성
    public static QuestionResponse ofWithUserData(Question question, boolean isBookmarked, UserAnswer userAnswer) {
        return new QuestionResponse(
                question.getId(),
                question.getContent(),
                question.getCategory(),
                question.getCompany(),
                question.getStatus(),
                question.getQuestionAt(),
                userAnswer != null ? userAnswer.getPossible() : false,
                isBookmarked,
                userAnswer != null ? userAnswer.getAnswer() : null
        );
    }
}

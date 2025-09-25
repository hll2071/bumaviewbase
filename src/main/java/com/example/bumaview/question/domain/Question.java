package com.example.bumaview.question.domain;

import com.example.bumaview.question.dto.QuestionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String category;

    private String company;

    private String status;

    private String questionAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean like;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean possible;

    public static Question of(QuestionRequest request) {
        Question question = new Question(request.getContent(), request.getCategory(), request.getCompany(), request.getStatus(),request.getQuestionAt());
        return question;
    }

    Question(String content, String category, String company, String status,String questionAt) {
        this.content = content;
        this.category = category;
        this.company = company;
        this.status = status;
        this.questionAt = this.questionAt;
        this.like = false;
        this.possible = false;
    }

    public void update(QuestionRequest request) {
        this.content = request.getContent();
        this.category = request.getCategory();
        this.company = request.getCompany();
        this.questionAt = request.getQuestionAt();
    }
}

package com.example.bumaview.question.domain;

import com.example.bumaview.question.dto.QuestionRequest;
import com.example.bumaview.user.domain.User;
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

    private QuestionType status;

    private String questionAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static Question of(QuestionRequest request, User user) {
        Question question = new Question(request.getContent(), request.getCategory(), request.getCompany(), request.getStatus(),request.getQuestionAt(), user);
        return question;
    }

    Question(String content, String category, String company, QuestionType status,String questionAt, User user) {
        this.content = content;
        this.category = category;
        this.company = company;
        this.status = status;
        this.questionAt = questionAt;
        this.user = user;
    }

    public void update(QuestionRequest request) {
        this.content = request.getContent();
        this.category = request.getCategory();
        this.company = request.getCompany();
        this.questionAt = request.getQuestionAt();
    }


}

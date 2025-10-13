package com.example.bumaview.user.domain;

import com.example.bumaview.question.domain.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_answers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean possible;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public static UserAnswer of(User user, Question question, String answer) {
        return UserAnswer.builder()
                .user(user)
                .question(question)
                .answer(answer)
                .possible(false)
                .build();
    }

    public void updateAnswer(String answer) {
        this.answer = answer;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePossible(Boolean possible) {
        this.possible = possible;
        this.updatedAt = LocalDateTime.now();
    }
}
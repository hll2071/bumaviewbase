package com.example.bumaview.user.domain;

import com.example.bumaview.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    Optional<UserAnswer> findByUserAndQuestion(User user, Question question);
    boolean existsByUserAndQuestion(User user, Question question);
}
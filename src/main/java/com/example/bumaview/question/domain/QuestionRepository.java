package com.example.bumaview.question.domain;

import com.example.bumaview.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findAllByOrderByIdDesc();
    Optional<Question> findByIdAndUser(Long id, User user);
    List<Question> findByQuestionAtOrderByQuestionAtDesc(String questionAt);
    List<Question> findByCompanyOrderByQuestionAtDesc(String company);
    List<Question> findByCategoryOrderByQuestionAtDesc(String category);
}

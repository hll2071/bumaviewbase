package com.example.bumaview.user.domain;

import com.example.bumaview.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {
    Optional<UserBookmark> findByUserAndQuestion(User user, Question question);
    List<UserBookmark> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByUserAndQuestion(User user, Question question);
    void deleteByUserAndQuestion(User user, Question question);
}
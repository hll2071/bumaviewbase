package com.example.bumaview.question.service;

import com.example.bumaview.question.domain.Question;
import com.example.bumaview.question.domain.QuestionRepository;
import com.example.bumaview.question.dto.QuestionRequest;
import com.example.bumaview.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionResponse createQuestion(QuestionRequest request) {
        Question question = Question.of(request);
        Question questionSaved = questionRepository.save(question);

        return QuestionResponse.of(questionSaved);
    }

    public List<QuestionResponse> getAllQuestion() {
        List<QuestionResponse> questions = questionRepository.findAll().stream()
                .map(QuestionResponse::of)
                .toList();

        return questions;
    }

    public QuestionResponse updateQuestion(QuestionRequest request, Long questionId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));
        question.update(request);
        Question updatedQuestion = questionRepository.save(question);
        return QuestionResponse.of(updatedQuestion);
    }

    public String deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
        return "삭제완료";
    }

    public List<QuestionResponse> getQuestionByQuestionAt(String questionAt) {
        List<QuestionResponse> questionResponses = questionRepository.findByQuestionAtOrderByQuestionAtDesc(questionAt)
                .stream()
                .map(QuestionResponse::of)
                .toList();
        return questionResponses;
    }
}

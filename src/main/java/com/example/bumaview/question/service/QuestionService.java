package com.example.bumaview.question.service;

import com.example.bumaview.ai.service.GeminiService;
import com.example.bumaview.question.domain.Question;
import com.example.bumaview.question.domain.QuestionRepository;
import com.example.bumaview.question.dto.*;
import com.example.bumaview.user.domain.User;
import com.example.bumaview.user.domain.UserBookmark;
import com.example.bumaview.user.domain.UserBookmarkRepository;
import com.example.bumaview.user.domain.UserAnswer;
import com.example.bumaview.user.domain.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final GeminiService geminiService;

    public QuestionResponse createQuestion(QuestionRequest request, User user) {
        Question question = Question.of(request, user);
        Question questionSaved = questionRepository.save(question);

        return QuestionResponse.of(questionSaved);
    }

    public List<QuestionResponse> getAllQuestions() {
        List<QuestionResponse> questions = questionRepository.findAllByOrderByIdDesc().stream()
                .map(question -> QuestionResponse.of(question, false)) // 기본적으로 북마크 false
                .toList();

        return questions;
    }

    public QuestionResponse updateQuestion(QuestionRequest request, Long questionId, User user) {
        Question question = questionRepository.findByIdAndUser(questionId, user)
            .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));
        question.update(request);
        Question updatedQuestion = questionRepository.save(question);
        return QuestionResponse.of(updatedQuestion);
    }

    public String deleteQuestion(Long questionId, User user) {
        Question question = questionRepository.findByIdAndUser(questionId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));
        questionRepository.delete(question);
        return "삭제완료";
    }

    public List<QuestionResponse> getQuestionsByQuestionAt(String questionAt) {
        List<QuestionResponse> questionResponses = questionRepository.findByQuestionAtOrderByQuestionAtDesc(questionAt)
                .stream()
                .map(question -> QuestionResponse.of(question, false)) // 기본적으로 북마크 false
                .toList();
        return questionResponses;
    }

    public List<QuestionResponse> getQuestionsByCompany(String company) {
        List<QuestionResponse> questionResponses = questionRepository.findByCompanyOrderByQuestionAtDesc(company)
                .stream()
                .map(question -> QuestionResponse.of(question, false)) // 기본적으로 북마크 false
                .toList();
        return questionResponses;
    }

    public List<QuestionResponse> getBookmarkedQuestions(User user) {
        List<QuestionResponse> questionResponses = userBookmarkRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(bookmark -> QuestionResponse.of(bookmark.getQuestion(), true)) // 북마크된 질문이므로 true
                .toList();
        return questionResponses;
    }

    public List<QuestionResponse> getQuestionsByCategory(String category) {
        List<QuestionResponse> questionResponses = questionRepository.findByCategoryOrderByQuestionAtDesc(category)
                .stream()
                .map(question -> QuestionResponse.of(question, false)) // 기본적으로 북마크 false
                .toList();
        return questionResponses;
    }

    @Transactional
    public QuestionResponse toggleBookmark(Long questionId, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));

        boolean isBookmarked = userBookmarkRepository.existsByUserAndQuestion(user, question);

        if (isBookmarked) {
            // 북마크 해제
            userBookmarkRepository.deleteByUserAndQuestion(user, question);
        } else {
            // 북마크 추가
            UserBookmark bookmark = UserBookmark.of(user, question);
            userBookmarkRepository.save(bookmark);
        }

        return QuestionResponse.of(question, !isBookmarked); // 토글 후 상태 반환
    }

    public AiResponse generateSimilarQuestion(AiQuestionRequest request, User user) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));

        String aiGeneratedQuestion = geminiService.generateSimilarQuestion(
                question.getContent(),
                request.getDifficulty()
        ).block(); // 동기 처리

        // AI 생성 질문을 무조건 DB에 저장
        String currentYear = String.valueOf(java.time.Year.now().getValue());
        QuestionRequest newQuestionRequest = QuestionRequest.builder()
                .content(aiGeneratedQuestion)
                .category(question.getCategory())
                .company(question.getCompany())
                .status(question.getStatus())
                .questionAt(currentYear)
                .build();
        createQuestion(newQuestionRequest, user);

        return new AiResponse("유사 질문이 생성되었습니다.", aiGeneratedQuestion);
    }

    public AiResponse verifyAnswer(AiVerificationRequest request, User user) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));

        String verificationResult = geminiService.verifyAnswer(
                question.getContent(),
                request.getAnswer()
        ).block(); // 동기 처리

        // AI 검증 결과가 "좋음"으로 시작하면 자동으로 사용자별 답변 저장
        if (verificationResult != null && verificationResult.startsWith("좋음")) {
            UserAnswer userAnswer = userAnswerRepository.findByUserAndQuestion(user, question)
                    .orElse(UserAnswer.of(user, question, request.getAnswer()));

            userAnswer.updateAnswer(request.getAnswer());
            userAnswer.updatePossible(true);
            userAnswerRepository.save(userAnswer);
        }

        return new AiResponse("답변 검증이 완료되었습니다.", verificationResult);
    }

    public QuestionResponse updatePossible(Long questionId, Boolean possible, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다."));

        UserAnswer userAnswer = userAnswerRepository.findByUserAndQuestion(user, question)
                .orElse(UserAnswer.of(user, question, ""));

        userAnswer.updatePossible(possible);
        userAnswerRepository.save(userAnswer);

        boolean isBookmarked = userBookmarkRepository.existsByUserAndQuestion(user, question);
        return QuestionResponse.ofWithUserData(question, isBookmarked, userAnswer);
    }
}

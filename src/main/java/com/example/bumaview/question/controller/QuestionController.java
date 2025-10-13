package com.example.bumaview.question.controller;

import com.example.bumaview.question.dto.*;
import com.example.bumaview.question.service.QuestionService;
import com.example.bumaview.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public QuestionResponse createQuestion(@RequestBody QuestionRequest request, @AuthenticationPrincipal User user) {
        return questionService.createQuestion(request, user);
    }

    @GetMapping
    public List<QuestionResponse> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionResponse updateQuestion(@RequestBody QuestionRequest request, @PathVariable Long questionId, @AuthenticationPrincipal User user) {
        return questionService.updateQuestion(request, questionId, user);
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteQuestion(@PathVariable Long questionId, @AuthenticationPrincipal User user) {
        return questionService.deleteQuestion(questionId, user);
    }

    @GetMapping("/{questionAt}")
    public List<QuestionResponse> getQuestionByQuestionAt(@PathVariable String questionAt) {
        return questionService.getQuestionsByQuestionAt(questionAt);
    }

    @GetMapping("/company")
    public List<QuestionResponse> getQuestionsByCompany(@RequestParam String company) {
        return questionService.getQuestionsByCompany(company);
    }

    @GetMapping("/bookmark")
    public List<QuestionResponse> getBookmarkedQuestions(@AuthenticationPrincipal User user) {
        return questionService.getBookmarkedQuestions(user);
    }

    @GetMapping("/category")
    public List<QuestionResponse> getQuestionsByCategory(@RequestParam String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @PatchMapping("/bookmark/{questionId}")
    public QuestionResponse toggleBookmark(@PathVariable Long questionId, @AuthenticationPrincipal User user) {
        return questionService.toggleBookmark(questionId, user);
    }

    @PostMapping("/ai-question")
    public AiResponse generateSimilarQuestion(@RequestBody AiQuestionRequest request, @AuthenticationPrincipal User user) {
        return questionService.generateSimilarQuestion(request, user);
    }

    @PostMapping("/ai-verification")
    public AiResponse verifyAnswer(@RequestBody AiVerificationRequest request, @AuthenticationPrincipal User user) {
        return questionService.verifyAnswer(request, user);
    }


    @PatchMapping("/possible/{questionId}")
    public QuestionResponse updatePossible(@PathVariable Long questionId, @RequestParam Boolean possible, @AuthenticationPrincipal User user) {
        return questionService.updatePossible(questionId, possible, user);
    }

    @GetMapping("/test-auth")
    public String testAuth(@AuthenticationPrincipal User user) {
        return "현재 사용자: " + user.getEmail() + ", 권한: " + user.getAuthorities() + ", Role: " + user.getRole();
    }

}

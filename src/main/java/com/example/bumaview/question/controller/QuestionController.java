package com.example.bumaview.question.controller;

import com.example.bumaview.question.dto.QuestionRequest;
import com.example.bumaview.question.dto.QuestionResponse;
import com.example.bumaview.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public QuestionResponse createQuestion(QuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @GetMapping
    public List<QuestionResponse> getAllQuestions() {
        return questionService.getAllQuestion();
    }

    @PutMapping("/{questionId}")
    public QuestionResponse updateQuestion(QuestionRequest request, @PathVariable Long questionId) {
        return questionService.updateQuestion(request, questionId);
    }

    @DeleteMapping("/{questionId}")
    public String deleteQuestion(@PathVariable Long questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @GetMapping("/{questionAt}")
    public List<QuestionResponse> getQuestionByQuestionAt(@PathVariable String questionAt) {
        return questionService.getQuestionByQuestionAt(questionAt);
    }
    
}

package com.example.bumaview.document.controller;

import com.example.bumaview.document.dto.DocumentRequest;
import com.example.bumaview.document.dto.DocumentResponse;
import com.example.bumaview.document.service.DocumentService;
import com.example.bumaview.question.dto.AiResponse;
import com.example.bumaview.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentResponse createDocument(@RequestBody DocumentRequest request, @AuthenticationPrincipal User user) {
        return documentService.createDocument(request, user);
    }

    @GetMapping
    public DocumentResponse getMyDocument(@AuthenticationPrincipal User user) {
        return documentService.getUserDocument(user);
    }

    @PutMapping
    public DocumentResponse updateDocument(@RequestBody DocumentRequest request, @AuthenticationPrincipal User user) {
        return documentService.updateDocument(request, user);
    }

    @DeleteMapping
    public String deleteDocument(@AuthenticationPrincipal User user) {
        return documentService.deleteDocument(user);
    }

    @PostMapping("/generate-questions")
    public AiResponse generateQuestionsFromDocument(@AuthenticationPrincipal User user) {
        return documentService.generateQuestionsFromDocument(user);
    }
}
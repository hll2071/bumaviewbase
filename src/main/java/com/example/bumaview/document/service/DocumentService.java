package com.example.bumaview.document.service;

import com.example.bumaview.ai.service.GeminiService;
import com.example.bumaview.document.domain.Document;
import com.example.bumaview.document.domain.DocumentRepository;
import com.example.bumaview.document.dto.DocumentRequest;
import com.example.bumaview.document.dto.DocumentResponse;
import com.example.bumaview.question.domain.Question;
import com.example.bumaview.question.domain.QuestionRepository;
import com.example.bumaview.question.domain.QuestionType;
import com.example.bumaview.question.dto.AiResponse;
import com.example.bumaview.question.dto.QuestionRequest;
import com.example.bumaview.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final QuestionRepository questionRepository;
    private final GeminiService geminiService;

    public DocumentResponse createDocument(DocumentRequest request, User user) {
        Document document = new Document(request.getContent(), LocalDateTime.now(), user);
        Document savedDocument = documentRepository.save(document);
        return DocumentResponse.of(savedDocument);
    }

    public DocumentResponse getUserDocument(User user) {
        Optional<Document> document = documentRepository.findByUser(user);
        if (document.isPresent()) {
            return DocumentResponse.of(document.get());
        }
        return null;
    }

    public DocumentResponse updateDocument(DocumentRequest request, User user) {
        Document document = documentRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 자기소개서가 존재하지 않습니다."));
        document.update(request.getContent());
        Document updatedDocument = documentRepository.save(document);
        return DocumentResponse.of(updatedDocument);
    }

    public String deleteDocument(User user) {
        Document document = documentRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 자기소개서가 존재하지 않습니다."));
        documentRepository.delete(document);
        return "자기소개서 삭제완료";
    }

    public AiResponse generateQuestionsFromDocument(User user) {
        Document document = documentRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 자기소개서가 존재하지 않습니다."));

        String aiGeneratedQuestions = geminiService.generateQuestionsFromDocument(
                document.getContent(), user.getNickname()
        ).block(); // 동기 처리

        // AI가 생성한 질문들을 파싱하여 개별 질문으로 분리
        List<String> questions = parseQuestionsFromResponse(aiGeneratedQuestions);

        // 각 질문을 DB에 저장
        String currentYear = String.valueOf(java.time.Year.now().getValue());
        for (String questionContent : questions) {
            if (questionContent != null && !questionContent.trim().isEmpty()) {
                QuestionRequest newQuestionRequest = QuestionRequest.builder()
                        .content(questionContent.trim())
                        .category("자기소개서")
                        .company("AI생성")
                        .status(QuestionType.PERSONALITY)
                        .questionAt(currentYear)
                        .build();

                Question question = Question.of(newQuestionRequest, user);
                questionRepository.save(question);
            }
        }

        return new AiResponse("자기소개서 기반 면접 질문이 생성되었습니다.", aiGeneratedQuestions);
    }

    private List<String> parseQuestionsFromResponse(String response) {
        // "1. ", "2. " 등으로 시작하는 질문들을 파싱
        return Arrays.stream(response.split("\\n"))
                .filter(line -> line.matches("^\\d+\\.\\s*.*"))
                .map(line -> line.replaceFirst("^\\d+\\.\\s*", ""))
                .filter(line -> !line.trim().isEmpty())
                .toList();
    }
}
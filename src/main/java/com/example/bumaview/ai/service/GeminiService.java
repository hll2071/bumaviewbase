package com.example.bumaview.ai.service;

import com.example.bumaview.config.GeminiConfig;
import com.example.bumaview.question.domain.Difficulty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final WebClient geminiWebClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    public Mono<String> generateSimilarQuestion(String originalQuestion, Difficulty difficulty) {
        String prompt = String.format(
                "다음 면접 질문과 유사한 질문을 %s 난이도로 1개 생성해주세요. " +
                "원본 질문: %s\n\n" +
                "생성할 질문의 조건:\n" +
                "- 난이도: %s\n" +
                "- 원본 질문과 같은 주제나 분야\n" +
                "- 면접에 적합한 질문\n" +
                "- 질문 길이는 100자 이내로 제한\n" +
                "- 질문만 답변해주세요 (부가 설명 없이)",
                difficulty.getDescription(),
                originalQuestion,
                difficulty.getDescription()
        );

        return callGeminiApi(prompt);
    }

    public Mono<String> verifyAnswer(String question, String answer) {
        String prompt = String.format(
                "다음 면접 질문에 대한 답변을 평가해주세요:\n\n" +
                "질문: %s\n" +
                "답변: %s\n\n" +
                "평가 기준:\n" +
                "1. 질문에 대한 적합성\n" +
                "2. 답변의 완성도\n" +
                "3. 논리성과 구체성\n" +
                "4. 부적절한 표현이나 내용 여부\n\n" +
                "평가 결과를 '좋음', '보통', '개선 필요' 중 하나로 시작해서 " +
                "구체적인 피드백을 2-3문장으로 제공해주세요. 이때 평가 결과를 말하는 문장의 첫부분에는 무조건 '좋음', '보통', '개선 필요'로 시작시켜주세요. 특수문자나 마크다운 문법은 사용하지 마세요.",
                question,
                answer
        );

        return callGeminiApi(prompt);
    }

    public Mono<String> generateQuestionsFromDocument(String documentContent, String nickname) {
        String prompt = String.format(
                "다음은 %s님의 자기소개서 내용입니다. 이를 바탕으로 면접 질문 5개를 생성해주세요:\n\n" +
                "자기소개서 내용:\n%s\n\n" +
                "생성 조건:\n" +
                "- %s님의 자기소개서 경험, 역량, 프로젝트 등을 바탕으로 한 질문\n" +
                "- 실제 면접에서 나올 수 있는 현실적인 질문\n" +
                "- 각 질문을 번호와 함께 나열 (1. 2. 3. 4. 5.)\n" +
                "- 각 질문은 100자 이내로 제한\n" +
                "- 질문만 생성하고 난이도나 부가 설명은 포함하지 마세요",
                nickname,
                documentContent,
                nickname
        );

        return callGeminiApi(prompt);
    }

    private Mono<String> callGeminiApi(String prompt) {
        Map<String, Object> requestBody = createRequestBody(prompt);

        return geminiWebClient
                .post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", geminiConfig.getGeminiApiKey()).build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .map(this::extractTextFromResponse)
                .doOnError(error -> log.error("GEMINI API 호출 실패: ", error))
                .onErrorReturn("AI 서비스 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        // 생성 설정
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 1024);
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            log.error("GEMINI API 응답 파싱 실패: ", e);
            return "응답을 처리하는 중 오류가 발생했습니다.";
        }
    }
}
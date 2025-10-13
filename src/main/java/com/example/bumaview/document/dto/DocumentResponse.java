package com.example.bumaview.document.dto;

import com.example.bumaview.document.domain.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public static DocumentResponse of(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getContent(),
                document.getCreatedAt()
        );
    }
}
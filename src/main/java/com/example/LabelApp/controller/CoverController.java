package com.example.LabelApp.controller;

import com.example.LabelApp.models.Cover;
import com.example.LabelApp.repositories.CoverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CoverController {

    private final CoverRepository coverRepository;

    private static final String COVER_DIR = "uploads/covers/";

    @GetMapping("/covers/{coverId}")
    public ResponseEntity<Resource> getCoverById(@PathVariable String coverId) throws IOException {
        // Убираем все пробелы, неразрывные пробелы и другой мусор
        String cleaned = coverId.replaceAll("[\\s\\u00A0]+", "");

        Long id;
        try {
            id = Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Некорректный coverId: {}", coverId);
            return ResponseEntity.badRequest().build();
        }

        Cover cover = coverRepository.findById(id)
                .orElse(null);

        if (cover == null) {
            log.warn("Обложка не найдена по ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        String fileName = cover.getFilePath().substring(cover.getFilePath().lastIndexOf("/") + 1);
        Path filePath = Paths.get("uploads/covers/", fileName).normalize();

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            log.warn("Файл обложки не найден на диске: {}", filePath.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        String contentType = cover.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = Files.probeContentType(filePath);
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
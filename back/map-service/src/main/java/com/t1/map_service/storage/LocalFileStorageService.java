package com.t1.map_service.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Service
@Primary
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadDir = Paths.get("uploads/offices");

    @Override
    public String uploadImage(MultipartFile file, String objectKey) {
        try {
            // создаём папку uploads/offices, если её ещё нет
            Files.createDirectories(uploadDir);

            // берём имя файла
            String originalName = file.getOriginalFilename();

            // создаём уникальное имя, чтобы файлы не перезаписывались
            String fileName = System.nanoTime() + "-" + originalName;

            // полный путь до файла
            Path filePath = uploadDir.resolve(fileName);

            // сохраняем файл на диск
            Files.copy(file.getInputStream(), filePath);

            // возвращаем путь, который потом будет храниться в БД
            return "/uploads/offices/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save file locally", e);
        }
    }

    @Override
    public void deleteImage(String objectKey) {
        // пока можно оставить пустым
    }

    @Override
    public String presignGet(String objectKey, Duration ttl) {
        // MinIO больше нет, поэтому просто возвращаем обычный путь
        return objectKey;
    }

    @Override
    public String extractObjectKeyFromUrl(String url) {
        return url;
    }
}
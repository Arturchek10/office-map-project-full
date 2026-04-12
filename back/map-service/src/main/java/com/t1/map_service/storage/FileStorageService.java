package com.t1.map_service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

public interface FileStorageService {

    String uploadImage(MultipartFile file, String objectKey);

    void deleteImage(String objectKey);

    String presignGet(String objectKey, Duration ttl);

    String extractObjectKeyFromUrl(String url);
}

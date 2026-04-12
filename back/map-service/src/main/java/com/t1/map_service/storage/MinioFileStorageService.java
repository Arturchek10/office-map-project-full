package com.t1.map_service.storage;

import com.t1.map_service.exception.MinioException;
import com.t1.map_service.exception.MinioStorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minio;

    private final String bucket;

    private static final int MAX_PRESIGN_SECONDS = 7 * 24 * 60 * 60;

    private static final Set<String> ALLOWED = Set.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            "image/svg+xml"
    );

    public MinioFileStorageService(MinioClient minio,
                                   @Value("${kangaroohy.minio.bucket-name}") String bucket) {
        this.minio = minio;
        this.bucket = bucket;
    }

    @Override
    public String uploadImage(MultipartFile file, String objectKey) {
        if (file == null || file.isEmpty()) throw new MinioException("File is empty");

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) throw new MinioException("Unsupported content type");

        String key = StringUtils.hasText(objectKey) ? objectKey + ext(file)
                : "uploads/" + System.nanoTime() + ext(file);

        try (InputStream in = file.getInputStream()) {
            minio.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .contentType(contentType)
                    .stream(in, file.getSize(), -1)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to upload image, message: {}", e.getMessage());
            throw new MinioStorageException("Failed to upload image: %s".formatted(e.getMessage()), e.getCause());
        }
        return key;
    }

    @Override
    public void deleteImage(String objectKey) {
        if (!StringUtils.hasText(objectKey)) return;
        try {
            minio.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to delete image, message: {}", e.getMessage());
            throw new MinioStorageException("Failed to delete image: %s".formatted(e.getMessage()), e.getCause());
        }
    }

    @Override
    public String presignGet(String objectKey, Duration ttl) {
        if (!StringUtils.hasText(objectKey)) return null;
        int seconds = (int) Math.min(Math.max(ttl.toSeconds(), 1), MAX_PRESIGN_SECONDS);

        try {
            return minio.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(seconds)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to presign Get, objectKey: {}, message: {}", objectKey, e.getMessage());
            throw new MinioStorageException("Failed to presign url: %s".formatted(e.getMessage()), e.getCause());
        }
    }

    @Override
    public String extractObjectKeyFromUrl(String url) {
        if (!StringUtils.hasText(url)) return null;

        try {
            URI uri = URI.create(url);
            String path = Optional.ofNullable(uri.getPath()).orElse("");
            String clearPath = path.startsWith("/") ? path.substring(1) : path;

            String prefix = bucket + "/";
            if (!clearPath.startsWith(prefix)) {
                log.warn("Extract Object Key: path does not start with expected bucket. path = {}, bucket = {}", clearPath, bucket);
                return null;
            }

            String objectKey = clearPath.substring(prefix.length());

            return URLDecoder.decode(objectKey, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Extract Object Key: failed to parse url={}, message={}", url, e.getMessage());
            return null;
        }
    }

    private String ext(MultipartFile file) {
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();
        if (fileName.endsWith(".png")) return ".png";
        if (fileName.endsWith(".jpg")) return ".jpg";
        if (fileName.endsWith(".jpeg")) return ".jpeg";
        if (fileName.endsWith(".svg")) return ".svg";

        String contentType = file.getContentType();
        if ("image/svg+xml".equals(contentType)) return ".svg";
        if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) return ".png";
        if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) return ".jpg";
        return "";
    }
}

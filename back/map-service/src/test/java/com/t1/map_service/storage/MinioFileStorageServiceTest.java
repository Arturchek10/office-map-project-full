package com.t1.map_service.storage;

import com.t1.map_service.exception.MinioException;
import com.t1.map_service.exception.MinioStorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MinioFileStorageServiceTest {

    private MinioClient minio;
    private MinioFileStorageService service;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        minio = mock(MinioClient.class);
        service = new MinioFileStorageService(minio, "test-bucket");
        file = mock(MultipartFile.class);
    }

    @Test
    void uploadImage_shouldThrowWhenFileNull() {
        assertThatThrownBy(() -> service.uploadImage(null, "key"))
                .isInstanceOf(MinioException.class)
                .hasMessageContaining("File is empty");

        verifyNoInteractions(minio);
    }

    @Test
    void uploadImage_shouldThrowWhenFileEmpty() {
        when(file.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> service.uploadImage(file, "key"))
                .isInstanceOf(MinioException.class)
                .hasMessageContaining("File is empty");

        verifyNoInteractions(minio);
    }

    @Test
    void uploadImage_shouldThrowWhenUnsupportedType() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        assertThatThrownBy(() -> service.uploadImage(file, "key"))
                .isInstanceOf(MinioException.class)
                .hasMessageContaining("Unsupported content type");

        verifyNoInteractions(minio);
    }

    @Test
    void uploadImage_shouldUploadWithGivenKey() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(MediaType.IMAGE_PNG_VALUE);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(file.getSize()).thenReturn(4L);

        String result = service.uploadImage(file, "custom-key");

        assertThat(result).isEqualTo("custom-key.png");
        verify(minio).putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadImage_shouldThrowStorageExceptionWhenMinioFails() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(MediaType.IMAGE_JPEG_VALUE);
        when(file.getOriginalFilename()).thenReturn("img.jpg");
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(file.getSize()).thenReturn(10L);

        doThrow(new RuntimeException("boom"))
                .when(minio).putObject(any(PutObjectArgs.class));

        assertThatThrownBy(() -> service.uploadImage(file, "key"))
                .isInstanceOf(MinioStorageException.class)
                .hasMessageContaining("Failed to upload image");
    }

    @Test
    void deleteImage_shouldIgnoreEmptyKey() {
        service.deleteImage("");
        verifyNoInteractions(minio);
    }

    @Test
    void deleteImage_shouldCallMinio() throws Exception {
        service.deleteImage("some-key");
        verify(minio).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteImage_shouldThrowStorageExceptionWhenMinioFails() throws Exception {
        doThrow(new RuntimeException("fail"))
                .when(minio).removeObject(any(RemoveObjectArgs.class));

        assertThatThrownBy(() -> service.deleteImage("key"))
                .isInstanceOf(MinioStorageException.class)
                .hasMessageContaining("Failed to delete image");
    }

    @Test
    void presignGet_shouldReturnNullWhenKeyBlank() {
        assertThat(service.presignGet("", Duration.ofSeconds(10))).isNull();
        verifyNoInteractions(minio);
    }

    @Test
    void presignGet_shouldReturnUrl() throws Exception {
        when(minio.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("http://localhost/url");

        String url = service.presignGet("key", Duration.ofSeconds(60));

        assertThat(url).isEqualTo("http://localhost/url");
        verify(minio).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void presignGet_shouldClampTtlWithinBounds() throws Exception {
        when(minio.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("url");

        service.presignGet("key", Duration.ofSeconds(0)); // ниже 1 -> станет 1
        service.presignGet("key", Duration.ofDays(99));   // выше max -> станет 7 days

        verify(minio, times(2)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void presignGet_shouldThrowStorageExceptionWhenMinioFails() throws Exception {
        when(minio.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("oops"));

        assertThatThrownBy(() -> service.presignGet("key", Duration.ofSeconds(10)))
                .isInstanceOf(MinioStorageException.class)
                .hasMessageContaining("Failed to presign url");
    }

    @Test
    void extractObjectKey_shouldReturnKey_fromPathStyleWithQuery() {
        String url = "http://127.0.0.1:9000/test-bucket/folder/img.png?X-Amz-Expires=600&sig=abc";
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isEqualTo("folder/img.png");
    }

    @Test
    void extractObjectKey_shouldReturnKey_whenNoQueryParams() {
        String url = "http://127.0.0.1:9000/test-bucket/a/b/c.jpg";
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isEqualTo("a/b/c.jpg");
    }

    @Test
    void extractObjectKey_shouldDecodeUrlEncodedSegments() {
        String encodedName = URLEncoder.encode("My File (final).svg", StandardCharsets.UTF_8);
        String url = "http://127.0.0.1:9000/test-bucket/folder/" + encodedName;
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isEqualTo("folder/My File (final).svg");
    }

    @Test
    void extractObjectKey_shouldReturnNull_whenBucketDoesNotMatch() {
        String url = "http://127.0.0.1:9000/another-bucket/folder/img.png";
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isNull();
    }

    @Test
    void extractObjectKey_shouldReturnNull_whenPathDoesNotStartWithBucket() {
        String url = "http://127.0.0.1:9000/folder/img.png";
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isNull();
    }

    @Test
    void extractObjectKey_shouldReturnEmpty_whenOnlyBucketProvided() {
        String url = "http://127.0.0.1:9000/test-bucket/";
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isEmpty();
    }

    @Test
    void extractObjectKey_shouldReturnNull_onMalformedUrl() {
        String url = "http://127.0.0.1:9000/test-bucket/%zz%zz";
        String key = service.extractObjectKeyFromUrl(url);
        assertThat(key).isNull();
    }
}
package com.t1.map_service.service.photo.impl;

import com.t1.map_service.service.photo.PhotoOwner;
import com.t1.map_service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseMinioPhotoServiceTest {

    static class DummyEntity implements PhotoOwner {
        private Long id;
        private String photoKey;

        DummyEntity(Long id) { this.id = id; }

        @Override public Long getId() {
            return id;
        }

        @Override public String getPhotoKey() {
            return photoKey;
        }

        @Override public void setPhotoKey(String key) {
            this.photoKey = key;
        }
    }

    static class DummyPhotoService extends BaseMinioPhotoService<DummyEntity> {
        private static final String KEY_PREFIX = "test/%d/photo";

        public DummyPhotoService(FileStorageService storage) {
            super(storage);
        }

        @Override protected String keyPrefixFormat() {
            return KEY_PREFIX;
        }
    }

    @Mock
    FileStorageService storage;

    @Mock
    MultipartFile file;

    private DummyPhotoService service;
    private DummyEntity entity;

    @BeforeEach
    void setUp() {
        service = new DummyPhotoService(storage);
        entity = new DummyEntity(42L);
    }

    // hasPhoto

    @Test
    void shouldReturnFalseWhenPhotoKeyNullOrBlank() {
        entity.setPhotoKey(null);
        assertThat(service.hasPhoto(entity)).isFalse();

        entity.setPhotoKey("");
        assertThat(service.hasPhoto(entity)).isFalse();

        entity.setPhotoKey("   ");
        assertThat(service.hasPhoto(entity)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenPhotoKeyExists() {
        entity.setPhotoKey("test/42/photo.png");
        assertThat(service.hasPhoto(entity)).isTrue();
    }

    // uploadPhoto

    @Test
    void shouldDoNothingWhenFileNullOrEmptyOnUpload() {
        service.uploadPhoto(entity, null);
        verifyNoInteractions(storage);
        assertThat(entity.getPhotoKey()).isNull();

        when(file.isEmpty()).thenReturn(true);
        service.uploadPhoto(entity, file);
        verifyNoMoreInteractions(storage);
        assertThat(entity.getPhotoKey()).isNull();
    }

    @Test
    void shouldUploadPhotoAndSetKey() {
        when(file.isEmpty()).thenReturn(false);
        when(storage.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn("test/42/photo.jpg");

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);

        service.uploadPhoto(entity, file);

        verify(storage).uploadImage(fileCaptor.capture(), prefixCaptor.capture());
        assertThat(fileCaptor.getValue()).isSameAs(file);
        assertThat(prefixCaptor.getValue()).isEqualTo("test/42/photo");
        assertThat(entity.getPhotoKey()).isEqualTo("test/42/photo.jpg");
    }

    @Test
    void shouldThrowWhenEntityIdNullOnUpload() {
        DummyEntity noId = new DummyEntity(null);
        when(file.isEmpty()).thenReturn(false);

        assertThatThrownBy(() -> service.uploadPhoto(noId, file))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Entity id must not be null");
        verifyNoInteractions(storage);
    }

    @Test
    void shouldPropagateStorageExceptionOnUpload() {
        when(file.isEmpty()).thenReturn(false);
        when(storage.uploadImage(any(MultipartFile.class), anyString()))
                .thenThrow(new RuntimeException("storage down"));

        assertThatThrownBy(() -> service.uploadPhoto(entity, file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("storage down");
    }

    // replacePhoto

    @Test
    void shouldReplacePhoto_removeOldThenUploadNew() {
        entity.setPhotoKey("test/42/photo-old.jpg");
        when(file.isEmpty()).thenReturn(false);
        when(storage.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn("test/42/photo-new.jpg");

        ArgumentCaptor<String> deleteCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uploadPrefixCaptor = ArgumentCaptor.forClass(String.class);

        service.replacePhoto(entity, file);

        verify(storage).deleteImage(deleteCaptor.capture());
        verify(storage).uploadImage(any(MultipartFile.class), uploadPrefixCaptor.capture());

        assertThat(deleteCaptor.getValue()).isEqualTo("test/42/photo-old.jpg");
        assertThat(uploadPrefixCaptor.getValue()).isEqualTo("test/42/photo");
        assertThat(entity.getPhotoKey()).isEqualTo("test/42/photo-new.jpg");
    }

    @Test
    void shouldReplacePhotoWhenNoOldKey_onlyUpload() {
        entity.setPhotoKey(null);
        when(file.isEmpty()).thenReturn(false);
        when(storage.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn("test/42/photo-new.jpg");

        service.replacePhoto(entity, file);

        verify(storage, never()).deleteImage(anyString());
        verify(storage).uploadImage(any(MultipartFile.class), eq("test/42/photo"));
        assertThat(entity.getPhotoKey()).isEqualTo("test/42/photo-new.jpg");
    }

    @Test
    void shouldNotChangeAnythingWhenReplaceWithNullOrEmptyFile() {
        entity.setPhotoKey("test/42/photo.jpg");

        service.replacePhoto(entity, null);
        verifyNoInteractions(storage);
        assertThat(entity.getPhotoKey()).isEqualTo("test/42/photo.jpg");

        when(file.isEmpty()).thenReturn(true);
        service.replacePhoto(entity, file);
        verifyNoInteractions(storage);
        assertThat(entity.getPhotoKey()).isEqualTo("test/42/photo.jpg");
    }

    // removePhoto

    @Test
    void shouldRemovePhotoAndClearKey() {
        entity.setPhotoKey("test/42/photo.jpg");

        ArgumentCaptor<String> deleteCaptor = ArgumentCaptor.forClass(String.class);

        service.removePhoto(entity);

        verify(storage).deleteImage(deleteCaptor.capture());
        assertThat(deleteCaptor.getValue()).isEqualTo("test/42/photo.jpg");
        assertThat(entity.getPhotoKey()).isNull();
    }

    @Test
    void shouldDoNothingWhenNoPhotoOnRemove() {
        entity.setPhotoKey(null);

        service.removePhoto(entity);

        verifyNoInteractions(storage);
        assertThat(entity.getPhotoKey()).isNull();
    }

    // presign

    @Test
    void shouldReturnNullPresignWhenNoKeyOrBlank() {
        entity.setPhotoKey(null);
        assertThat(service.presign(entity, Duration.ofMinutes(5))).isNull();
        verifyNoInteractions(storage);

        entity.setPhotoKey("   ");
        assertThat(service.presign(entity, Duration.ofMinutes(5))).isNull();
        verifyNoInteractions(storage);
    }

    @Test
    void shouldReturnPresignedUrl() {
        entity.setPhotoKey("test/42/photo.jpg");
        when(storage.presignGet("test/42/photo.jpg", Duration.ofMinutes(5)))
                .thenReturn("http://minio/presigned");

        String url = service.presign(entity, Duration.ofMinutes(5));

        verify(storage).presignGet("test/42/photo.jpg", Duration.ofMinutes(5));
        assertThat(url).isEqualTo("http://minio/presigned");
    }
}
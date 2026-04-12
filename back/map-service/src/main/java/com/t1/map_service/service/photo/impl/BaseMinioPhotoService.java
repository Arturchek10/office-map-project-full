package com.t1.map_service.service.photo.impl;

import com.t1.map_service.service.photo.GenericPhotoService;
import com.t1.map_service.service.photo.PhotoOwner;
import com.t1.map_service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RequiredArgsConstructor
public abstract class BaseMinioPhotoService<T extends PhotoOwner> implements GenericPhotoService<T> {

    private final FileStorageService storage;

    // Каждая реализация задает свой префикс
    protected abstract String keyPrefixFormat();

    protected String keyPrefix(T entity){
        if (entity == null || entity.getId() == null) {
            throw new IllegalStateException("Entity id must not be null for photo operations");
        }
        return keyPrefixFormat().formatted(entity.getId());
    }

    @Override
    public boolean hasPhoto(T entity){
        return entity.getPhotoKey() != null && !entity.getPhotoKey().isBlank();
    }

    @Override
    public void uploadPhoto(T entity, MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        String key = storage.uploadImage(file, keyPrefix(entity)); // storage сам добавит расширение
        entity.setPhotoKey(key);
    }

    @Override
    public void replacePhoto(T entity, MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        removePhoto(entity);
        uploadPhoto(entity, file);
    }

    @Override
    public void removePhoto(T entity) {
        if (hasPhoto(entity)) {
            storage.deleteImage(entity.getPhotoKey());
            entity.setPhotoKey(null);
        }
    }

    @Override
    public String presign(T entity, Duration ttl) {
        if (!StringUtils.hasText(entity.getPhotoKey())) return null;
        return storage.presignGet(entity.getPhotoKey(), ttl);
    }
}

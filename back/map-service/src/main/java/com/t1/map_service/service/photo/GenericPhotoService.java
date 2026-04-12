package com.t1.map_service.service.photo;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

public interface GenericPhotoService<T extends PhotoOwner> {

    boolean hasPhoto(T entity);

    void uploadPhoto(T entity, MultipartFile file);

    void replacePhoto(T entity, MultipartFile file);

    void removePhoto(T entity);

    String presign(T entity, Duration ttl);
}

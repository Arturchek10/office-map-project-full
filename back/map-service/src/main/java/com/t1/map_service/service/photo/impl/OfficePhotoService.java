package com.t1.map_service.service.photo.impl;

import com.t1.map_service.model.entity.Office;
import com.t1.map_service.storage.FileStorageService;
import org.springframework.stereotype.Service;

@Service
public class OfficePhotoService extends BaseMinioPhotoService<Office> {

    private static final String KEY_PREFIX = "offices/%d/photo";

    public OfficePhotoService(FileStorageService storage) {
        super(storage);
    }

    @Override
    protected String keyPrefixFormat() {
        return KEY_PREFIX;
    }
}
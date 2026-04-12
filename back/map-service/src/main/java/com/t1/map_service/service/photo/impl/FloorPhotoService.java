package com.t1.map_service.service.photo.impl;

import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.storage.FileStorageService;
import org.springframework.stereotype.Service;

@Service
public class FloorPhotoService extends BaseMinioPhotoService<Floor>{

    private static final String KEY_PREFIX = "floors/%d/photo";

    public FloorPhotoService(FileStorageService storage) {
        super(storage);
    }

    @Override
    protected String keyPrefixFormat() {
        return KEY_PREFIX;
    }
}

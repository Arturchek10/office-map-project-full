package com.t1.map_service.service.photo.impl;

import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.storage.FileStorageService;
import org.springframework.stereotype.Service;

@Service
public class FurniturePhotoService extends BaseMinioPhotoService<Furniture>{

    private static final String KEY_PREFIX = "furniture/%d/photo";

    public FurniturePhotoService(FileStorageService storage) {
        super(storage);
    }

    @Override
    protected String keyPrefixFormat() {
        return KEY_PREFIX;
    }
}

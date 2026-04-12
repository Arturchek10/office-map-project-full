package com.t1.map_service.mapper.furniture;

import com.t1.map_service.dto.furniture.FurnitureShortDto;
import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.storage.FileStorageService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface FurnitureShortMapper {

    @Mapping(target = "photoUrl",
            source = "photoKey",
            qualifiedByName = "photoKeyToPresigned")
    FurnitureShortDto toDto(Furniture furniture,
                            @Context FileStorageService storage,
                            @Context Duration ttl);

    @Named("photoKeyToPresigned")
    default String photoKeyToPresigned(String key,
                                       @Context FileStorageService storage,
                                       @Context Duration ttl) {
        if (key == null || key.isBlank()) return null;
        return storage.presignGet(key, ttl);
    }
}

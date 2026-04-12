package com.t1.map_service.mapper.furniture;

import com.t1.map_service.dto.furniture.*;
import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.storage.FileStorageService;
import org.mapstruct.*;

import java.time.Duration;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FurnitureMapper {

    @Mapping(target = "photoKey", ignore = true)
    Furniture toEntity(FurnitureCreateRequest request);

    Furniture toEntity(FurniturePlaceRequest request);

    FurnitureDto toDto(Furniture furniture);

    @Mapping(target = "photoUrl",
            source = "photoKey",
            qualifiedByName = "photoKeyToPresigned")
    FurnitureDto toDto(Furniture furniture,
                       @Context FileStorageService storage,
                       @Context Duration ttl);

    void update(@MappingTarget Furniture furniture, FurniturePatchUiRequest request);

    @Mapping(target = "photoKey", ignore = true)
    void update(@MappingTarget Furniture furniture, FurniturePatchRequest request);

    @Named("photoKeyToPresigned")
    default String photoKeyToPresigned(String key,
                                       @Context FileStorageService storage,
                                       @Context Duration ttl) {
        if (key == null || key.isBlank()) return null;
        return storage.presignGet(key, ttl);
    }
}

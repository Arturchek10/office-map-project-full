package com.t1.map_service.mapper;

import com.t1.map_service.dto.floor.FloorCreateRequest;
import com.t1.map_service.dto.floor.FloorDto;
import com.t1.map_service.dto.floor.FloorUpdateRequest;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.storage.FileStorageService;
import org.mapstruct.*;

import java.time.Duration;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FloorMapper {

    @Mapping(target = "photoKey", ignore = true)
    @Mapping(target = "office", ignore = true) // office задаётся в сервисе
    Floor toEntity(FloorCreateRequest dto);

    @Mapping(target = "photoUrl", ignore = true)
    FloorDto toDto(Floor floor);

    @Mapping(target = "photoUrl",
            source = "photoKey",
            qualifiedByName = "photoKeyToPresigned")
    FloorDto toDto(Floor floor,
                   @Context FileStorageService storage,
                   @Context Duration ttl);

    @Mapping(target = "photoKey", ignore = true)
    @Mapping(target = "office", ignore = true)
    void update(@MappingTarget Floor target, FloorUpdateRequest source);

    @Named("photoKeyToPresigned")
    default String photoKeyToPresigned(String key,
                                       @Context FileStorageService storage,
                                       @Context Duration ttl) {
        if (key == null || key.isBlank()) return null;
        return storage.presignGet(key, ttl);
    }
}
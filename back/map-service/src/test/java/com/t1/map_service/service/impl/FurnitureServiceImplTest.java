package com.t1.map_service.service.impl;

import com.t1.map_service.dto.furniture.*;
import com.t1.map_service.exception.EntityAlreadyExistsException;
import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.mapper.furniture.FurnitureMapper;
import com.t1.map_service.mapper.furniture.FurnitureShortMapper;
import com.t1.map_service.model.Point;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.repository.FurnitureRepository;
import com.t1.map_service.service.inner.FloorInnerService;
import com.t1.map_service.service.inner.FurnitureInnerService;
import com.t1.map_service.service.photo.impl.FurniturePhotoService;
import com.t1.map_service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FurnitureServiceImplTest {

    @Mock FurnitureRepository repository;
    @Mock FurnitureInnerService innerService;
    @Mock FurnitureMapper mapper;
    @Mock FurnitureShortMapper shortMapper;
    @Mock FloorInnerService floorService;
    @Mock FileStorageService storage;
    @Mock FurniturePhotoService photoService;

    @InjectMocks FurnitureServiceImpl service;

    private Furniture entity;
    private FurnitureDto dto;

    @BeforeEach
    void setUp() {
        entity = new Furniture();
        entity.setId(1L);
        entity.setName("Chair");

        dto = new FurnitureDto(
                1L,
                "Chair",
                "http://url",
                90,
                new Point(10.0, 20.0),
                (short) 2
        );

        // durationTtl инициализируется через @PostConstruct
        service.init();
    }

    @Test
    void shouldGetFurnitureById() {
        when(innerService.getEntityById(1L)).thenReturn(entity);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto result = service.getFurnitureById(1L);

        verify(innerService).getEntityById(1L);
        verify(mapper).toDto(same(entity), same(storage), any(Duration.class));
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldFailWithoutPhoto_onCreate() {
        FurnitureCreateRequest req = new FurnitureCreateRequest("Chair");
        when(repository.existsByNameIgnoreCase("Chair")).thenReturn(false);

        assertThatThrownBy(() -> service.create(req, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Photo is required");

        // был вызван только чек уникальности
        verify(repository).existsByNameIgnoreCase("Chair");
        verifyNoMoreInteractions(repository);

        verifyNoInteractions(mapper, storage, photoService);
    }

    @Test
    void shouldCreateWithPhoto() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        FurnitureCreateRequest req = new FurnitureCreateRequest("Chair");

        Furniture entity = new Furniture();
        entity.setName("Chair");

        when(repository.existsByNameIgnoreCase("Chair")).thenReturn(false);
        when(mapper.toEntity(req)).thenReturn(entity);

        when(storage.uploadImage(file, null)).thenReturn("uploads/123.png");

        when(repository.save(entity)).thenReturn(entity);

        FurnitureDto dto = new FurnitureDto(1L, "Chair", "http://url", 90, new Point(10.0, 20.0), (short) 2);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto result = service.create(req, file);

        // фото грузим через storage, а не через photoService
        verify(storage).uploadImage(file, null);
        verify(photoService, never()).uploadPhoto(any(), any());

        // в сущности должен появиться ключ
        assertThat(entity.getPhotoKey()).isEqualTo("uploads/123.png");

        // сохраняем один раз
        verify(repository, times(1)).save(entity);

        // маппинг в DTO вызван с контекстами
        verify(mapper).toDto(same(entity), same(storage), any(Duration.class));

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldThrowWhenNameExists_onCreate() {
        FurnitureCreateRequest req = new FurnitureCreateRequest("Chair");
        when(repository.existsByNameIgnoreCase("Chair")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req, null))
                .isInstanceOf(EntityAlreadyExistsException.class);

        verify(repository).existsByNameIgnoreCase("Chair");
        verify(repository, never()).save(any());
        verify(photoService, never()).uploadPhoto(any(), any());
    }

    @Test
    void shouldPlaceFurnitureSuccessfully() {
        Long floorId = 5L;

        String url = "http://10.10.146.211:9000/map-service/offices/225/photo.png?X-Amz-...";
        String extractedKey = "offices/225/photo.png";

        FurniturePlaceRequest req = new FurniturePlaceRequest(
                "Sofa",
                new Point(1.0, 2.0),
                url
        );

        Floor floor = new Floor();
        floor.setId(floorId);

        Furniture placed = new Furniture();
        placed.setId(42L);
        placed.setName("Sofa");

        FurnitureDto placedDto = new FurnitureDto(42L, "Sofa", null, null, null, (short) 1);

        when(repository.existsByNameIgnoreCase("Sofa")).thenReturn(true);
        when(floorService.getEntityById(floorId)).thenReturn(floor);
        when(mapper.toEntity(req)).thenReturn(placed);
        when(storage.extractObjectKeyFromUrl(url)).thenReturn(extractedKey);
        when(repository.save(placed)).thenReturn(placed);
        when(mapper.toDto(same(placed), same(storage), any(Duration.class))).thenReturn(placedDto);

        FurnitureDto result = service.placeFurniture(floorId, req);

        verify(repository).existsByNameIgnoreCase("Sofa");
        verify(floorService).getEntityById(floorId);
        verify(mapper).toEntity(req);
        verify(storage).extractObjectKeyFromUrl(url);
        verify(repository).save(placed);
        verify(mapper).toDto(same(placed), same(storage), any(Duration.class));

        assertThat(placed.getFloor()).isSameAs(floor);
        assertThat(placed.getPhotoKey()).isEqualTo(extractedKey);
        assertThat(result).isEqualTo(placedDto);
    }

    @Test
    void shouldFailOnPlace_whenCannotExtractKeyFromUrl() {
        Long floorId = 7L;

        String url = "http://10.10.146.211:9000/unknown-bucket/file.png?X-Amz-...";
        FurniturePlaceRequest req = new FurniturePlaceRequest(
                "Sofa",
                new Point(3.3, 4.4),
                url
        );

        when(repository.existsByNameIgnoreCase("Sofa")).thenReturn(true);
        when(floorService.getEntityById(floorId)).thenReturn(new Floor());

        Furniture mapped = new Furniture();
        mapped.setName("Sofa");
        when(mapper.toEntity(req)).thenReturn(mapped);

        when(storage.extractObjectKeyFromUrl(url)).thenReturn(null); // ключ не смогли достать

        assertThatThrownBy(() -> service.placeFurniture(floorId, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot extract photo key");

        // не сохраняем, если ключ не извлечён
        verify(repository, never()).save(any(Furniture.class));
    }

    @Test
    void shouldThrowWhenFurnitureTemplateNotExists_onPlace() {
        FurniturePlaceRequest req = new FurniturePlaceRequest(
                "Unknown",
                new Point(0.0, 0.0),
                "http://10.10.146.211:9000/map-service/some/key.png?X-Amz-..."
        );
        when(repository.existsByNameIgnoreCase("Unknown")).thenReturn(false);

        assertThatThrownBy(() -> service.placeFurniture(1L, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Furniture not found");

        verifyNoMoreInteractions(floorService, mapper, repository);
    }

    @Test
    void shouldMoveFurniture_setNewPosition() {
        Long id = 10L;
        Point newPos = new Point(7.7, 8.8);
        FurnitureMoveRequest req = new FurnitureMoveRequest(newPos);

        when(innerService.getEntityById(id)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto result = service.move(id, req);

        verify(innerService).getEntityById(id);
        verify(repository).save(entity);
        assertThat(entity.getPosition()).isEqualTo(newPos);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldUpdateUiAndReturnDto() {
        Long id = 1L;
        FurniturePatchUiRequest req = new FurniturePatchUiRequest(180, (short) 3);

        when(innerService.getEntityById(id)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto result = service.updateUi(id, req);

        verify(innerService).getEntityById(id);
        verify(mapper).update(same(entity), eq(req));
        verify(repository).save(entity);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldUpdate_removePhotoWhenFlagTrue() {
        Long id = 1L;
        FurniturePatchRequest req = new FurniturePatchRequest("OldName", true);

        when(innerService.getEntityById(id)).thenReturn(entity);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto result = service.update(id, req, null);

        verify(photoService).removePhoto(entity);
        verify(photoService, never()).replacePhoto(any(), any());
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldUpdate_replacePhotoWhenNewPhotoProvided() {
        Long id = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        FurniturePatchRequest req = new FurniturePatchRequest("NewName", false);

        when(innerService.getEntityById(id)).thenReturn(entity);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto result = service.update(id, req, file);

        verify(photoService).replacePhoto(entity, file);
        verify(photoService, never()).removePhoto(any());
        verify(mapper).update(same(entity), eq(req));
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldUpdate_doNothingWithPhoto_whenNoFlagsAndNoFile() {
        Long id = 1L;
        FurniturePatchRequest req = new FurniturePatchRequest("NewName", null);

        when(innerService.getEntityById(id)).thenReturn(entity);
        when(mapper.toDto(same(entity), same(storage), any(Duration.class))).thenReturn(dto);

        FurnitureDto r1 = service.update(id, req, null);
        verify(photoService, never()).removePhoto(any());
        verify(photoService, never()).replacePhoto(any(), any());
        assertThat(r1).isEqualTo(dto);
    }

    @Test
    void shouldDeleteFurnitureAndRemovePhoto() {
        when(innerService.getEntityById(1L)).thenReturn(entity);

        service.deleteFurniture(1L);

        verify(photoService).removePhoto(entity);
        verify(repository).delete(entity);
    }

    @Test
    void shouldDeleteEvenIfRemovePhotoFails() {
        when(innerService.getEntityById(1L)).thenReturn(entity);
        doThrow(new RuntimeException("minio down")).when(photoService).removePhoto(entity);

        service.deleteFurniture(1L);

        verify(repository).delete(entity);
    }

    @Test
    void shouldReturnCatalogPage_mappedToShortDtos() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        Furniture f1 = new Furniture();
        f1.setId(1L);
        f1.setName("A");

        Furniture f2 = new Furniture();
        f2.setId(2L);
        f2.setName("B");

        Page<Furniture> page = new PageImpl<>(List.of(f1, f2), pageable, 2);

        FurnitureShortDto s1 = new FurnitureShortDto(1L, "A", null);
        FurnitureShortDto s2 = new FurnitureShortDto(2L, "B", null);

        when(innerService.getAllUnique(pageable)).thenReturn(page);
        when(shortMapper.toDto(same(f1), same(storage), any(Duration.class))).thenReturn(s1);
        when(shortMapper.toDto(same(f2), same(storage), any(Duration.class))).thenReturn(s2);

        Page<FurnitureShortDto> result = service.getCatalog(pageable);

        verify(innerService).getAllUnique(pageable);
        verify(shortMapper).toDto(same(f1), same(storage), any(Duration.class));
        verify(shortMapper).toDto(same(f2), same(storage), any(Duration.class));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).containsExactly(s1, s2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
    }
}
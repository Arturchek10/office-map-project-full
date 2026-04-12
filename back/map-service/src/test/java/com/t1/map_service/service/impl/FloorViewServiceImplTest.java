package com.t1.map_service.service.impl;

import com.t1.map_service.dto.floor.FloorViewDto;
import com.t1.map_service.dto.furniture.FurnitureDto;
import com.t1.map_service.dto.layer.BaseLayerDto;
import com.t1.map_service.dto.layer.LayerShortDto;
import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.mapper.LayerShortMapper;
import com.t1.map_service.mapper.MarkerShortMapper;
import com.t1.map_service.mapper.furniture.FurnitureMapper;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.model.entity.Layer;
import com.t1.map_service.model.entity.Marker;
import com.t1.map_service.repository.FloorRepository;
import com.t1.map_service.repository.LayerRepository;
import com.t1.map_service.repository.MarkerRepository;
import com.t1.map_service.service.photo.impl.FloorPhotoService;
import com.t1.map_service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloorViewServiceImplTest {

    @Mock FloorRepository floorRepository;
    @Mock LayerRepository layerRepository;
    @Mock MarkerRepository markerRepository;
    @Mock LayerShortMapper layerShortMapper;
    @Mock MarkerShortMapper markerShortMapper;
    @Mock FurnitureMapper furnitureMapper;
    @Mock FloorPhotoService photoService;
    @Mock FileStorageService storage;

    @InjectMocks FloorViewServiceImpl service;

    private Floor floor;
    private Layer baseLayer;
    private List<Layer> allLayers;
    private List<Marker> allMarkers;

    @BeforeEach
    void setUp() {
        floor = new Floor();
        floor.setId(1L);
        floor.setName("Floor-1");
        floor.setOrderNumber(2);

        baseLayer = new Layer();
        baseLayer.setId(100L);
        baseLayer.setName("Base");
        baseLayer.setBase(true);
        baseLayer.setFloor(floor);

        Layer other = new Layer();
        other.setId(101L);
        other.setName("Other");
        other.setBase(false);
        other.setFloor(floor);

        allLayers = List.of(baseLayer, other);

        Marker m1 = new Marker();
        m1.setId(200L);
        Marker m2 = new Marker();
        m2.setId(201L);
        allMarkers = List.of(m1, m2);

        // выставляем TTL так же, как будет в рантайме
        ReflectionTestUtils.setField(service, "presignMinutes", 10);
        service.init();
    }

    @Test
    void shouldReturnFullFloorView_whenAllDataPresent() {
        Furniture f1 = new Furniture();
        f1.setId(10L);
        f1.setName("Table");
        Furniture f2 = new Furniture();
        f2.setId(11L);
        f2.setName("Chair");
        floor.setFurnitures(List.of(f1, f2));

        when(floorRepository.findById(1L)).thenReturn(Optional.of(floor));
        when(layerRepository.findByFloorIdOrderByNameAsc(1L)).thenReturn(allLayers);
        when(layerRepository.findByFloorIdAndBaseTrue(1L)).thenReturn(Optional.of(baseLayer));
        when(markerRepository.findByLayer_Floor_Id(1L)).thenReturn(allMarkers);

        var layersShort = List.of(
                new LayerShortDto(100L, "Base", true),
                new LayerShortDto(101L, "Other", false)
        );
        when(layerShortMapper.toDtoList(allLayers)).thenReturn(layersShort);
        when(markerShortMapper.toDtoList(allMarkers)).thenReturn(List.of());

        FurnitureDto dto1 = new FurnitureDto(10L, "Table", "url1", null, null, null);
        FurnitureDto dto2 = new FurnitureDto(11L, "Chair", "url2", null, null, null);

        // стабы на перегрузку с контекстом
        when(furnitureMapper.toDto(eq(f1), eq(storage), any(Duration.class))).thenReturn(dto1);
        when(furnitureMapper.toDto(eq(f2), eq(storage), any(Duration.class))).thenReturn(dto2);

        when(photoService.presign(eq(floor), any(Duration.class))).thenReturn("http://photo.url");

        FloorViewDto result = service.getFloorView(1L);

        verify(floorRepository).findById(1L);
        verify(layerRepository).findByFloorIdOrderByNameAsc(1L);
        verify(layerRepository).findByFloorIdAndBaseTrue(1L);
        verify(markerRepository).findByLayer_Floor_Id(1L);
        verify(layerShortMapper).toDtoList(allLayers);
        verify(markerShortMapper).toDtoList(allMarkers);

        // проверяем, что вызывалась правильная перегрузка
        verify(furnitureMapper).toDto(eq(f1), eq(storage), any(Duration.class));
        verify(furnitureMapper).toDto(eq(f2), eq(storage), any(Duration.class));

        verify(photoService).presign(eq(floor), any(Duration.class));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Floor-1");
        assertThat(result.orderNumber()).isEqualTo(2);
        assertThat(result.photoUrl()).isEqualTo("http://photo.url");
        assertThat(result.layers()).containsExactlyElementsOf(layersShort);

        BaseLayerDto base = result.baseLayer();
        assertThat(base).isNotNull();
        assertThat(base.id()).isEqualTo(100L);
        assertThat(base.name()).isEqualTo("Base");
        assertThat(base.base()).isTrue();

        assertThat(result.furnitures()).containsExactlyInAnyOrder(dto1, dto2);
    }

    @Test
    void shouldHandleFloorWithoutFurniture() {
        floor.setFurnitures(null);

        when(floorRepository.findById(1L)).thenReturn(Optional.of(floor));
        when(layerRepository.findByFloorIdOrderByNameAsc(1L)).thenReturn(allLayers);
        when(layerRepository.findByFloorIdAndBaseTrue(1L)).thenReturn(Optional.of(baseLayer));
        when(markerRepository.findByLayer_Floor_Id(1L)).thenReturn(allMarkers);

        when(layerShortMapper.toDtoList(allLayers))
                .thenReturn(List.of(new LayerShortDto(100L, "Base", true)));
        when(markerShortMapper.toDtoList(allMarkers)).thenReturn(List.of());
        when(photoService.presign(eq(floor), any(Duration.class))).thenReturn(null);

        FloorViewDto result = service.getFloorView(1L);

        verify(photoService).presign(eq(floor), any(Duration.class));
        assertThat(result.photoUrl()).isNull();
        assertThat(result.furnitures()).isEmpty();
    }

    @Test
    void shouldThrowWhenFloorNotFound() {
        when(floorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFloorView(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Floor not found");

        verifyNoInteractions(layerRepository, markerRepository, layerShortMapper, markerShortMapper, furnitureMapper, photoService);
    }

    @Test
    void shouldThrowWhenBaseLayerMissing() {
        when(floorRepository.findById(1L)).thenReturn(Optional.of(floor));
        when(layerRepository.findByFloorIdOrderByNameAsc(1L)).thenReturn(allLayers);
        when(layerRepository.findByFloorIdAndBaseTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFloorView(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Base Layer not found");

        verify(layerRepository).findByFloorIdAndBaseTrue(1L);
        verifyNoInteractions(markerRepository, furnitureMapper, photoService);
    }
}
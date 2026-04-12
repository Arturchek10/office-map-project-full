package com.t1.map_service.service.impl;

import com.t1.map_service.dto.floor.FloorCreateRequest;
import com.t1.map_service.dto.floor.FloorPlanPatchRequest;
import com.t1.map_service.dto.floor.FloorUpdateRequest;
import com.t1.map_service.exception.EntityAlreadyExistsException;
import com.t1.map_service.mapper.FloorMapper;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Office;
import com.t1.map_service.observer.event.FloorCreatedEvent;
import com.t1.map_service.repository.FloorRepository;
import com.t1.map_service.service.inner.FloorInnerService;
import com.t1.map_service.service.inner.OfficeInnerService;
import com.t1.map_service.service.photo.impl.FloorPhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloorServiceImplTest {

    @Mock FloorRepository repository;
    @Mock FloorMapper mapper;
    @Mock OfficeInnerService officeInnerService;
    @Mock FloorInnerService floorInnerService;
    @Mock ApplicationEventPublisher publisher;
    @Mock FloorPhotoService photoService;

    @InjectMocks FloorServiceImpl service;

    private Office office;
    private Floor floor;
    private Floor saved;

    @BeforeEach
    void setUp() {
        office = new Office();
        office.setId(5L);
        office.setName("HQ");

        floor = new Floor();
        floor.setName("1 этаж");
        floor.setOrderNumber(1);

        saved = new Floor();
        saved.setId(10L);
        saved.setName("1 этаж");
        saved.setOrderNumber(1);
        saved.setOffice(office);
    }

    // --------- CREATE ---------

    @Test
    void shouldCreateFloorSuccessfully_andPublishEvent() {
        FloorCreateRequest req = new FloorCreateRequest("1 этаж", 1);

        when(repository.existsByOfficeIdAndOrderNumber(5L, 1)).thenReturn(false);
        when(officeInnerService.getEntityById(5L)).thenReturn(office);
        when(mapper.toEntity(req)).thenReturn(floor);
        when(repository.save(floor)).thenReturn(saved);

        Long id = service.create(req, 5L);

        assertThat(id).isEqualTo(10L);
        assertThat(floor.getOffice()).isSameAs(office);

        verify(repository).existsByOfficeIdAndOrderNumber(5L, 1);
        verify(officeInnerService).getEntityById(5L);
        verify(mapper).toEntity(req);
        verify(repository).save(floor);

        ArgumentCaptor<FloorCreatedEvent> eventCaptor = ArgumentCaptor.forClass(FloorCreatedEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().layerId()).isEqualTo(10L);
    }

    @Test
    void shouldThrowWhenFloorAlreadyExistsOnCreate() {
        FloorCreateRequest req = new FloorCreateRequest("1 этаж", 1);
        when(repository.existsByOfficeIdAndOrderNumber(5L, 1)).thenReturn(true);

        assertThatThrownBy(() -> service.create(req, 5L))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Floor already exists");

        verify(repository).existsByOfficeIdAndOrderNumber(5L, 1);
        verifyNoInteractions(officeInnerService, mapper, publisher);
        verify(repository, never()).save(any());
    }

    // --------- UPDATE (name/orderNumber) ---------

    @Test
    void shouldUpdateFloorSuccessfully() {
        FloorUpdateRequest dto = new FloorUpdateRequest("Новый этаж", 2);

        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        // при апдейте сервис проверяет конфликт по новому orderNumber
        when(repository.existsByOfficeIdAndOrderNumber(5L, 2)).thenReturn(false);

        service.update(10L, dto);

        verify(floorInnerService).getEntityById(10L);
        verify(repository).existsByOfficeIdAndOrderNumber(5L, 2);
        verify(mapper).update(saved, dto);
        // dirty checking: save() не обязателен
        verify(repository, never()).save(any(Floor.class));
    }

    @Test
    void shouldThrowWhenOrderNumberConflictOnUpdate() {
        FloorUpdateRequest dto = new FloorUpdateRequest("Новый этаж", 2);

        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        when(repository.existsByOfficeIdAndOrderNumber(5L, 2)).thenReturn(true);

        assertThatThrownBy(() -> service.update(10L, dto))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Floor already exists");

        verify(mapper, never()).update(any(), any());
        verify(repository, never()).save(any());
    }

    // --------- UPDATE IMAGE (plan) ---------

    @Test
    void shouldUpdateImage_removePhoto_whenHasExisting() {
        FloorPlanPatchRequest dto = new FloorPlanPatchRequest(true);
        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        when(photoService.hasPhoto(saved)).thenReturn(true);

        service.updateImage(10L, dto, null);

        verify(photoService).removePhoto(saved);
        verify(repository).save(saved);
        verify(photoService, never()).uploadPhoto(any(), any());
        verify(photoService, never()).replacePhoto(any(), any());
    }

    @Test
    void shouldUpdateImage_removePhoto_noExisting_keepsState() {
        FloorPlanPatchRequest dto = new FloorPlanPatchRequest(true);
        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        when(photoService.hasPhoto(saved)).thenReturn(false);

        service.updateImage(10L, dto, null);

        verify(photoService, never()).removePhoto(any());
        // сервис всё равно делает save(floor) после ветки remove
        verify(repository).save(saved);
        verify(photoService, never()).uploadPhoto(any(), any());
        verify(photoService, never()).replacePhoto(any(), any());
    }

    @Test
    void shouldUpdateImage_replacePhoto_whenHasExistingPhoto() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        FloorPlanPatchRequest dto = new FloorPlanPatchRequest(false);

        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        when(photoService.hasPhoto(saved)).thenReturn(true);

        service.updateImage(10L, dto, file);

        verify(photoService).replacePhoto(saved, file);
        verify(photoService, never()).uploadPhoto(any(), any());
        verify(repository).save(saved);
    }

    @Test
    void shouldUpdateImage_uploadPhoto_whenNoExistingPhoto() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        FloorPlanPatchRequest dto = new FloorPlanPatchRequest(false);

        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        when(photoService.hasPhoto(saved)).thenReturn(false);

        service.updateImage(10L, dto, file);

        verify(photoService).uploadPhoto(saved, file);
        verify(photoService, never()).replacePhoto(any(), any());
        verify(repository).save(saved);
    }

    @Test
    void shouldUpdateImage_skip_whenNoFileAndRemoveFalse() {
        FloorPlanPatchRequest dto = new FloorPlanPatchRequest(false);
        when(floorInnerService.getEntityById(10L)).thenReturn(saved);

        service.updateImage(10L, dto, null);

        verify(photoService, never()).uploadPhoto(any(), any());
        verify(photoService, never()).replacePhoto(any(), any());
        verify(repository, never()).save(any());
    }

    // --------- DELETE ---------

    @Test
    void shouldDeleteFloor_andRemovePhoto() {
        when(floorInnerService.getEntityById(10L)).thenReturn(saved);

        service.delete(10L);

        verify(photoService).removePhoto(saved);
        verify(repository).delete(saved);
    }

    @Test
    void shouldDeleteFloor_evenIfRemovePhotoFails() {
        when(floorInnerService.getEntityById(10L)).thenReturn(saved);
        doThrow(new RuntimeException("storage error")).when(photoService).removePhoto(saved);

        service.delete(10L);

        verify(repository).delete(saved);
    }
}
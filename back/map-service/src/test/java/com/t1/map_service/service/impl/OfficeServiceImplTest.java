package com.t1.map_service.service.impl;

import com.t1.map_service.dto.office.OfficeCreateRequest;
import com.t1.map_service.dto.office.OfficeDto;
import com.t1.map_service.dto.office.OfficeShortDto;
import com.t1.map_service.dto.office.OfficeUpdateRequest;
import com.t1.map_service.exception.EntityAlreadyExistsException;
import com.t1.map_service.mapper.OfficeMapper;
import com.t1.map_service.mapper.OfficeShortMapper;
import com.t1.map_service.model.entity.Office;
import com.t1.map_service.repository.OfficeRepository;
import com.t1.map_service.service.inner.OfficeInnerService;
import com.t1.map_service.service.photo.impl.OfficePhotoService;
import com.t1.map_service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceImplTest {

    @Mock OfficeRepository repository;
    @Mock OfficeInnerService officeInnerService;
    @Mock OfficeMapper mapper;
    @Mock OfficeShortMapper shortMapper;
    @Mock FileStorageService storage;
    @Mock OfficePhotoService photoService;

    @InjectMocks OfficeServiceImpl service;

    private Office office;
    private OfficeDto dto;

    @BeforeEach
    void setUp() {
        office = new Office();
        office.setId(1L);
        office.setName("HQ");
        office.setAddress("Москва, ул. Пушкина, 1");
        office.setLatitude(55.0);
        office.setLongitude(37.0);
        office.setCity("Москва");

        dto = new OfficeDto(
                1L, "HQ", "Москва, ул. Пушкина, 1",
                55.0, 37.0, "Москва", null
        );

        service.init();
    }

    @Test
    void shouldCreateOfficeWithoutPhoto() {
        OfficeCreateRequest req = new OfficeCreateRequest("HQ", office.getAddress(), 55.0, 37.0, "Москва");

        when(repository.existsByAddressIgnoreCase(office.getAddress())).thenReturn(false);
        when(mapper.toEntity(req)).thenReturn(office);
        when(repository.save(office)).thenReturn(office);
        when(mapper.toDto(any(Office.class), same(storage), any(Duration.class))).thenReturn(dto);

        OfficeDto result = service.create(req, null);

        verify(repository).existsByAddressIgnoreCase(office.getAddress());
        verify(repository, times(1)).save(office);
        verify(photoService, never()).uploadPhoto(any(), any());
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldCreateOfficeWithPhoto() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        OfficeCreateRequest req = new OfficeCreateRequest("HQ", office.getAddress(), 55.0, 37.0, "Москва");

        when(repository.existsByAddressIgnoreCase(office.getAddress())).thenReturn(false);
        when(mapper.toEntity(req)).thenReturn(office);
        when(repository.save(office)).thenReturn(office);
        when(mapper.toDto(any(Office.class), same(storage), any(Duration.class))).thenReturn(dto);

        OfficeDto result = service.create(req, file);

        verify(photoService).uploadPhoto(office, file);
        verify(repository, times(2)).save(office); // первый раз до фото, второй — после upload
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldThrowWhenAddressExistsOnCreate() {
        OfficeCreateRequest req = new OfficeCreateRequest("HQ", office.getAddress(), 55.0, 37.0, "Москва");
        when(repository.existsByAddressIgnoreCase(office.getAddress())).thenReturn(true);

        assertThatThrownBy(() -> service.create(req, null))
                .isInstanceOf(EntityAlreadyExistsException.class);

        verify(repository).existsByAddressIgnoreCase(office.getAddress());
        verify(repository, never()).save(any());
        verify(photoService, never()).uploadPhoto(any(), any());
    }

    @Test
    void shouldDeleteOfficeAndRemovePhoto() {
        when(officeInnerService.getEntityById(1L)).thenReturn(office);

        service.delete(1L);

        verify(photoService).removePhoto(office);
        verify(repository).delete(office);
    }

    @Test
    void shouldDeleteOfficeEvenIfRemovePhotoFails() {
        when(officeInnerService.getEntityById(1L)).thenReturn(office);
        doThrow(new RuntimeException("remove failed")).when(photoService).removePhoto(office);

        service.delete(1L);

        verify(repository).delete(office);
    }

    @Test
    void shouldGetOffice() {
        OfficeShortDto shortDto = new OfficeShortDto(1L, "HQ", null, List.of());

        when(officeInnerService.getEntityByIdWithFloorsOrdered(1L)).thenReturn(office);
        when(shortMapper.toDto(office)).thenReturn(shortDto);

        OfficeShortDto result = service.getById(1L);

        verify(officeInnerService).getEntityByIdWithFloorsOrdered(1L);
        verify(shortMapper).toDto(office);
        assertThat(result).isEqualTo(shortDto);
    }

    @Test
    void shouldUpdateAndReplacePhoto() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        OfficeUpdateRequest update = new OfficeUpdateRequest("HQ", "Новая улица", null);

        when(officeInnerService.getEntityById(1L)).thenReturn(office);
        when(mapper.toDto(any(Office.class), same(storage), any(Duration.class))).thenReturn(dto);

        OfficeDto result = service.update(1L, update, file);

        verify(photoService).replacePhoto(office, file);
        verify(photoService, never()).removePhoto(any());
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldUpdateAndRemovePhoto() {
        OfficeUpdateRequest update = new OfficeUpdateRequest("HQ", "Новая улица", true);

        when(officeInnerService.getEntityById(1L)).thenReturn(office);
        when(mapper.toDto(any(Office.class), same(storage), any(Duration.class))).thenReturn(dto);

        OfficeDto result = service.update(1L, update, null);

        verify(photoService).removePhoto(office);
        verify(photoService, never()).replacePhoto(any(), any());
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldUpdateAddressSuccessfullyWhenUnique() {
        OfficeUpdateRequest update = new OfficeUpdateRequest("HQ", "Новая улица", null);

        when(officeInnerService.getEntityById(1L)).thenReturn(office);
        when(repository.existsByAddressIgnoreCaseAndIdNot("Новая улица", 1L)).thenReturn(false);
        when(mapper.toDto(any(Office.class), same(storage), any(Duration.class))).thenReturn(dto);

        OfficeDto result = service.update(1L, update, null);

        verify(photoService, never()).removePhoto(any());
        verify(photoService, never()).replacePhoto(any(), any());
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldThrowWhenUpdateAddressConflict() {
        OfficeUpdateRequest update = new OfficeUpdateRequest("HQ", "Новая улица", null);

        when(officeInnerService.getEntityById(1L)).thenReturn(office);
        when(repository.existsByAddressIgnoreCaseAndIdNot("Новая улица", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L, update, null))
                .isInstanceOf(EntityAlreadyExistsException.class);

        verify(photoService, never()).removePhoto(any());
        verify(photoService, never()).replacePhoto(any(), any());
    }

    @Test
    void shouldReturnAllOffices() {
        Office office2 = new Office();
        office2.setId(2L);
        office2.setName("SPB");
        office2.setAddress("СПБ");
        office2.setLatitude(59.0);
        office2.setLongitude(30.0);
        office2.setCity("СПб");

        OfficeDto dto2 = new OfficeDto(2L, "SPB", "СПБ", 59.0, 30.0, "СПб", null);

        when(repository.findAll()).thenReturn(List.of(office, office2));
        when(mapper.toDto(any(Office.class), any(), any())).thenReturn(dto, dto2);

        List<OfficeDto> result = service.getAll();

        verify(repository).findAll();
        assertThat(result).containsExactly(dto, dto2);
    }

    @Test
    void shouldReturnEmptyListWhenNoOffices() {
        when(repository.findAll()).thenReturn(List.of());

        List<OfficeDto> result = service.getAll();

        verify(repository).findAll();
        assertThat(result).isEmpty();
    }
}
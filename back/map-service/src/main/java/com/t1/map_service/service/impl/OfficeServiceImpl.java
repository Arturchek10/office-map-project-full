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
import com.t1.map_service.service.OfficeService;
import com.t1.map_service.service.inner.OfficeInnerService;
import com.t1.map_service.service.photo.impl.OfficePhotoService;
import com.t1.map_service.storage.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository repository;
    private final OfficeInnerService officeInnerService;
    private final OfficeMapper mapper;
    private final OfficeShortMapper shortMapper;

    private final FileStorageService storage;
    private final OfficePhotoService photoService;

    @Value("${app.files.presign_minutes:10}")
    private int presignMinutes;

    private Duration durationTtl;

    @PostConstruct
    public void init() {
        this.durationTtl = Duration.ofMinutes(presignMinutes);
    }

    @Override
    @Transactional
    public OfficeDto create(OfficeCreateRequest request, MultipartFile photo) {
        log.debug("Create office: start (address={})", request.address());

        if(repository.existsByAddressIgnoreCase(request.address())){
            log.warn("Create office: conflict — office already exists (address={})", request.address());
            throw new EntityAlreadyExistsException("Office with address=%s already exists".formatted(request.address()));
        }

        Office saved = repository.save(mapper.toEntity(request));

        if (photo != null && !photo.isEmpty()) {
            photoService.uploadPhoto(saved, photo);
            saved = repository.save(saved);
        }

        // TODO удаление фото из стораджа при откате транзакции
        log.info("Office created (officeId={}, address={})", saved.getId(), saved.getAddress());
        return mapper.toDto(saved, storage, durationTtl);
    }

    @Override
    @Transactional
    public void delete(Long officeId) {
        log.debug("Delete office: start (officeId={})", officeId);

        Office officeToDelete = officeInnerService.getEntityById(officeId);

        try {
            photoService.removePhoto(officeToDelete);
        } catch (Exception e) {
            log.warn("Delete office: failed to remove photo from MinIO (officeId={})", officeId, e);
        }

        repository.delete(officeToDelete);

        log.info("Office deleted (officeId={}, address={})", officeId, officeToDelete.getAddress());
    }

    @Override
    public OfficeShortDto getById(Long officeId) {
        log.debug("Get office: start (officeId={})", officeId);

        Office office = officeInnerService.getEntityByIdWithFloorsOrdered(officeId);

        log.debug("Get office: success (officeId={}, address={})", office.getId(), office.getAddress());
        return shortMapper.toDto(office);
    }

    @Override
    @Transactional
    public OfficeDto update(Long officeId, OfficeUpdateRequest dto, MultipartFile photo) {
        log.debug("Update office: start (officeId={})", officeId);

        Office office = officeInnerService.getEntityById(officeId);

        // если адрес передан и отличается - проверяем уникальность
        if (dto.address() != null) {
            String newAddress = dto.address().trim();
            String current = office.getAddress();
            boolean changed = current == null || !current.equalsIgnoreCase(newAddress);

            if (changed) {
                log.debug("Update office: address change detected (officeId={}, from='{}', to='{}')",
                        officeId, current, newAddress);

                if (repository.existsByAddressIgnoreCaseAndIdNot(newAddress, officeId)) {
                    log.warn("Update office: conflict — address already in use (officeId={}, address={})",
                            officeId, newAddress);
                    throw new EntityAlreadyExistsException("Office with address=%s already exists".formatted(newAddress));
                }
            }
        }

        mapper.update(office, dto);

        if (Boolean.TRUE.equals(dto.removePhoto())) {
            photoService.removePhoto(office);
        } else if (photo != null && !photo.isEmpty()) {
            photoService.replacePhoto(office, photo);
        }

        log.info("Office updated (officeId={}, address={})", officeId, office.getAddress());
        return mapper.toDto(office, storage, durationTtl);
    }

    @Override
    public List<OfficeDto> getAll() {
        return repository.findAll()
                .stream()
                .map(office -> mapper.toDto(office, storage, durationTtl))
                .collect(Collectors.toList());
    }
}

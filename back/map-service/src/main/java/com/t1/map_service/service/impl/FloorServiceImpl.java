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
import com.t1.map_service.service.FloorService;
import com.t1.map_service.service.inner.FloorInnerService;
import com.t1.map_service.service.inner.OfficeInnerService;
import com.t1.map_service.service.photo.impl.FloorPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FloorServiceImpl implements FloorService {

    private final FloorRepository repository;
    private final FloorMapper mapper;
    private final OfficeInnerService officeInnerService;
    private final FloorInnerService floorInnerService;

    private final ApplicationEventPublisher publisher;
    private final FloorPhotoService photoService;

    @Override
    @Transactional
    public Long create(FloorCreateRequest dto, Long officeId) {
        log.debug("Create floor: start (officeId={}, orderNumber={})", officeId, dto.orderNumber());

        if (repository.existsByOfficeIdAndOrderNumber(officeId, dto.orderNumber())) {
            log.warn("Create floor: conflict — already exists (officeId={}, orderNumber={})", officeId, dto.orderNumber());
            throw new EntityAlreadyExistsException("Floor already exists");
        }

        Office office = officeInnerService.getEntityById(officeId);
        Floor floor = mapper.toEntity(dto);
        floor.setOffice(office);

        Floor saved = repository.save(floor);

        publisher.publishEvent(new FloorCreatedEvent(saved.getId()));
        log.info("Floor created (floorId={}, officeId={}, orderNumber={})",
                saved.getId(), officeId, saved.getOrderNumber());
        return saved.getId();
    }

    @Override
    @Transactional
    public void update(Long floorId, FloorUpdateRequest dto) {
        log.debug("Update floor: start (floorId={})", floorId);
        Floor floor = floorInnerService.getEntityById(floorId);

        if (repository.existsByOfficeIdAndOrderNumber(floor.getOffice().getId(), dto.orderNumber())) {
            log.warn("Update floor: conflict — already exists (officeId={}, orderNumber={})", floor.getOffice().getId(), dto.orderNumber());
            throw new EntityAlreadyExistsException("Floor with id=%d already exists".formatted(floorId));
        }

        mapper.update(floor, dto);
        log.debug("Update floor: success (floorId={})", floorId);
    }

    @Override
    @Transactional
    public void updateImage(Long floorId, FloorPlanPatchRequest dto, MultipartFile photo) {
        log.debug("Upload floor image: start (floorId={})", floorId);
        Floor floor = floorInnerService.getEntityById(floorId);

        if (Boolean.TRUE.equals(dto.removePhoto())) {
            if (photoService.hasPhoto(floor)) {
                photoService.removePhoto(floor);
                log.info("Upload floor image: removed (floorId={})", floorId);
            } else {
                log.debug("Upload floor image: no photo to remove (floorId={})", floorId);
            }
            repository.save(floor);
            return;
        }

        // если передан новый файл
        if (photo != null && !photo.isEmpty()) {
            if (photoService.hasPhoto(floor)) {
                photoService.replacePhoto(floor, photo);
                log.info("Upload floor image: replaced (floorId={}, key={})", floorId, floor.getPhotoKey());
            } else {
                photoService.uploadPhoto(floor, photo);
                log.info("Upload floor image: uploaded (floorId={}, key={})", floorId, floor.getPhotoKey());
            }
            repository.save(floor);
        } else {
            log.debug("Upload floor image: skipped, no file provided (floorId={})", floorId);
        }
    }

    @Override
    @Transactional
    public void delete(Long floorId) {
        log.debug("Delete floor: start (floorId={})", floorId);
        Floor floor = floorInnerService.getEntityById(floorId);

        try {
            photoService.removePhoto(floor);
        } catch (Exception e) {
            log.warn("Delete floor: failed to remove photo from storage (floorId={})", floorId, e);
        }

        repository.delete(floor);
        log.info("Floor deleted (floorId={})", floorId);
    }
}
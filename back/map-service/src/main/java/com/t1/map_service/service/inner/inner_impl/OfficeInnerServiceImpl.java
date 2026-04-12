package com.t1.map_service.service.inner.inner_impl;

import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.model.entity.Office;
import com.t1.map_service.repository.OfficeRepository;
import com.t1.map_service.service.inner.OfficeInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfficeInnerServiceImpl implements OfficeInnerService {

    private final OfficeRepository repository;

    @Override
    public Office getEntityById(Long officeId) {
        return repository.findById(officeId)
                .orElseThrow(() -> {
                    log.warn("Find office: not found (officeId={})", officeId);
                    return new EntityNotFoundException("Office with id=%d not found".formatted(officeId));
                });
    }

    @Override
    public Office getEntityByIdWithFloorsOrdered(Long officeId) {
        return repository.findByIdWithFloorsOrdered(officeId)
                .orElseThrow(() -> {
                    log.warn("Find office: not found (officeId={})", officeId);
                    return new EntityNotFoundException("Office with id=%d not found".formatted(officeId));
                });
    }

    @Override
    public boolean existsEntityById(Long officeId) {
        return repository.existsById(officeId);
    }


}

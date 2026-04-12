package com.t1.map_service.service;

import com.t1.map_service.dto.office.OfficeCreateRequest;
import com.t1.map_service.dto.office.OfficeDto;
import com.t1.map_service.dto.office.OfficeShortDto;
import com.t1.map_service.dto.office.OfficeUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OfficeService {

    OfficeShortDto getById(Long officeId);

    OfficeDto update(Long officeId, OfficeUpdateRequest request, MultipartFile photo);

    List<OfficeDto> getAll();

    OfficeDto create(OfficeCreateRequest request, MultipartFile photo);

    void delete(Long officeId);
}

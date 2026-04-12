package com.t1.map_service.security.service.impl;

import com.t1.map_service.dto.office_admin.PromoteProjectAdminRequest;
import com.t1.map_service.dto.office_admin.RevokeProjectAdminRequest;
import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.security.model.OfficeAdmin;
import com.t1.map_service.security.repository.OfficeAdminRepository;
import com.t1.map_service.security.service.OfficeAdminService;
import com.t1.map_service.service.inner.OfficeInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfficeAdminServiceImpl implements OfficeAdminService {

    private static final String DEFAULT_CREATED_BY = "system";

    private final OfficeAdminRepository officeAdminRepository;
    private final OfficeInnerService officeInnerService;

    @Override
    @Transactional
    public void promoteProjectAdmin(Long officeId, PromoteProjectAdminRequest request) {
        if (!officeInnerService.existsEntityById(officeId)) {
            throw new EntityNotFoundException("Office with id=%d not found".formatted(officeId));
        }

        // Если есть связь - пропускаем
        if (officeAdminRepository.existsByLoginAndOfficeId(request.login(), officeId)) {
            log.info("Project admin already assigned (officeId={}, login={})", officeId, request.login());
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdBy = authentication != null ? authentication.getName() : DEFAULT_CREATED_BY;

        OfficeAdmin record = OfficeAdmin.builder()
                .officeId(officeId)
                .login(request.login())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();

        officeAdminRepository.save(record);
        log.info("Project admin assigned (officeId={}, login={}, createdBy={})", officeId, request.login(), createdBy);

        //TODO Публикация события в RabbitMQ для обновления роли в auth-service
        //ProjectAdminPromotedEvent
        // rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }

    @Override
    public void revokeProjectAdmin(Long officeId, RevokeProjectAdminRequest request) {
        if (!officeInnerService.existsEntityById(officeId)) {
            throw new EntityNotFoundException("Office not found");
        }

        officeAdminRepository.findByLoginAndOfficeId(request.login(), officeId)
                .ifPresentOrElse(
                        record -> {
                            officeAdminRepository.delete(record);
                            log.info("Project admin revoked (officeId={}, login={})", officeId, request.login());
                            // TODO Публикация события в RabbitMQ о снятии роли
                        },
                        () -> log.info("No project admin record to revoke (officeId={}, login={})", officeId, request.login())
                );
    }
}

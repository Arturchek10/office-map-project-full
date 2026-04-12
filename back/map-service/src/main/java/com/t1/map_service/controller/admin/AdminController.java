package com.t1.map_service.controller.admin;

// DTO (объекты для передачи данных в запросе)
import com.t1.map_service.dto.office_admin.PromoteProjectAdminRequest;
import com.t1.map_service.dto.office_admin.RevokeProjectAdminRequest;
// Сервис, где лежит основная бизнес-логика
import com.t1.map_service.security.service.OfficeAdminService;
// Валидация входных данных
import jakarta.validation.Valid;
// Lombok — автоматически создаёт конструктор
import lombok.RequiredArgsConstructor;
// HTTP-ответы
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Безопасность (проверка прав)
import org.springframework.security.access.prepost.PreAuthorize;
// Аннотации для REST API
import org.springframework.web.bind.annotation.*;

// Говорим Spring: это REST-контроллер (принимает HTTP-запросы)
@RestController
// Базовый URL для всех методов в этом классе
// {officeId} — это переменная из URL
@RequestMapping("/api/v1/admin/{officeId}")
// Lombok: создаёт конструктор для final-полей (чтобы Spring мог внедрить зависимости)
@RequiredArgsConstructor
public class AdminController {
    // Сервис (бизнес-логика), который делает всю работу
    private final OfficeAdminService officeAdminService;

    // Назначение PROJECT_ADMIN в офисе

    // Обрабатывает POST-запрос
    // POST /api/v1/admin/{officeId}
    @PostMapping

    // Проверка прав:
    // можно ли текущему пользователю управлять этим офисом
    @PreAuthorize("@perm.canManageOffice(authentication, #officeId)")
    public ResponseEntity<Void> promoteProjectAdmin(
            // Берём officeId из URL (например: /admin/42)
            @PathVariable Long officeId,
            // Берём тело запроса (JSON) и валидируем его
            @RequestBody @Valid PromoteProjectAdminRequest request
    ) {
        // Вызываем сервис — там происходит вся логика
        officeAdminService.promoteProjectAdmin(officeId, request);
        // Возвращаем HTTP 201 (Created — успешно создано)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // =========================
    // УДАЛЕНИЕ АДМИНА ПРОЕКТА
    // =========================

    // Обрабатывает DELETE-запрос
    // DELETE /api/v1/admin/{officeId}
    @DeleteMapping
    // Проверка прав:
    // можно ли удалить администратора у конкретного пользователя (request.login())
    @PreAuthorize("@perm.canRevokeProjectAdmin(authentication, #officeId, #request.login())")
    public ResponseEntity<Void> revokeProjectAdmin(
            // Берём officeId из URL
            @PathVariable Long officeId,
            // Берём тело запроса (JSON)
            @RequestBody @Valid RevokeProjectAdminRequest request
    ) {
        // Вызываем сервис — там удаляется админ
        officeAdminService.revokeProjectAdmin(officeId, request);
        // Возвращаем HTTP 204 (No Content — успешно, но без ответа)
        return ResponseEntity.noContent().build();
    }
}

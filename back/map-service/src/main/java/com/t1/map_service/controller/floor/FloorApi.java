package com.t1.map_service.controller.floor;

import com.t1.map_service.dto.floor.FloorCreateRequest;
import com.t1.map_service.dto.floor.FloorPlanPatchRequest;
import com.t1.map_service.dto.floor.FloorUpdateRequest;
import com.t1.map_service.dto.floor.FloorViewDto;
import com.t1.map_service.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FloorApi {

    // ========================= GET /floors/{floorId} =========================
    @Operation(
            summary = "Получить этаж с его слоями и базовым слоем",
            description = """
                Возвращает полное представление этажа.
                Состав ответа:
                 • layers — все слои этажа (для переключения на фронте);
                 • baseLayer — базовый слой, в нём лежат все маркеры этажа;
                 • furnitures — мебель, привязанная к этажу;
                 • photoUrl — presigned URL плана этажа (подходит для <img src="...">).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Этаж найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FloorViewDto.class))),
            @ApiResponse(responseCode = "404", description = "Этаж не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<FloorViewDto> getFloor(
            @Parameter(description = "ID этажа", example = "12") Long floorId
    );

    // ========================= POST /floors/{officeId} =======================
    @Operation(
            summary = "Создать этаж",
            description = """
                Создаёт этаж в указанном офисе (по officeId).
                После сохранения публикуется событие, которое создаёт базовый слой.
                Поле orderNumber должно быть уникально в пределах офиса.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Этаж создан",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FloorViewDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Офис не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Конфликт orderNumber в офисе",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FloorCreateRequest.class),
                    examples = @ExampleObject(value = """
                        {"name":"Первый этаж","orderNumber":1}
                    """))
    )
    ResponseEntity<FloorViewDto> create(
            @Parameter(description = "Тело запроса — FloorCreateRequest")  FloorCreateRequest dto,
            @Parameter(description = "ID офиса", example = "10") Long officeId
    );

    // ========================= PATCH /floors/{floorId} =======================
    @Operation(
            summary = "Обновить этаж",
            description = """
                Частично обновляет свойства этажа (name, orderNumber).
                Фото плана меняется отдельным эндпоинтом /plan/{floorId}.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Этаж обновлён",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FloorViewDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Этаж не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Конфликт orderNumber в офисе",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FloorUpdateRequest.class),
                    examples = @ExampleObject(value = """
                        {"name":"Второй этаж","orderNumber":2}
                    """))
    )
    ResponseEntity<FloorViewDto> update(
            @Parameter(description = "ID этажа", example = "12") Long floorId,
            @Parameter(description = "Тело запроса — FloorUpdateRequest")  FloorUpdateRequest dto
    );

    // ============== PATCH /floors/plan/{floorId} (upload/replace/remove) ==============
    @Operation(
            summary = "Загрузить/обновить/удалить план этажа (фото)",
            description = """
                Управление фото плана этажа.
                Тип запроса: multipart/form-data.
                Правила:
                 • передайте photo — картинка будет загружена/заменена;
                 • передайте {"removePhoto":true} в JSON-части data — фото будет удалено;
                 • нельзя одновременно прислать photo и установить removePhoto=true.
                Поддерживаемые форматы: image/png, image/jpeg, image/svg+xml.
                В ответе photoUrl — presigned URL из MinIO (или null, если фото удалено).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото плана обновлено/удалено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FloorViewDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации / конфликт параметров фото",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Этаж не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Ошибка файлового хранилища (MinIO)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    ResponseEntity<FloorViewDto> uploadPlan(
            @Parameter(description = "ID этажа", example = "12") Long floorId,

            @Parameter(
                    name = "data",
                    description = "JSON FloorPlanPatchRequest. Пример: {\"removePhoto\":false}",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FloorPlanPatchRequest.class)),
                    examples = {
                            @ExampleObject(name = "Upload JSON", value = "{\"removePhoto\":false}"),
                            @ExampleObject(name = "Remove JSON", value = "{\"removePhoto\":true}")
                    }
            )  FloorPlanPatchRequest data,

            @Parameter(
                    name = "photo",
                    description = "Изображение плана (PNG/JPEG/SVG) — опционально",
                    required = false,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            ) MultipartFile photo
    );

    // ========================= DELETE /floors/{floorId} ======================
    @Operation(summary = "Удалить этаж", description = "Удаляет этаж. Фото плана (если было) также удаляется.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Этаж удалён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Этаж не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID этажа", example = "12") Long floorId
    );
}
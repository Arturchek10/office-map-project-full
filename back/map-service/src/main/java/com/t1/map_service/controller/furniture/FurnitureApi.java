package com.t1.map_service.controller.furniture;

import com.t1.map_service.dto.furniture.FurnitureCreateRequest;
import com.t1.map_service.dto.furniture.FurnitureDto;
import com.t1.map_service.dto.furniture.FurnitureMoveRequest;
import com.t1.map_service.dto.furniture.FurniturePatchRequest;
import com.t1.map_service.dto.furniture.FurniturePatchUiRequest;
import com.t1.map_service.dto.furniture.FurniturePlaceRequest;
import com.t1.map_service.dto.furniture.FurnitureShortDto;
import com.t1.map_service.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FurnitureApi {

    // ====================== GET /furniture/{furnitureId} ======================
    @Operation(
            summary = "Получить мебель",
            description = "Возвращает мебель по её ID. Поле photoUrl — presigned URL из MinIO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureDto.class))),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<FurnitureDto> getFurniture(
            @Parameter(description = "ID мебели", example = "15") Long furnitureId
    );

    // ====================== GET /furniture/catalog ============================
    @Operation(
            summary = "Получить каталог мебели",
            description = "Возвращает уникальные объекты мебели (каталог). Поле photoUrl — presigned URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Каталог получен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<Page<FurnitureShortDto>> getFurnitureCatalog(
            @Parameter(description = "Параметры пагинации и сортировки")
            org.springframework.data.domain.Pageable pageable
    );

    // ====================== POST /furniture (catalog item) ====================
    @Operation(
            summary = "Создать мебель (каталог)",
            description = """
                    Тип запроса: multipart/form-data.
                    Части формы:
                      • data  — JSON FurnitureCreateRequest
                      • photo — файл (PNG/JPEG/SVG)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Мебель создана",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации/формата файла",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Мебель уже существует",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
    )
    ResponseEntity<FurnitureDto> create(
            @Parameter(
                    name = "data",
                    description = "JSON FurnitureCreateRequest",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureCreateRequest.class)),
                    examples = @ExampleObject(value = "{\"name\":\"Шкаф офисный\"}")
            ) @Valid FurnitureCreateRequest request,

            @Parameter(
                    name = "photo",
                    description = "Изображение (PNG/JPEG/SVG)",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )  MultipartFile photo
    );

    // ====================== POST /furniture/{floorId} (place) =================
    @Operation(
            summary = "Разместить мебель на этаже",
            description = """
                Размещает мебель на этаже.
                Фронт передаёт JSON с именем мебели, позицией и presigned URL изображения.
                Бэкенд извлекает из URL objectKey и сохраняет его в photoKey (файл не загружается).
                Если мебели с таким именем нет в каталоге — 404.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Мебель размещена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (нельзя извлечь ключ из photoUrl и т.п.)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Этаж не найден / мебели нет в каталоге",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FurniturePlaceRequest.class),
                    examples = @ExampleObject(
                            name = "Пример размещения по URL",
                            value = """
                                {
                                  "name": "Стол",
                                  "position": { "position_x": 3.5, "position_y": 7.0 },
                                  "photoUrl": "http://10.10.146.211:9000/map-service/offices/225/photo.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20250820%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250820T131016Z&X-Amz-Expires=600&X-Amz-SignedHeaders=host&X-Amz-Signature=9dc9a24b64943d320a790179b1bdf6088e3a2e5141c1a4f7d2eec883480d80a6"
                                }
                                """
                    )
            )
    )
    ResponseEntity<FurnitureDto> place(
            @Parameter(description = "ID этажа", example = "5")
            Long floorId,

            @Parameter(hidden = true) // тело описано в @RequestBody выше
            @Valid FurniturePlaceRequest request
    );

    // ====================== PATCH /furniture/move/{furnitureId} ===============
    @Operation(summary = "Переместить мебель", description = "Тип: application/json.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель перемещена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureDto.class))),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<FurnitureDto> move(
            @Parameter(description = "ID мебели", example = "15") Long furnitureId,
            @Parameter(description = "Новая позиция",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureMoveRequest.class)))
            @Valid FurnitureMoveRequest request
    );

    // ====================== PATCH /furniture/ui/{furnitureId} =================
    @Operation(
            summary = "Изменить UI мебели",
            description = "Тип: application/json. Ограничения: angle 0..359, sizeFactor > 0."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "UI обновлён",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<FurnitureDto> updateUi(
            @Parameter(description = "ID мебели", example = "15") Long furnitureId,
            @Parameter(description = "Параметры UI",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurniturePatchUiRequest.class)))
            @Valid FurniturePatchUiRequest request
    );

    // ====================== PATCH /furniture/{furnitureId} ====================
    @Operation(
            summary = "Обновить мебель",
            description = """
                    Тип запроса: multipart/form-data.
                    Части формы:
                      • data  — JSON FurniturePatchRequest
                      • photo — файл (опционально; заменяет текущее фото)
                    Нельзя одновременно передавать photo и removePhoto=true.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель обновлена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurnitureDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации / конфликт параметров фото",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
    )
    ResponseEntity<FurnitureDto> update(
            @Parameter(description = "ID мебели", example = "15") Long furnitureId,

            @Parameter(
                    name = "data",
                    description = "JSON FurniturePatchRequest",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FurniturePatchRequest.class)),
                    examples = @ExampleObject(value = "{\"name\":\"Стол переговорный\",\"removePhoto\":false}")
            ) @Valid FurniturePatchRequest request,

            @Parameter(
                    name = "photo",
                    description = "Изображение (PNG/JPEG/SVG) — опционально",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            ) MultipartFile photo
    );

    // ====================== DELETE /furniture/{furnitureId} ===================
    @Operation(summary = "Удалить мебель", description = "Удаляет мебель и её фото (если было) из MinIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Мебель удалена", content = @Content),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID мебели", example = "15") Long furnitureId
    );
}
package com.t1.map_service.controller.office;

import com.t1.map_service.dto.office.OfficeCreateRequest;
import com.t1.map_service.dto.office.OfficeDto;
import com.t1.map_service.dto.office.OfficeShortDto;
import com.t1.map_service.dto.office.OfficeUpdateRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OfficeApi {

    // ========================= GET /offices/{officeId} =======================
    @Operation(
            summary = "Получить офис",
            description = """
                    Возвращает офис по идентификатору.
                    startFloor — этаж, который рисовать первым.
                    floors — отсортированный по orderNumber список этажей.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Офис найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OfficeShortDto.class))),
            @ApiResponse(responseCode = "404", description = "Офис не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<OfficeShortDto> getOffice(
            @Parameter(description = "ID офиса", example = "5") Long officeId
    );

    // ========================= GET /offices =======================
    @Operation(
            summary = "Получить список всех офисов",
            description = """
                    Возвращает список офисов с координатами, городом и фото.
                    Поле photoUrl — presigned URL из MinIO.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список офисов получен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OfficeDto.class)))
    })
    ResponseEntity<List<OfficeDto>> getAllOffices();

    // ========================= POST /offices ================================
    @Operation(
            summary = "Создать офис",
            description = """
                    Тип запроса: multipart/form-data.
                    Части формы:
                      • data  — JSON OfficeCreateRequest
                      • photo — файл (PNG/JPEG/SVG), опционально
                    Адрес должен быть уникальным.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Офис создан",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OfficeDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации/формата файла",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Офис уже существует",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    ResponseEntity<OfficeDto> createOffice(
            @Parameter(
                    name = "data",
                    description = "JSON OfficeCreateRequest",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OfficeCreateRequest.class)),
                    examples = @ExampleObject(value = """
                            {"name":"HQ Office","address":"Nevsky 10","latitude":59.9343,"longitude":30.3351,"city":"Saint-Petersburg"}
                            """)
            ) @Valid  OfficeCreateRequest request,

            @Parameter(
                    name = "photo",
                    description = "Изображение (PNG/JPEG/SVG) — опционально",
                    required = false,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            ) MultipartFile photo
    );

    // ========================= PATCH /offices/{officeId} ====================
    @Operation(
            summary = "Обновить офис",
            description = """
                    Тип запроса: multipart/form-data.
                    Части формы:
                      • data  — JSON OfficeUpdateRequest
                      • photo — файл (PNG/JPEG/SVG), опционально — заменяет текущее фото
                    Также можно передавать флаг removePhoto=true внутри data,
                    тогда photo не передавать. Нельзя одновременно передать фото и removePhoto=true.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Офис обновлён",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OfficeDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации/конфликт параметров фото",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Офис не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    ResponseEntity<OfficeDto> updateOffice(
            @Parameter(description = "ID офиса", example = "5") Long officeId,

            @Parameter(
                    name = "data",
                    description = "JSON OfficeUpdateRequest",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OfficeUpdateRequest.class)),
                    examples = @ExampleObject(value = """
                            {"name":"HQ Moscow (updated)","address":"Москва, ул. Пушкина, 1","removePhoto":false}
                            """)
            ) @Valid  OfficeUpdateRequest dto,

            @Parameter(
                    name = "photo",
                    description = "Новое изображение (PNG/JPEG/SVG) — опционально",
                    required = false,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            ) MultipartFile photo
    );

    // ========================= DELETE /offices/{officeId} ====================
    @Operation(summary = "Удалить офис", description = "Удаляет офис и его фото (если было).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Офис удалён", content = @Content),
            @ApiResponse(responseCode = "404", description = "Офис не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteOffice(
            @Parameter(description = "ID офиса", example = "5") Long officeId
    );
}
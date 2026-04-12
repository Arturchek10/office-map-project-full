package com.t1.map_service.controller.layer;

import com.t1.map_service.dto.layer.LayerCreateRequest;
import com.t1.map_service.dto.layer.LayerDto;
import com.t1.map_service.dto.layer.LayerUpdateRequest;
import com.t1.map_service.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface LayerApi {

    // ========================= POST /layers/{floorId} ========================
    @Operation(
            summary = "Создать слой",
            description = "Создает слой, привязанный к указанному этажу. Имена слоёв в пределах одного этажа должны быть уникальны."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Слой создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LayerDto.class),
                            examples = @ExampleObject(name = "Created", value = """
                {
                  "id": 101,
                  "name": "Рабочие места",
                  "markers": []
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "ValidationError", value = """
                {
                  "status": 400,
                  "message": "Validation failed",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/10",
                  "subErrors": [
                    { "object": "LayerCreateRequest", "field": "name", "rejectedValue": "", "message": "must not be blank" }
                  ]
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Слой с таким именем уже существует на этаже",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflict", value = """
                {
                  "status": 409,
                  "message": "Layer already exists",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/10",
                  "subErrors": []
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "InternalServerError", value = """
                {
                  "status": 500,
                  "message": "Internal Server Error",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<LayerDto> create(
            @Parameter(description = "ID этажа, к которому привязан слой", example = "10")
            Long floorId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания нового слоя",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LayerCreateRequest.class),
                            examples = @ExampleObject(value = "{\"name\": \"Рабочие места\"}")
                    )
            )
              @RequestBody @Valid  LayerCreateRequest dto
    );

    // ========================= GET /layers/{layerId} =========================
    @Operation(summary = "Получить слой", description = "Возвращает слой с его маркерами.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Слой найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LayerDto.class),
                            examples = @ExampleObject(name = "OK", value = """
                {
                  "id": 101,
                  "name": "Рабочие места",
                  "markers": [
                    { "id": 9001, "position": { "position_x": 0.0, "position_y": 0.0 }, "type": "empty" },
                    { "id": 9002, "position": { "position_x": 5.0, "position_y": 2.5 }, "type": "desk" }
                  ]
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Слой не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Layer not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/999",
                  "subErrors": []
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "InternalServerError", value = """
                {
                  "status": 500,
                  "message": "Internal Server Error",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<LayerDto> getById(
            @Parameter(description = "ID слоя", example = "101") Long layerId
    );

    // ========================= PATCH /layers/{layerId} =======================
    @Operation(summary = "Обновить слой", description = "Обновляет имя слоя.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Слой обновлен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LayerDto.class),
                            examples = @ExampleObject(name = "OK", value = """
                {
                  "id": 101,
                  "name": "Обновленное имя",
                  "markers": []
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "ValidationError", value = """
                {
                  "status": 400,
                  "message": "Validation failed",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/10",
                  "subErrors": [
                    { "object": "LayerUpdateRequest", "field": "name", "rejectedValue": "", "message": "must not be blank" }
                  ]
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Слой не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Layer not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/999",
                  "subErrors": []
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "InternalServerError", value = """
                {
                  "status": 500,
                  "message": "Internal Server Error",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<LayerDto> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные слоя",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LayerUpdateRequest.class),
                            examples = @ExampleObject(value = "{\"name\": \"Обновленное имя\"}")
                    )
            )
              @RequestBody @Valid  LayerUpdateRequest dto,

            @Parameter(description = "ID слоя", example = "101")
            Long layerId
    );

    // ========================= DELETE /layers/{layerId} ======================
    @Operation(summary = "Удалить слой", description = "Удаляет слой и все его маркеры.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Слой удален", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Слой не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Layer not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/999",
                  "subErrors": []
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "InternalServerError", value = """
                {
                  "status": 500,
                  "message": "Internal Server Error",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID слоя", example = "101") Long layerId
    );
}
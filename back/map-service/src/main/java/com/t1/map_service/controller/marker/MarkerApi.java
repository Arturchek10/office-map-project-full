package com.t1.map_service.controller.marker;

import com.t1.map_service.dto.description.RoomDescriptionDto;
import com.t1.map_service.dto.description.WorkspaceDescriptionDto;
import com.t1.map_service.dto.marker.CreateMarkerRequest;
import com.t1.map_service.dto.marker.MarkerDto;
import com.t1.map_service.dto.marker.MarkerMoveRequest;
import com.t1.map_service.dto.marker.UpdateMarkerRequest;
import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

public interface MarkerApi {

    // ======================= POST /markers/{layerId} =========================
    @Operation(
            summary = "Создать маркер",
            description = """
            Создаёт новый маркер, привязанный к слою.
            Обязательные поля:
            • type — тип маркера (строка). Допустимые значения:
              - workspace — рабочее место
              - room — комната
              - utility — остальное (кофемашина, кондер и тд)
              - emergency — аварийный объект
              - empty — пустой маркер
            
            По умолчанию:
            
            position =
             {
                "position_x": 0.0,
                "position_y": 0.0
             }
            
            description = null
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Маркер создан",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarkerDto.class),
                            examples = @ExampleObject(name = "Created", value = """
                {
                  "id": 1001,
                  "name": null,
                  "type": "empty",
                  "position": { "position_x": 0.0, "position_y": 0.0 },
                  "description": null
                }
                """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации (например, отсутствует тип)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "ValidationError", value = """
                {
                  "status": 400,
                  "message": "Validation failed",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/markers/15",
                  "subErrors": [
                    { "object": "CreateMarkerRequest", "field": "type", "rejectedValue": "", "message": "Type must exists" }
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
                    )),
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
                  "path": "/api/v1/markers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<MarkerDto> createMarker(
            @Parameter(description = "ID слоя, к которому привязан маркер", example = "15", in = ParameterIn.PATH)
            Long layerId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового маркера",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateMarkerRequest.class),
                            examples = @ExampleObject(value = "{\"type\": \"room\"}")
                    )
            )
              @RequestBody @Valid  CreateMarkerRequest dto
    );

    // ======================= GET /markers/{markerId} =========================
    @Operation(summary = "Получить маркер", description = "Возвращает маркер по его ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркер найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarkerDto.class),
                            examples = @ExampleObject(name = "OK", value = """
                {
                  "id": 1001,
                  "name": "Рабочее место №1",
                  "type": "workspace",
                  "position": { "position_x": 12.5, "position_y": 4.0 },
                  "description": { "haveComputer": true }
                }
                """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Маркер не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Marker not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/markers/999",
                  "subErrors": []
                }
                """)
                    )),
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
                  "path": "/api/v1/markers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<MarkerDto> getMarker(
            @Parameter(description = "ID маркера", example = "1001", in = ParameterIn.PATH)
            Long markerId
    );

    // ======================= PATCH /markers/{markerId} =======================
    @Operation(
            summary = "Обновить маркер",
            description = """
            Устанавливает/меняет поля маркера.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркер обновлён",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarkerDto.class),
                            examples = @ExampleObject(name = "OK", value = """
                {
                  "id": 1001,
                  "name": "Комната переговоров",
                  "type": "room",
                  "position": { "position_x": 5.0, "position_y": 10.0 },
                  "description": { "capacity": 12 }
                }
                """))),
            @ApiResponse(responseCode = "400", description = "Неверный тип маркера или payload",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "UnsupportedType", value = """
                {
                  "status": 400,
                  "message": "Unsupported type *",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/markers/999",
                  "subErrors": []
                }
                """)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Маркер не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Marker not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/999",
                  "subErrors": []
                }
                """)
                    )),
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
                  "path": "/api/v1/markers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<MarkerDto> updateMarker(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                Данные для обновления маркера.
                """,
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarkerApi.UpdateMarkerRequestDoc.class),
                            examples = {
                                    @ExampleObject(name = "Workspace", value = """
                    {
                      "name": "Рабочее место №1",
                      "type": "workspace",
                      "payload": { "haveComputer": true }
                    }
                    """),
                                    @ExampleObject(name = "Room", value = """
                    {
                      "name": "Комната переговоров",
                      "type": "room",
                      "payload": { "capacity": 10 }
                    }
                    """),
                                    @ExampleObject(name = "Utility", value = """
                    {
                      "name": "Шкаф с оборудованием",
                      "type": "utility",
                      "payload": {}
                    }
                    """),
                                    @ExampleObject(name = "Empty", value = """
                    {
                      "name": null,
                      "type": "empty",
                      "payload": {}
                    }
                    """)
                            }
                    )
            )
              @RequestBody @Valid  UpdateMarkerRequest dto,
            @Parameter(description = "ID маркера", example = "1001", in = ParameterIn.PATH)
            Long markerId
    );

    // ======================= PATCH /markers/move/{markerId} ==================
    @Operation(summary = "Переместить маркер", description = "Изменяет координаты маркера.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркер перемещён",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarkerDto.class),
                            examples = @ExampleObject(name = "OK", value = """
                {
                  "id": 1001,
                  "name": "Рабочее место №1",
                  "type": "workspace",
                  "position": { "position_x": 25.0, "position_y": 8.5 },
                  "description": { "haveComputer": true }
                }
                """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Маркер не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Marker not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/999",
                  "subErrors": []
                }
                """)
                    )),
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
                  "path": "/api/v1/markers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<MarkerDto> move(
            @Parameter(description = "ID маркера", example = "1001", in = ParameterIn.PATH)
            Long markerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новая позиция маркера",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarkerMoveRequest.class),
                            examples = @ExampleObject(name = "Request", value = """
                { "position": { "position_x": 25.0, "position_y": 8.5 } }
                """))
            )
              @RequestBody @Valid  MarkerMoveRequest dto
    );

    // ======================= DELETE /markers/{markerId} ======================
    @Operation(summary = "Удалить маркер", description = "Удаляет маркер по его ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Маркер удалён", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Маркер не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NotFound", value = """
                {
                  "status": 404,
                  "message": "Marker not found",
                  "timestamp": "2025-08-10T12:34:56",
                  "path": "/api/v1/layers/999",
                  "subErrors": []
                }
                """)
                    )),
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
                  "path": "/api/v1/markers/5",
                  "subErrors": []
                }
                """)
                    )
            )
    })
    ResponseEntity<Void> deleteMarker(
            @Parameter(description = "ID маркера", example = "1001", in = ParameterIn.PATH)
            Long markerId
    );

    // --------------------- ДОК-СХЕМЫ ТОЛЬКО ДЛЯ SWAGGER ---------------------
    @Schema(name = "UpdateMarkerRequestDoc", description = """
        Документ-схема для Swagger (контроллер принимает UpdateMarkerRequest).
        Здесь показано:
        • type — enum MarkerType
        • payload — oneOf (WorkspaceDescriptionDto | RoomDescriptionDto | пустой объект)
        """)
    class UpdateMarkerRequestDoc {
        public String name;

        @Schema(description = "Тип маркера", implementation = MarkerType.class)
        public String type;

        @Schema(
                description = "Данные описания, зависят от type",
                oneOf = { WorkspaceDescriptionDto.class, RoomDescriptionDto.class }
        )
        public Object payload;
    }
}
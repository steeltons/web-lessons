package org.jenjetsu.com.finalproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
    info = @Info(
        title = "Pretty Kanban Board",
        description = "Control your projects on highest level",
        contact = @Contact(
            name = "Tsvetkov Stas",
            email = "tcvetkov.so@dvfu.ru"
        )
    )
)
@SecurityScheme(
    name = "JWT",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfiguration {
    
    @ApiResponse(
        description = "Successful request without body"
    )
    public class OkResponse {

    }

    @ApiResponse(
        description = "Successful creating request without body"
    )
    public class CreatedResponse {

    }

    @ApiResponse(
        description = "Successful done work without returning body"
    )
    public class NoContentResponse {

    }

    @ApiResponse(
        description = "Error while saving entity",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = "object",
                requiredProperties = {"error_message"}
            )
        )
    )
    public class EntityCreateError {
        @Schema(type = "string", example = "Impossible to save Entity <Entity name>. Error message...")
        String errorMessage;
    }

    @ApiResponse(
        description = "Try to get not exist entity by id",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = "object",
                requiredProperties = {"error_message"}
            )
        )
    )
    public class EntityNotFoundError {
        @Schema(type = "string", example = "Imposible to find entity <EntityName> with id <id>")
        String errorMessage;
    }

    @ApiResponse(
        description = "Error while delete entity",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = "object",
                requiredProperties = {"error_message"}
            )
        )
    )
    public class EntityDeleteError {
        @Schema(type = "string", example = "Impossible to delete Entity <EntityName> with id <Id>. Error message...")
        String errorMessage;
    }
    
    @ApiResponse(
        description = "Error while updating entity",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = "object",
                requiredProperties = {"error_message"}
            )
        )
    )
    public class EntityModificationError {
        @Schema(type = "string", example = "Impossible to update Entity <EntityName>. Error message...")
        String errorMessage;
    }
}

package org.gaba.JavaTechTask.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO постов")
public record PostDTO(
        @Schema(description = "Тема поста") String subject,
        @Schema(description = "Текст поста") String content,
        @Schema(description = "Прилагаемые файла") Object[] files) {

}

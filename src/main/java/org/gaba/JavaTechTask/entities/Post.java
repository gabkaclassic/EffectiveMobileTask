package org.gaba.JavaTechTask.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table("posts")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность постов")
public class Post {

    @Id
    private String id;

    @Schema(description = "Тема поста")
    private String subject;

    @Schema(description = "Текст поста")
    private String content;

    @Schema(description = "ID автора")
    private String author;

    @CreatedDate
    @Column("creation_date")
    @Schema(description = "Время создания")
    private LocalDateTime creationTime;

    @Schema(description = "Пути до файлов в S3 (чтобы получить, нужно подставить оставшуюся часть ссылки на фронте)")
    private List<String> files;
}

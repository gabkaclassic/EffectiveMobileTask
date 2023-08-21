package org.gaba.JavaTechTask.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("chats")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность чата")
public class Chat {

    @Id
    private String id;

    @Schema(description = "Создатель чата")
    private String creator;

    @Schema(description = "Второй участник (в дальнейшем таким же образом можно сделать несколько участников)")
    private String participant;
}

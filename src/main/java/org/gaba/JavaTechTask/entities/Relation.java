package org.gaba.JavaTechTask.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("relations")
@Data
@NoArgsConstructor
@Schema(description = "Сущность связи между двумя пользователями")
public class Relation {

    @Id
    private String id;

    @Column("sender")
    @Schema(description = "Первый пользователь")
    private String user;

    @Schema(description = "Второй пользователь")
    private String target;

    @Schema(description = "Тип связи")
    private RelationType relationType;

    public Relation(String user, String target, RelationType relationType) {
        this.user = user;
        this.target = target;
        this.relationType = relationType;
    }
}

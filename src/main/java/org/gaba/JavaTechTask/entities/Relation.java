package org.gaba.JavaTechTask.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("relations")
@Data
@NoArgsConstructor
public class Relation {

    @Id
    private String id;

    @Column("sender")
    private String user;

    private String target;

    private RelationType relationType;

    public Relation(String user, String target, RelationType relationType) {
        this.user = user;
        this.target = target;
        this.relationType = relationType;
    }
}

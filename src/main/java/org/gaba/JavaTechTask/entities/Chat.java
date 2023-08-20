package org.gaba.JavaTechTask.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("chats")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    private String id;

    private String creator;

    private String participant;
}

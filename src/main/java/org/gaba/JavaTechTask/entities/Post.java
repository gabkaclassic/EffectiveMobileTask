package org.gaba.JavaTechTask.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private UUID id;

    private String subject;

    private String content;

    private List<String> files;

}

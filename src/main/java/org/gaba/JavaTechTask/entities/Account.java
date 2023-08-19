package org.gaba.JavaTechTask.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    private UUID id;

    private String username;

    private String password;

    private String email;
}

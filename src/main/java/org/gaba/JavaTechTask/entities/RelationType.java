package org.gaba.JavaTechTask.entities;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип связи между двумя пользователями")
public enum RelationType {
    FRIEND, SUBSCRIBER, REQUEST
}

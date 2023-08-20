package org.gaba.JavaTechTask.entities;

import org.springframework.web.multipart.MultipartFile;

public record PostData(String subject, String content, Object[] files) {

}

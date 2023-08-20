package org.gaba.JavaTechTask.validators;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class FilesValidator {

    private static final Long MAX_FILES_SIZE_MB = 1048576L;
    private static final Pattern EXTENSION_PATTERN = Pattern.compile("^[a-zA-Z0-9_ -]+\\.[a-z]+$");

    private boolean checkContentType(String contentType, String type) {

        return StringUtils.hasText(contentType)
                && contentType.startsWith(type);
    }

    public List<String> checkFiles(List<FilePart> files, String type) {

        long summarySize = 0;

        for (var file : files) {

            if (file == null)
                return List.of("Invalid files");

            summarySize += file.headers().size();

            if (summarySize > MAX_FILES_SIZE_MB)
                return List.of("Too big");

            if (StringUtils.hasText(file.filename()) && !EXTENSION_PATTERN.matcher(file.filename()).matches())
                return List.of("Invalid file name");

            if (file.headers().getContentType() != null
                    && StringUtils.hasText(file.headers().getContentType().getType())
                    && !checkContentType(file.headers().getContentType().getType(), type))
                return List.of("Invalid content type");
        }
        return Collections.emptyList();
    }
}

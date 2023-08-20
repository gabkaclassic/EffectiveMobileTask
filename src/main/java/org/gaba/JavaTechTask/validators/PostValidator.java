package org.gaba.JavaTechTask.validators;

import org.gaba.JavaTechTask.entities.Post;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostValidator {

    private static final Long MAX_LENGTH = 1048576L;


    public List<String> validatePost(Post post) {

        var errors = new ArrayList<String>();

        if(!checkContentLength(post.getContent()))
            errors.add("Invalid content length");

        return errors;
    }

    private boolean checkContentLength(String content) {

        return StringUtils.hasText(content)
                && content.length() <= MAX_LENGTH;
    }
}

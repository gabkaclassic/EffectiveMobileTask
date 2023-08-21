package org.gaba.JavaTechTask;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.gaba.JavaTechTask.controllers.PostController;
import org.gaba.JavaTechTask.controllers.dto.PostDTO;
import org.gaba.JavaTechTask.entities.Post;
import org.gaba.JavaTechTask.services.JWTService;
import org.gaba.JavaTechTask.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@WebFluxTest(JavaTechTaskApplication.class)
public class PostControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private PostService postService;

    @Mock
    private JWTService jwtService;

    private PostController postController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        postController = new PostController(postService, jwtService);
        webTestClient = WebTestClient.bindToController(postController)
                .build();
    }

    @Test
    public void testCreatePost() {
        // Arrange
        String token = "dummyToken";
        PostDTO postDTO = new PostDTO("Test Subject", "Test Content", new FilePart[0]);
        when(jwtService.extractId(token)).thenReturn("dummyUserId");
        when(postService.create(anyString(), anyString(), anyString(), anyList())).thenReturn(Mono.just(Arrays.asList("Error 1", "Error 2")));

        // Act
        EntityExchangeResult<List<String>> result = webTestClient.post()
                .uri("/post")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(postDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .returnResult();

        // Assert
        List<String> errors = result.getResponseBody();
        assertThat(errors).containsExactly("Error 1", "Error 2");
        verify(postService).create(eq("Test Subject"), eq("Test Content"), eq("dummyUserId"), eq(Arrays.asList()));
    }

    @Test
    public void testGetActivityFeed() {
        // Arrange
        String token = "dummyToken";
        when(jwtService.extractId(token)).thenReturn("dummyUserId");
        when(postService.getFeed(anyString(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(Flux.just(new Post(), new Post()));

        // Act
        EntityExchangeResult<List<Post>> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/post/feed")
                        .queryParam("orderDesc", "true")
                        .queryParam("pageSize", "3")
                        .queryParam("pageNumber", "0")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class)
                .returnResult();

        // Assert
        List<Post> posts = result.getResponseBody();
        assertThat(posts).hasSize(2);
        verify(postService).getFeed(eq("dummyUserId"), eq(3), eq(0), eq(true));
    }

    @Test
    public void testDeletePost() {
        // Arrange
        String token = "dummyToken";
        String postId = "dummyPostId";
        when(jwtService.extractId(token)).thenReturn("dummyUserId");
        when(postService.delete(anyString(), anyString())).thenReturn(Mono.just(true));

        // Act
        EntityExchangeResult<Boolean> result = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/post")
                        .queryParam("postId", postId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult();

        // Assert
        boolean deleteResult = result.getResponseBody();
        assertThat(deleteResult).isTrue();
        verify(postService).delete(eq("dummyUserId"), eq(postId));
    }

}

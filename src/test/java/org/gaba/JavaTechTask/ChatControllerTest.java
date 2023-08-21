package org.gaba.JavaTechTask;

import org.apache.http.HttpHeaders;
import org.gaba.JavaTechTask.controllers.ChatController;
import org.gaba.JavaTechTask.entities.Chat;
import org.gaba.JavaTechTask.services.ChatService;
import org.gaba.JavaTechTask.services.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(JavaTechTaskApplication.class)
class ChatControllerTest {

	private WebTestClient webTestClient;
	private ChatService chatService;
	private JWTService jwtService;

	@BeforeEach
	public void setUp() {
		chatService = Mockito.mock(ChatService.class);
		jwtService = Mockito.mock(JWTService.class);
		webTestClient = WebTestClient.bindToController(new ChatController(chatService, jwtService)).build();
	}

	@Test
	public void createChat_ValidRequest_ReturnsChatId() {
		// Arrange
		Chat chat = new Chat("creator", "participant");
		String token = "auth_token";

		when(jwtService.extractId(token)).thenReturn("user_id");
		when(chatService.create(anyString(), any(Chat.class))).thenReturn(Mono.just("chat_id"));

		// Act & Assert
		webTestClient.post().uri("/chat")
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(chat)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("chat_id");
	}

	@Test
	public void getChat_ValidRequest_ReturnsChat() {
		// Arrange
		String token = "auth_token";
		String chatId = "chat_id";
		Chat chat = new Chat("creator", "participant");

		when(jwtService.extractId(token)).thenReturn("user_id");
		when(chatService.getChat(anyString(), anyString())).thenReturn(Mono.just(chat));

		// Act & Assert
		webTestClient.get().uri("/chat?chatId=chat_id")
				.header(HttpHeaders.AUTHORIZATION, token)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Chat.class)
				.isEqualTo(chat);
	}
}

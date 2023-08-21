package org.gaba.JavaTechTask;

import org.apache.http.HttpHeaders;
import org.gaba.JavaTechTask.controllers.AccountController;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.services.AccountService;
import org.gaba.JavaTechTask.services.JWTService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@WebFluxTest(JavaTechTaskApplication.class)
class AccountControllerTest {

	private final AccountService accountService = Mockito.mock(AccountService.class);
	private final JWTService jwtService = Mockito.mock(JWTService.class);
	private final AccountController accountController = new AccountController(accountService, jwtService);
	private final WebTestClient webTestClient = WebTestClient.bindToController(accountController).build();

	@Test
	public void getSubscribers_ReturnsSubscribers_WhenValidParametersProvided() {
		String username = "testUser";
		String relationType = "SUBSCRIBER";
		List<String> subscribers = Collections.singletonList("subscriber1");

		Mockito.when(accountService.getUsersByRelation(username, relationType)).thenReturn(Flux.fromIterable(subscribers));

		webTestClient.get()
				.uri("/account/relations?username={username}&relationType={relationType}", username, relationType)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void auth_ReturnsToken_WhenValidAccountProvided() {
		Account account = new Account("testUser", "password");
		String token = "jwtToken";

		Mockito.when(accountService.login(account)).thenReturn(Mono.just(token));

		webTestClient.post()
				.uri("/account/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(account), Account.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody().isEmpty();
	}

	@Test
	public void auth_ReturnsForbidden_WhenInvalidAccountProvided() {
		Account account = new Account("testUser", "wrongPassword");

		Mockito.when(accountService.login(account)).thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

		webTestClient.post()
				.uri("/account/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(account), Account.class)
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	public void registration_ReturnsOk_WhenValidAccountProvided() {
		Account account = new Account("testUser", "MsdfkKd!34f");

		Mockito.when(accountService.registration(account)).thenReturn(Mono.just(Collections.emptyList()));

		webTestClient.post()
				.uri("/account/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(account), Account.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody().json("[]");
	}

	@Test
	public void registration_ReturnsForbidden_WhenInvalidAccountProvided() {
		Account account = new Account("testUser", "password");

		List<String> errors = Collections.singletonList("Email is required");

		Mockito.when(accountService.registration(account)).thenReturn(Mono.just(errors));

		webTestClient.post()
				.uri("/account/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(account), Account.class)
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	public void sendRequest_SendsRequest_WhenValidParametersProvided() {
		String token = "jwtToken";
		String targetUsername = "friendUser";

		Mockito.when(jwtService.extractId(token)).thenReturn("senderUserId");

		webTestClient.post()
				.uri("/account/send_request?targetUsername={targetUsername}", targetUsername)
				.header(HttpHeaders.AUTHORIZATION, token)
				.exchange()
				.expectStatus().isOk()
				.expectBody().isEmpty();

		Mockito.verify(accountService, Mockito.times(1)).sendRequest("senderUserId", targetUsername);
	}

	@Test
	public void rejectRequest_RejectsRequest_WhenValidParametersProvided() {
		String token = "jwtToken";
		String username = "requestUser";

		Mockito.when(jwtService.extractId(token)).thenReturn("targetUserId");

		webTestClient.post()
				.uri("/account/reject_request?username={username}", username)
				.header(HttpHeaders.AUTHORIZATION, token)
				.exchange()
				.expectStatus().isOk()
				.expectBody().isEmpty();

		Mockito.verify(accountService, Mockito.times(1)).rejectRequest(username, "targetUserId");
	}

	@Test
	public void confirmRequest_ConfirmsRequest_WhenValidParametersProvided() {
		String token = "jwtToken";
		String username = "requestUser";

		Mockito.when(jwtService.extractId(token)).thenReturn("targetUserId");

		webTestClient.post()
				.uri("/account/confirm_request?username={username}", username)
				.header(HttpHeaders.AUTHORIZATION, token)
				.exchange()
				.expectStatus().isOk()
				.expectBody().isEmpty();

		Mockito.verify(accountService, Mockito.times(1)).confirmRequest(username, "targetUserId");
	}

	@Test
	public void rejectFriend_RejectsFriend_WhenValidParametersProvided() {
		String token = "jwtToken";
		String targetUsername = "friendUser";

		Mockito.when(jwtService.extractId(token)).thenReturn("senderUserId");

		webTestClient.post()
				.uri("/account/reject_friend?targetUsername={targetUsername}", targetUsername)
				.header(HttpHeaders.AUTHORIZATION, token)
				.exchange()
				.expectStatus().isOk()
				.expectBody().isEmpty();

		Mockito.verify(accountService, Mockito.times(1)).rejectFriend("senderUserId", targetUsername);
	}

}

package com.tnt.gateway.application;

import static com.tnt.common.error.model.ErrorMessage.APPLE_AUTH_ERROR;
import static com.tnt.common.error.model.ErrorMessage.KAKAO_SERVER_ERROR;
import static com.tnt.member.domain.SocialType.APPLE;
import static com.tnt.member.domain.SocialType.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.verify;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tnt.common.error.exception.OAuthException;
import com.tnt.gateway.dto.request.OAuthLoginRequest;
import com.tnt.gateway.dto.response.OAuthLoginResponse;
import com.tnt.member.application.MemberService;
import com.tnt.member.domain.Member;
import com.tnt.member.dto.response.LogoutResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

	private MockWebServer mockWebServer;

	@Mock
	private SessionService sessionService;

	@Mock
	private MemberService memberService;

	@InjectMocks
	private OAuthService oAuthService;

	@Value("${social-login.provider.apple.audience}")
	private String appleApiUrl;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		String baseUrl = mockWebServer.url("/").toString();

		WebClient webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.build();

		oAuthService = new OAuthService(webClient, sessionService, memberService);

		ReflectionTestUtils.setField(oAuthService, "kakaoApiUrl", baseUrl);
		ReflectionTestUtils.setField(oAuthService, "appleApiUrl", appleApiUrl);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	@DisplayName("Kakao 로그인 실패")
	void kakao_login_failure_error() {
		// given
		OAuthLoginRequest request = new OAuthLoginRequest(KAKAO, "fcm", "invalid-token", null);

		String errorResponse = "{\"msg\": \"this access token does not exist\", \"code\": -401}";

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(401)
			.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.setBody(errorResponse));

		// when & then
		assertThatThrownBy(() -> oAuthService.oauthLogin(request))
			.isInstanceOf(OAuthException.class)
			.hasMessage(KAKAO_SERVER_ERROR.getMessage());
	}

	@Test
	@DisplayName("Apple 로그인 실패")
	void apple_token_verification_error() {
		// given
		OAuthLoginRequest request = new OAuthLoginRequest(APPLE, "fcm", null, "asdf");

		mockWebServer.enqueue(new MockResponse().setResponseCode(400));

		// when & then
		assertThatThrownBy(() -> oAuthService.oauthLogin(request))
			.isInstanceOf(OAuthException.class)
			.hasMessage(APPLE_AUTH_ERROR.getMessage());
	}

	@Test
	@DisplayName("Kakao 로그인 성공")
	void kakao_login_success() {
		// given
		OAuthLoginRequest request = new OAuthLoginRequest(KAKAO, "fcm", "valid-token", null);
		Member member = mock(Member.class);
		given(member.getId()).willReturn(1L);

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(200)
			.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.setBody("{\"id\": \"12345\"}"));

		given(memberService.getBySocialIdAndSocialType("12345", KAKAO)).willReturn(member);

		// when
		OAuthLoginResponse response = oAuthService.oauthLogin(request);

		// then
		assertThat(response).isNotNull();
		verify(sessionService).createSession(anyString(), eq("1"));
	}

	@Test
	@DisplayName("Apple 로그인 성공")
	void apple_login_success() throws Exception {
		// given
		KeyPairGenerator ecKeyGen = KeyPairGenerator.getInstance("EC");
		ecKeyGen.initialize(256);

		KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance("RSA");
		rsaKeyGen.initialize(2048);
		KeyPair rsaPair = rsaKeyGen.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey)rsaPair.getPublic();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)rsaPair.getPrivate();

		String mockIdToken = JWT.create()
			.withKeyId("test-kid")
			.withIssuer(appleApiUrl)
			.withSubject("test-user-id")
			.withAudience("test-client-id")
			.withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
			.withIssuedAt(new Date())
			.sign(Algorithm.RSA256(rsaPublicKey, rsaPrivateKey));

		OAuthLoginRequest request = new OAuthLoginRequest(APPLE, "fcm", null, mockIdToken);
		Member mockMember = mock(Member.class);
		given(mockMember.getId()).willReturn(1L);

		String mockN = Base64.getUrlEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray());
		String mockE = Base64.getUrlEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray());

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(200)
			.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.setBody(
				"{\"keys\": [{\"kid\": \"test-kid\", \"kty\": \"RSA\", \"n\": \"" + mockN + "\", \"e\": \"" + mockE
					+ "\"}]}"));

		given(memberService.getBySocialIdAndSocialType("test-user-id", APPLE)).willReturn(mockMember);

		// when
		OAuthLoginResponse response = oAuthService.oauthLogin(request);

		// then
		assertThat(response).isNotNull();
		verify(sessionService).createSession(anyString(), eq("1"));
	}

	@Test
	@DisplayName("매칭되는 키가 없을 경우 예외 발생")
	void no_matching_key_error() {
		// given
		String mockIdToken = JWT.create()
			.withKeyId("test-kid")
			.withIssuer(appleApiUrl)
			.withSubject("test-user-id")
			.sign(Algorithm.HMAC256("secret"));

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(200)
			.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.setBody("{\"keys\": [{\"kid\": \"different-kid\"}]}"));

		OAuthLoginRequest request = new OAuthLoginRequest(APPLE, "fcm", null, mockIdToken);

		// when & then
		assertThatThrownBy(() -> oAuthService.oauthLogin(request))
			.isInstanceOf(OAuthException.class)
			.hasMessage(APPLE_AUTH_ERROR.getMessage());
	}

	@Test
	@DisplayName("다양한 HTTP 상태코드에 따른 에러 처리")
	void handle_response_with_different_status_error() {
		// given
		OAuthLoginRequest request = new OAuthLoginRequest(KAKAO, "fcm", "invalid-token", null);

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(401)
			.setBody("{\"error\":\"invalid_token\"}"));

		// when & then
		assertThatThrownBy(() -> oAuthService.oauthLogin(request))
			.isInstanceOf(OAuthException.class)
			.hasMessage(KAKAO_SERVER_ERROR.getMessage());
	}

	@Test
	@DisplayName("Apple 공개키 요청 실패시 예외 발생")
	void fetch_apple_public_keys_error() {
		// given
		String validToken = JWT.create()
			.withKeyId("test-kid")
			.withIssuer(appleApiUrl)
			.withSubject("test-user")
			.sign(Algorithm.HMAC256("secret"));

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(500)
			.setBody("Server Error"));

		OAuthLoginRequest request = new OAuthLoginRequest(APPLE, "fcm", null, validToken);

		// when & then
		assertThatThrownBy(() -> oAuthService.oauthLogin(request))
			.isInstanceOf(OAuthException.class)
			.hasMessage(APPLE_AUTH_ERROR.getMessage());
	}

	@Test
	@DisplayName("잘못된 형식의 JWKS 응답일 경우 예외 발생")
	void invalid_jwks_format_error() {
		// given
		String validToken = JWT.create()
			.withKeyId("test-kid")
			.withIssuer(appleApiUrl)
			.withSubject("test-user")
			.sign(Algorithm.HMAC256("secret"));

		mockWebServer.enqueue(new MockResponse()
			.setResponseCode(200)
			.setBody("invalid json"));

		OAuthLoginRequest request = new OAuthLoginRequest(APPLE, "fcm", null, validToken);

		// when & then
		assertThatThrownBy(() -> oAuthService.oauthLogin(request))
			.isInstanceOf(OAuthException.class)
			.hasMessage(APPLE_AUTH_ERROR.getMessage());
	}

	@Test
	@DisplayName("로그아웃 성공")
	void logout_success() {
		// given
		Long memberId = 1L;
		String sessionId = "testSessionId";

		given(sessionService.removeSession(String.valueOf(memberId))).willReturn(sessionId);

		//when
		LogoutResponse response = oAuthService.logout(memberId);

		//then
		assertThat(response.sessionId()).isEqualTo(sessionId);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}
}


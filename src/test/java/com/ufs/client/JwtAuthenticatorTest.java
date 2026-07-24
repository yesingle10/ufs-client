package com.ufs.client;

import com.ufs.client.auth.JwtAuthenticator;
import com.ufs.client.config.UfsClientConfig;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.ufs.client.TestHelper.mockAuthSuccess;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtAuthenticator 单元测试 — 验证 Token 签发、缓存和刷新逻辑。
 */
class JwtAuthenticatorTest {

    MockWebServer server;
    JwtAuthenticator auth;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build();
        UfsClientConfig config = new UfsClientConfig()
                .setBaseUrl(server.url("").toString())
                .setAppId("test-app")
                .setAppSecret("test-secret")
                .setTenantId("t_test");
        auth = new JwtAuthenticator(config, httpClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void shouldIssueTokenOnFirstCall() throws Exception {
        server.enqueue(mockAuthSuccess());

        String token = auth.getToken();

        assertEquals("mock-jwt-token-abc123", token);
        // 验证调用了 /api/v1/auth/sign
        var req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertTrue(req.getPath().contains("/auth/sign"));
    }

    @Test
    void shouldCacheTokenAndNotCallAgain() throws Exception {
        server.enqueue(mockAuthSuccess());

        String token1 = auth.getToken();
        String token2 = auth.getToken();

        assertEquals("mock-jwt-token-abc123", token1);
        assertEquals(token1, token2);
        // 只有一次请求
        assertEquals(1, server.getRequestCount());
    }

    @Test
    void shouldThrowOnAuthFailure() {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"code\": 9999, \"message\": \"Invalid credentials\"}"));

        var ex = assertThrows(Exception.class, () -> auth.getToken());
        assertTrue(ex.getMessage().contains("Auth sign failed"));
    }

    @Test
    void shouldThrowOnNonZeroCode() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"code\": 1401, \"message\": \"App not found\"}"));

        var ex = assertThrows(Exception.class, () -> auth.getToken());
        assertTrue(ex.getMessage().contains("App not found"));
    }
}

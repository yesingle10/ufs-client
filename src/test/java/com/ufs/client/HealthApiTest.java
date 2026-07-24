package com.ufs.client;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Map;

import static com.ufs.client.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HealthApi 集成测试 — 每个测试独立 MockWebServer。
 */
class HealthApiTest {

    MockWebServer server;
    UfsClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = createClient(server);
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
        server.shutdown();
    }

    @Test
    void shouldCheckHealth() throws Exception {
        enqueueAuthAndResponse(server, ok("{ \"status\": \"UP\" }"));

        Map<String, Object> health = client.health().check();

        assertNotNull(health);
        assertEquals("UP", health.get("status"));
    }
}

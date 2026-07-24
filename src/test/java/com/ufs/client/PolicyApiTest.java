package com.ufs.client;

import com.ufs.client.model.StoragePolicy;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static com.ufs.client.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PolicyApi 集成测试 — 每个测试独立 MockWebServer。
 */
class PolicyApiTest {

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
    void shouldListActivePolicies() throws Exception {
        String list = "[" + policyJson("avatar", "cos") + "," + policyJson("document", "oss") + "]";
        enqueueAuthAndResponse(server, ok(list));

        List<StoragePolicy> policies = client.policy().listActive();

        assertNotNull(policies);
        assertEquals(2, policies.size());
        assertEquals("avatar", policies.get(0).getBizType());
    }

    @Test
    void shouldGetPolicy() throws Exception {
        enqueueAuthAndResponse(server, ok(policyJson("avatar", "cos")));

        StoragePolicy policy = client.policy().get("avatar");

        assertNotNull(policy);
        assertEquals("avatar", policy.getBizType());
        assertTrue(policy.isActive());
    }

    @Test
    void shouldCreatePolicy() throws Exception {
        enqueueAuthAndResponse(server, ok(policyJson("video", "kodo")));

        StoragePolicy policy = client.policy().create("video", "kodo", "video-bucket", "videos/", "private", 90);

        assertNotNull(policy);
        assertEquals("video", policy.getBizType());
        assertEquals("kodo", policy.getVendor());
    }

    @Test
    void shouldUpdatePolicy() throws Exception {
        enqueueAuthAndResponse(server, ok(policyJson("avatar", "cos")));

        StoragePolicy policy = client.policy().update("avatar", "new-bucket", null, "public-read", 60);

        assertNotNull(policy);
        assertEquals("avatar", policy.getBizType());
    }

    @Test
    void shouldDeletePolicy() throws Exception {
        enqueueAuthAndResponse(server, okNoData());

        assertDoesNotThrow(() -> client.policy().delete("avatar"));
    }
}

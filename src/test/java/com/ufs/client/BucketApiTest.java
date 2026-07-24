package com.ufs.client;

import com.ufs.client.model.Bucket;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static com.ufs.client.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BucketApi 集成测试 — 使用 MockWebServer 模拟 UFS 后端。
 * 每个测试独立创建/销毁 MockWebServer，避免静态上下文卡死和 token 缓存污染。
 */
class BucketApiTest {

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
    void shouldListBuckets() throws Exception {
        enqueueAuthAndResponse(server, ok(bucketListJson()));

        List<Bucket> buckets = client.bucket().list();

        assertNotNull(buckets);
        assertEquals(2, buckets.size());
        assertEquals("bucket-a", buckets.get(0).getName());
        assertEquals("local", buckets.get(0).getVendor());
        assertEquals("bucket-b", buckets.get(1).getName());
    }

    @Test
    void shouldGetBucket() throws Exception {
        enqueueAuthAndResponse(server, ok(bucketJson("my-bucket", "cos", "ap-guangzhou", "private")));

        Bucket bucket = client.bucket().get("my-bucket");

        assertNotNull(bucket);
        assertEquals("my-bucket", bucket.getName());
        assertEquals("cos", bucket.getVendor());
    }

    @Test
    void shouldCreateBucket() throws Exception {
        enqueueAuthAndResponse(server, ok(bucketJson("new-bucket", "local", "us-west-1", "public-read")));

        Bucket bucket = client.bucket().create("new-bucket", "local", "us-west-1", "public-read");

        assertNotNull(bucket);
        assertEquals("new-bucket", bucket.getName());
        assertEquals("local", bucket.getVendor());
    }

    @Test
    void shouldUpdateBucket() throws Exception {
        enqueueAuthAndResponse(server, ok(bucketJson("my-bucket", "local", "us-east-1", "private")));

        Bucket bucket = client.bucket().update("my-bucket", "private", null);

        assertNotNull(bucket);
        assertEquals("private", bucket.getAcl());
    }

    @Test
    void shouldDeleteBucket() throws Exception {
        enqueueAuthAndResponse(server, okNoData());

        assertDoesNotThrow(() -> client.bucket().delete("my-bucket"));
    }

    @Test
    void shouldThrowOnBucketNotFound() throws Exception {
        server.enqueue(mockAuthSuccess());
        server.enqueue(error(1404, "Bucket not found"));

        var ex = assertThrows(Exception.class, () -> client.bucket().get("nonexistent"));
        assertTrue(ex.getMessage().contains("Bucket not found"));
    }
}

package com.ufs.client;

import com.ufs.client.model.ObjectMeta;
import com.ufs.client.model.PresignResult;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.ufs.client.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ObjectApi 集成测试 — 每个测试独立 MockWebServer。
 */
class ObjectApiTest {

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
    void shouldPresignUpload() throws Exception {
        enqueueAuthAndResponse(server, ok(presignJson("https://s3.example.com/upload?sign=xxx", "PUT", null)));

        PresignResult result = client.object().presignUpload("my-bucket", "test/file.txt", "file.txt", "text/plain", 1024);

        assertNotNull(result);
        assertEquals("PUT", result.getMethod());
        assertTrue(result.getUrl().contains("s3.example.com"));
    }

    @Test
    void shouldPresignMultipartUpload() throws Exception {
        String multipartJson = "{ \"url\":\"https://s3.example.com/part\", \"method\":\"PUT\","
                + " \"uploadId\":\"upload-123\", \"objectKey\":\"big.dat\", \"expiresAt\":1710000000,"
                + " \"parts\":[{\"partNumber\":1,\"url\":\"https://s3.example.com/part/1\"}] }";
        enqueueAuthAndResponse(server, ok(multipartJson));

        PresignResult result = client.object().presignMultipartUpload("my-bucket", "big.dat", "big.dat", "application/octet-stream", 10485760, 2);

        assertNotNull(result);
        assertEquals("upload-123", result.getUploadId());
    }

    @Test
    void shouldCompleteUpload() throws Exception {
        enqueueAuthAndResponse(server, ok(objectMetaJson()));

        ObjectMeta meta = client.object().completeUpload("my-bucket", "test/file.txt", "upload-123",
                List.of(Map.of("partNumber", 1, "etag", "abc")));

        assertNotNull(meta);
        assertEquals("f_001", meta.getFileId());
        assertEquals("file.txt", meta.getFileName());
    }

    @Test
    void shouldListObjects() throws Exception {
        String pageJson = "{ \"list\":[" + objectMetaJson() + "], \"nextCursor\":\"cursor-next\", \"hasMore\":true }";
        enqueueAuthAndResponse(server, ok(pageJson));

        var page = client.object().list("my-bucket", "test/", null, 20);

        assertNotNull(page);
        assertEquals(1, page.getList().size());
        assertTrue(page.isHasMore());
        assertEquals("cursor-next", page.getNextCursor());
    }

    @Test
    void shouldHeadObject() throws Exception {
        enqueueAuthAndResponse(server, ok(objectMetaJson()));

        ObjectMeta meta = client.object().head("my-bucket", "test/file.txt");

        assertNotNull(meta);
        assertEquals("f_001", meta.getFileId());
        assertEquals(1024, meta.getFileSize());
    }

    @Test
    void shouldPresignDownload() throws Exception {
        enqueueAuthAndResponse(server, ok(presignJson("https://s3.example.com/download?sign=yyy", "GET", null)));

        PresignResult result = client.object().presignDownload("my-bucket", "test/file.txt", "file.txt");

        assertNotNull(result);
        assertEquals("GET", result.getMethod());
    }

    @Test
    void shouldDeleteObject() throws Exception {
        enqueueAuthAndResponse(server, okNoData());

        assertDoesNotThrow(() -> client.object().delete("my-bucket", "test/file.txt"));
    }
}

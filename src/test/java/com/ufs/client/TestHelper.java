package com.ufs.client;

import com.ufs.client.config.UfsClientConfig;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 测试工具类：共享 MockWebServer 和 JSON 构造。
 */
public final class TestHelper {

    private TestHelper() {}

    /** 创建指向 MockWebServer 的 UfsClient */
    public static UfsClient createClient(MockWebServer server) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build();
        return new UfsClient(
                new UfsClientConfig()
                        .setBaseUrl(server.url("").toString())
                        .setAppId("test-app")
                        .setAppSecret("test-secret")
                        .setTenantId("t_test"),
                httpClient);
    }

    /** 模拟 JWT 签发成功 */
    public static MockResponse mockAuthSuccess() {
        return json(200, "{ \"code\": 0, \"message\": \"success\", \"data\": { \"token\": \"mock-jwt-token-abc123\" } }");
    }

    /** 成功响应包装 */
    public static MockResponse ok(String dataJson) {
        return json(200, "{ \"code\": 0, \"message\": \"success\", \"data\": " + dataJson + " }");
    }

    /** 错误响应包装 */
    public static MockResponse error(int code, String message) {
        return json(400, "{ \"code\": " + code + ", \"message\": \"" + message + "\", \"data\": null }");
    }

    /** 无 data 字段的成功响应 */
    public static MockResponse okNoData() {
        return json(200, "{ \"code\": 0, \"message\": \"success\" }");
    }

    /** 桶 JSON */
    public static String bucketJson(String name, String vendor, String region, String acl) {
        return "{ \"name\":\"" + name + "\", \"vendor\":\"" + vendor + "\", \"region\":\"" + region
                + "\", \"acl\":\"" + acl + "\", \"createdAt\":1710000000000 }";
    }

    /** 桶列表 JSON */
    public static String bucketListJson() {
        return "[" + bucketJson("bucket-a", "local", "us-east-1", "private") + ","
                + bucketJson("bucket-b", "cos", "ap-guangzhou", "public-read") + "]";
    }

    /** 预签名结果 JSON */
    public static String presignJson(String url, String method, String uploadId) {
        StringBuilder sb = new StringBuilder("{ \"url\":\"").append(url).append("\", \"method\":\"").append(method).append("\"");
        if (uploadId != null) sb.append(", \"uploadId\":\"").append(uploadId).append("\"");
        sb.append(", \"expiresAt\":1710000000, \"objectKey\":\"test/file.txt\" }");
        return sb.toString();
    }

    /** 对象元数据 JSON */
    public static String objectMetaJson() {
        return "{ \"fileId\":\"f_001\", \"objectKey\":\"test/file.txt\", \"fileName\":\"file.txt\","
                + " \"fileSize\":1024, \"contentType\":\"text/plain\", \"status\":1, \"createdAt\":1710000000000 }";
    }

    /** 策略 JSON */
    public static String policyJson(String bizType, String vendor) {
        return "{ \"policyId\":\"p_001\", \"bizType\":\"" + bizType + "\", \"vendor\":\"" + vendor
                + "\", \"bucket\":\"my-bucket\", \"prefix\":\"uploads/\", \"acl\":\"private\", \"ttlDays\":30, \"active\":true }";
    }

    private static MockResponse json(int code, String body) {
        return new MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body);
    }

    /** 给 MockWebServer 入队 Auth + 业务响应 */
    public static void enqueueAuthAndResponse(MockWebServer server, MockResponse businessResponse) throws IOException {
        // JwtAuthenticator calls /api/v1/auth/sign first
        server.enqueue(mockAuthSuccess());
        server.enqueue(businessResponse);
    }
}

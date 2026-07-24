package com.ufs.client;

import com.ufs.client.api.*;
import com.ufs.client.auth.JwtAuthenticator;
import com.ufs.client.config.UfsClientConfig;
import com.ufs.client.http.UfsHttpClient;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * UFS Java SDK 主入口。
 *
 * <h3>用法</h3>
 * <pre>{@code
 * UfsClient client = new UfsClient(new UfsClientConfig()
 *     .setBaseUrl("http://localhost:8080")
 *     .setAppId("app_demo")
 *     .setAppSecret("secret_demo")
 *     .setTenantId("t_demo"));
 *
 * List<Bucket> buckets = client.bucket().list();
 * PresignResult presign = client.object().presignUpload("my-bucket", "photo.jpg", "photo.jpg", "image/jpeg", 1024);
 * }</pre>
 */
public class UfsClient implements AutoCloseable {

    private final OkHttpClient httpClient;
    private final JwtAuthenticator auth;
    private final UfsHttpClient http;

    private final BucketApi bucket;
    private final ObjectApi object;
    private final PolicyApi policy;
    private final HealthApi health;

    public UfsClient(UfsClientConfig config) {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
                .build();
        this.auth = new JwtAuthenticator(config, httpClient);
        this.http = new UfsHttpClient(config.getBaseUrl(), httpClient, auth);

        this.bucket = new BucketApi(http);
        this.object = new ObjectApi(http);
        this.policy = new PolicyApi(http);
        this.health = new HealthApi(http);
    }

    public BucketApi bucket() { return bucket; }
    public ObjectApi object() { return object; }
    public PolicyApi policy() { return policy; }
    public HealthApi health() { return health; }

    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}

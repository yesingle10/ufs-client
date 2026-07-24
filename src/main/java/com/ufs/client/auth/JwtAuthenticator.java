package com.ufs.client.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufs.client.config.UfsClientConfig;
import com.ufs.client.exception.UfsClientException;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JWT 鉴权器 — 首次调用时签发 JWT，过期自动刷新。
 */
public class JwtAuthenticator {

    private final UfsClientConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;

    private volatile String cachedToken;
    private volatile long tokenExpiry;
    private final ReentrantLock lock = new ReentrantLock();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public JwtAuthenticator(UfsClientConfig config, OkHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
        this.mapper = new ObjectMapper();
    }

    /**
     * 获取 JWT token，自动懒加载和刷新。
     */
    public String getToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiry - 60_000) {
            return cachedToken;
        }
        lock.lock();
        try {
            // double-check
            if (cachedToken != null && System.currentTimeMillis() < tokenExpiry - 60_000) {
                return cachedToken;
            }
            cachedToken = issueToken();
            tokenExpiry = System.currentTimeMillis() + 3600_000; // 保守估计 1h
            return cachedToken;
        } finally {
            lock.unlock();
        }
    }

    private String issueToken() {
        String signUrl = config.getBaseUrl() + "/api/v1/auth/sign";
        String body;
        try {
            body = mapper.writeValueAsString(new SignRequest(
                    config.getAppId(), config.getAppSecret(), config.getTenantId()));
        } catch (Exception e) {
            throw new UfsClientException("Failed to serialize sign request", e);
        }

        Request request = new Request.Builder()
                .url(signUrl)
                .post(RequestBody.create(body, JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new UfsClientException(response.code(), "Auth sign failed: " + response.code());
            }
            JsonNode root = mapper.readTree(response.body().string());
            int code = root.path("code").asInt();
            if (code != 0) {
                throw new UfsClientException("Auth sign failed: " + root.path("message").asText());
            }
            return root.path("data").path("token").asText();
        } catch (IOException e) {
            throw new UfsClientException("Auth sign request failed", e);
        }
    }

    private record SignRequest(String appId, String appSecret, String tenantId) {}
}

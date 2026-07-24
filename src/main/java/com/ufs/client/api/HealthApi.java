package com.ufs.client.api;

import com.ufs.client.http.UfsHttpClient;

import java.util.Map;

/**
 * 健康检查 API。
 */
public class HealthApi {

    private final UfsHttpClient http;

    public HealthApi(UfsHttpClient http) {
        this.http = http;
    }

    /** 服务健康检查（无需鉴权） */
    @SuppressWarnings("unchecked")
    public Map<String, Object> check() {
        return (Map<String, Object>) http.get("/api/v1/health", Map.class);
    }
}

package com.ufs.client.config;

/**
 * UFS 客户端配置。
 *
 * <h3>用法</h3>
 * <pre>{@code
 * UfsClientConfig config = new UfsClientConfig()
 *     .setBaseUrl("http://localhost:8080")
 *     .setAppId("app_demo")
 *     .setAppSecret("secret_demo")
 *     .setTenantId("t_demo");
 * UfsClient client = new UfsClient(config);
 * }</pre>
 */
public class UfsClientConfig {

    private String baseUrl = "http://localhost:8080";
    private String appId;
    private String appSecret;
    private String tenantId;
    private int connectTimeoutMs = 10_000;
    private int readTimeoutMs = 30_000;

    public String getBaseUrl() { return baseUrl; }
    public UfsClientConfig setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }

    public String getAppId() { return appId; }
    public UfsClientConfig setAppId(String appId) { this.appId = appId; return this; }

    public String getAppSecret() { return appSecret; }
    public UfsClientConfig setAppSecret(String appSecret) { this.appSecret = appSecret; return this; }

    public String getTenantId() { return tenantId; }
    public UfsClientConfig setTenantId(String tenantId) { this.tenantId = tenantId; return this; }

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public UfsClientConfig setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; return this; }

    public int getReadTimeoutMs() { return readTimeoutMs; }
    public UfsClientConfig setReadTimeoutMs(int readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; return this; }
}

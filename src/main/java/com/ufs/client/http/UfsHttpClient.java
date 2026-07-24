package com.ufs.client.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufs.client.auth.JwtAuthenticator;
import com.ufs.client.exception.UfsClientException;
import com.ufs.client.model.ApiResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * UFS HTTP 客户端 — 封装 OkHttp + JWT 鉴权 + JSON 序列化。
 */
public class UfsHttpClient {

    private final OkHttpClient httpClient;
    private final JwtAuthenticator auth;
    private final String baseUrl;
    private final ObjectMapper mapper;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public UfsHttpClient(String baseUrl, OkHttpClient httpClient, JwtAuthenticator auth) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = httpClient;
        this.auth = auth;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T get(String path, Class<T> dataType) {
        return get(path, dataType, Map.of());
    }

    public <T> T get(String path, Class<T> dataType, Map<String, String> queryParams) {
        String url = buildUrl(path, queryParams);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + auth.getToken())
                .get()
                .build();
        return execute(request, dataType);
    }

    /** GET 请求并反序列化为 List&lt;T&gt;，保留泛型信息。 */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, Class<T> elementType) {
        return (List<T>) getList(path, elementType, Map.of());
    }

    /** GET 请求 + 查询参数 → List&lt;T&gt; */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, Class<T> elementType, Map<String, String> queryParams) {
        String url = buildUrl(path, queryParams);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + auth.getToken())
                .get()
                .build();
        JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
        JavaType apiRespType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, listType);
        return executeApi(request, apiRespType);
    }

    public <T> ApiResponse.PageResult<T> getPage(String path, Class<T> dataType, Map<String, String> queryParams) {
        String url = buildUrl(path, queryParams);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + auth.getToken())
                .get()
                .build();
        return executeRaw(request, new TypeReference<>() {});
    }

    public <T> T post(String path, Object body, Class<T> dataType) {
        String url = buildUrl(path, Map.of());
        try {
            String json = mapper.writeValueAsString(body);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + auth.getToken())
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();
            return execute(request, dataType);
        } catch (IOException e) {
            throw new UfsClientException("Failed to serialize request body", e);
        }
    }

    public <T> T put(String path, Object body, Class<T> dataType) {
        String url = buildUrl(path, Map.of());
        try {
            String json = mapper.writeValueAsString(body);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + auth.getToken())
                    .header("Content-Type", "application/json")
                    .put(RequestBody.create(json, JSON))
                    .build();
            return execute(request, dataType);
        } catch (IOException e) {
            throw new UfsClientException("Failed to serialize request body", e);
        }
    }

    public void delete(String path) {
        String url = buildUrl(path, Map.of());
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + auth.getToken())
                .delete()
                .build();
        executeVoid(request);
    }

    // ---- internal ----

    private String buildUrl(String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(baseUrl).append(path);
        if (params != null && !params.isEmpty()) {
            boolean first = true;
            for (var entry : params.entrySet()) {
                sb.append(first ? '?' : '&');
                sb.append(entry.getKey()).append('=').append(entry.getValue());
                first = false;
            }
        }
        return sb.toString();
    }

    /** 将 Class&lt;T&gt; 包装为 ApiResponse&lt;T&gt; 后反序列化 */
    private <T> T execute(Request request, Class<T> dataType) {
        JavaType apiRespType = mapper.getTypeFactory()
                .constructParametricType(ApiResponse.class, mapper.constructType(dataType));
        return executeApi(request, apiRespType);
    }

    /** 核心反序列化方法：apiResponseType 是完整的 ApiResponse&lt;T&gt; JavaType */
    @SuppressWarnings("unchecked")
    private <T> T executeApi(Request request, JavaType apiResponseType) {
        try (Response response = httpClient.newCall(request).execute()) {
            String bodyStr = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                throw new UfsClientException(response.code(), "HTTP " + response.code() + ": " + bodyStr);
            }
            ApiResponse<T> apiResp = mapper.readValue(bodyStr, apiResponseType);
            if (!apiResp.isSuccess()) {
                throw new UfsClientException(apiResp.getCode(), apiResp.getMessage());
            }
            return apiResp.getData();
        } catch (IOException e) {
            throw new UfsClientException("HTTP request failed: " + request.url(), e);
        }
    }

    private <T> ApiResponse.PageResult<T> executeRaw(Request request, TypeReference<ApiResponse.PageResult<T>> typeRef) {
        try (Response response = httpClient.newCall(request).execute()) {
            String bodyStr = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                throw new UfsClientException(response.code(), "HTTP " + response.code() + ": " + bodyStr);
            }
            ApiResponse<ApiResponse.PageResult<T>> apiResp = mapper.readValue(bodyStr,
                    mapper.getTypeFactory().constructParametricType(ApiResponse.class,
                            mapper.getTypeFactory().constructParametricType(ApiResponse.PageResult.class, Object.class)));
            if (!apiResp.isSuccess()) {
                throw new UfsClientException(apiResp.getCode(), apiResp.getMessage());
            }
            return apiResp.getData();
        } catch (IOException e) {
            throw new UfsClientException("HTTP request failed: " + request.url(), e);
        }
    }

    private void executeVoid(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String bodyStr = response.body() != null ? response.body().string() : "";
                throw new UfsClientException(response.code(), "HTTP " + response.code() + ": " + bodyStr);
            }
        } catch (IOException e) {
            throw new UfsClientException("HTTP request failed: " + request.url(), e);
        }
    }
}

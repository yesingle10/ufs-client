package com.ufs.client.api;

import com.ufs.client.http.UfsHttpClient;
import com.ufs.client.model.ApiResponse;
import com.ufs.client.model.ObjectMeta;
import com.ufs.client.model.PresignResult;

import java.util.Map;

/**
 * 文件对象 API。
 */
public class ObjectApi {

    private final UfsHttpClient http;

    public ObjectApi(UfsHttpClient http) {
        this.http = http;
    }

    /** 简单上传预签名 URL */
    public PresignResult presignUpload(String bucket, String objectKey, String fileName,
                                        String contentType, long fileSize) {
        return http.post("/api/v1/objects/presign-upload",
                Map.of("bucket", bucket, "objectKey", objectKey,
                        "fileName", fileName, "contentType", contentType, "fileSize", fileSize),
                PresignResult.class);
    }

    /** 分片上传初始化 + 预签名 */
    @SuppressWarnings("unchecked")
    public PresignResult presignMultipartUpload(String bucket, String objectKey, String fileName,
                                                 String contentType, long fileSize, int partCount) {
        var resp = http.post("/api/v1/objects/presign-multipart",
                Map.of("bucket", bucket, "objectKey", objectKey,
                        "fileName", fileName, "contentType", contentType,
                        "fileSize", fileSize, "partCount", partCount),
                Map.class);
        // 服务端返回含 uploadId + parts 的复合结构
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return mapper.convertValue(resp, PresignResult.class);
    }

    /** 完成分片上传 */
    public ObjectMeta completeUpload(String bucket, String objectKey, String uploadId,
                                      java.util.List<Map<String, Object>> parts) {
        return http.post("/api/v1/objects/complete-upload",
                Map.of("bucket", bucket, "objectKey", objectKey,
                        "uploadId", uploadId, "parts", parts),
                ObjectMeta.class);
    }

    /** 列举对象 */
    public ApiResponse.PageResult<ObjectMeta> list(String bucket, String prefix, String cursor, int limit) {
        var params = new java.util.HashMap<String, String>();
        params.put("bucket", bucket);
        if (prefix != null) params.put("prefix", prefix);
        if (cursor != null) params.put("cursor", cursor);
        params.put("limit", String.valueOf(limit));
        return http.getPage("/api/v1/objects", ObjectMeta.class, params);
    }

    /** 获取对象元数据 */
    public ObjectMeta head(String bucket, String objectKey) {
        return http.get("/api/v1/objects/head?bucket=" + bucket + "&objectKey=" + objectKey,
                ObjectMeta.class);
    }

    /** 获取下载预签名 URL */
    public PresignResult presignDownload(String bucket, String objectKey, String fileName) {
        return http.post("/api/v1/objects/presign-download",
                Map.of("bucket", bucket, "objectKey", objectKey, "fileName", fileName),
                PresignResult.class);
    }

    /** 删除对象 */
    public void delete(String bucket, String objectKey) {
        http.delete("/api/v1/objects?bucket=" + bucket + "&objectKey=" + objectKey);
    }
}

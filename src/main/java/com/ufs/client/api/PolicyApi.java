package com.ufs.client.api;

import com.ufs.client.http.UfsHttpClient;
import com.ufs.client.model.StoragePolicy;

import java.util.List;
import java.util.Map;

/**
 * 存储策略 API。
 */
public class PolicyApi {

    private final UfsHttpClient http;

    public PolicyApi(UfsHttpClient http) {
        this.http = http;
    }

    /** 列举活跃策略 */
    public List<StoragePolicy> listActive() {
        return http.getList("/api/v1/policies/active", StoragePolicy.class);
    }

    /** 获取单个策略 */
    public StoragePolicy get(String bizType) {
        return http.get("/api/v1/policies/" + bizType, StoragePolicy.class);
    }

    /** 创建策略 */
    public StoragePolicy create(String bizType, String vendor, String bucket,
                                  String prefix, String acl, int ttlDays) {
        return http.post("/api/v1/policies",
                Map.of("bizType", bizType, "vendor", vendor, "bucket", bucket,
                        "prefix", prefix, "acl", acl, "ttlDays", ttlDays),
                StoragePolicy.class);
    }

    /** 更新策略 */
    public StoragePolicy update(String bizType, String bucket, String prefix,
                                  String acl, Integer ttlDays) {
        var body = new java.util.HashMap<String, Object>();
        if (bucket != null) body.put("bucket", bucket);
        if (prefix != null) body.put("prefix", prefix);
        if (acl != null) body.put("acl", acl);
        if (ttlDays != null) body.put("ttlDays", ttlDays);
        return http.put("/api/v1/policies/" + bizType, body, StoragePolicy.class);
    }

    /** 删除策略 */
    public void delete(String bizType) {
        http.delete("/api/v1/policies/" + bizType);
    }
}

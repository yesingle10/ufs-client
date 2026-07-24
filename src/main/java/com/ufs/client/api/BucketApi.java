package com.ufs.client.api;

import com.ufs.client.http.UfsHttpClient;
import com.ufs.client.model.Bucket;

import java.util.List;
import java.util.Map;

/**
 * 存储桶 API。
 */
public class BucketApi {

    private final UfsHttpClient http;

    public BucketApi(UfsHttpClient http) {
        this.http = http;
    }

    /** 列举当前租户所有桶 */
    public List<Bucket> list() {
        return http.getList("/api/v1/buckets", Bucket.class);
    }

    /** 获取单个桶详情 */
    public Bucket get(String bucketName) {
        return http.get("/api/v1/buckets/" + bucketName, Bucket.class);
    }

    /** 创建存储桶 */
    public Bucket create(String name, String vendor, String region, String acl) {
        return http.post("/api/v1/buckets",
                Map.of("name", name, "vendor", vendor, "region", region, "acl", acl),
                Bucket.class);
    }

    /** 更新桶属性 */
    public Bucket update(String bucketName, String acl, Map<String, String> meta) {
        var body = new java.util.HashMap<String, Object>();
        if (acl != null) body.put("acl", acl);
        if (meta != null) body.put("meta", meta);
        return http.put("/api/v1/buckets/" + bucketName, body, Bucket.class);
    }

    /** 删除桶 */
    public void delete(String bucketName) {
        http.delete("/api/v1/buckets/" + bucketName);
    }
}

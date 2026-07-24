package com.ufs.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** 存储桶 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bucket {
    private String name;
    private String vendor;
    private String region;
    private String acl;
    private Map<String, String> meta;
    private long createdAt;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getAcl() { return acl; }
    public void setAcl(String acl) { this.acl = acl; }
    public Map<String, String> getMeta() { return meta; }
    public void setMeta(Map<String, String> meta) { this.meta = meta; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

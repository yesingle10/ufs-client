package com.ufs.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 存储策略 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoragePolicy {
    private String policyId;
    private String bizType;
    private String vendor;
    private String bucket;
    private String prefix;
    private String acl;
    private int ttlDays;
    private boolean active;

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public String getAcl() { return acl; }
    public void setAcl(String acl) { this.acl = acl; }
    public int getTtlDays() { return ttlDays; }
    public void setTtlDays(int ttlDays) { this.ttlDays = ttlDays; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

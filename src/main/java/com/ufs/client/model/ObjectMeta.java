package com.ufs.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 文件对象 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectMeta {
    private String fileId;
    private String objectKey;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String md5;
    private String url;
    private int status;
    private long createdAt;

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

package com.ufs.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 通用 API 响应包装（对应 UFS Core 的 {@code ApiResponse<T>}）。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private String requestId;
    private long timestamp;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isSuccess() { return code == 0; }

    // ---- 分页 ----

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageResult<T> {
        private List<T> list;
        private String nextCursor;
        private boolean hasMore;

        public List<T> getList() { return list; }
        public void setList(List<T> list) { this.list = list; }
        public String getNextCursor() { return nextCursor; }
        public void setNextCursor(String nextCursor) { this.nextCursor = nextCursor; }
        public boolean isHasMore() { return hasMore; }
        public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }
    }
}

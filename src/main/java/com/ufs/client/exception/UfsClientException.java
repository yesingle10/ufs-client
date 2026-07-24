package com.ufs.client.exception;

/**
 * UFS 客户端异常。
 */
public class UfsClientException extends RuntimeException {

    private final int statusCode;

    public UfsClientException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public UfsClientException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public UfsClientException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public int getStatusCode() { return statusCode; }
}

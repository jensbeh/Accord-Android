package com.accord.net.rest;

public class GetRequestsWithObject {
    private String userKey = null;
    private String status = null;
    private String message = null;
    private Object data = null;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}

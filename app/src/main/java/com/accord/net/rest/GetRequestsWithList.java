package com.accord.net.rest;

import java.util.List;

public class GetRequestsWithList {
    private String userKey = null;
    private String status = null;
    private String message = null;
    private List data = null;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List getData() {
        return data;
    }
}
package com.accord.net;

import java.util.Map;

public class PostRequests {

    private String userKey = null;
    private String name = null;
    private String password = null;
    private String status = null;
    private String message = null;
    private Map data = null;

    public PostRequests(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map getData() {
        return data;
    }

    public String toString() {
        return "Post{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' + '}';
    }
}
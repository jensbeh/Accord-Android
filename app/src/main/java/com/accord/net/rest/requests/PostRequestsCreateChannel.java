package com.accord.net.rest.requests;

public class PostRequestsCreateChannel {
    private String name;
    private String type;
    private boolean privileged;

    public PostRequestsCreateChannel(String name, String type, boolean privileged) {
        this.name = name;
        this.type = type;
        this.privileged = privileged;
    }
}
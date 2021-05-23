package com.accord.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;


public class CustomWebSocketConfigurator extends ClientEndpointConfig.Configurator {
    private final String name;
    public static final String COM_NAME = "userKey";

    public CustomWebSocketConfigurator(String name) {
        this.name = name;
    }

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        super.beforeRequest(headers);
        ArrayList<String> key = new ArrayList<>();
        key.add(this.name);
        headers.put(COM_NAME, key);
    }
}
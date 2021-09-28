package com.accord.net.webSocket;

import org.json.JSONObject;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public interface WSCallback {
    void handleMessage(JSONObject msg);

    void onClose(Session session, CloseReason closeReason);
}
package com.accord.net;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public interface WSCallback {
    void handleMessage(String msg);
    void onClose(Session session, CloseReason closeReason);
}
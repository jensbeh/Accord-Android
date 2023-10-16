package com.accord.util;

public class Constants {
    // Server
    public static final String WEBSOCKET_PATH = "/ws";
    public static final String CHAT_WEBSOCKET_PATH = "/chat?user=";
    public static final String SYSTEM_WEBSOCKET_PATH = "/system";
    public static final String SERVER_SYSTEM_WEBSOCKET_PATH = "/system?serverId=";
    public static final String SERVER_WEBSOCKET_PATH = "&serverId=";
    //    public static final String API_PREFIX = "api/";
    public static final String USERS_PATH = "users";
    public static final String LOGIN_PATH = "users/login";
    public static final String LOGOUT_PATH = "users/logout";
    public static final String TEMP_USER_PATH = "users/temp";
    public static final String SERVER_PATH = "servers";
    public static final String CATEGORIES_PATH = "/categories";
    public static final String CHANNELS_PATH = "/channels";
    public static final String MESSAGES_PATH = "/messages?timestamp=";
    public static final String SUCCESS = "success";


    // Communication
    public static final String COM_USERKEY = "userKey";
    public static final String COM_NOOP = "noop";

    // Client
    public static String REST_SERVER_URL = "http://xxx:8080/"; // https://ac.uniks.de/api/
    public static String WS_SERVER_URL = "ws://xxx:8080"; // wss://ac.uniks.de
    public static void setIpAddress(String restServerUrl) {
        REST_SERVER_URL = "http://" + restServerUrl + ":8080/";
        WS_SERVER_URL = "ws://" + restServerUrl + ":8080";
    }

}
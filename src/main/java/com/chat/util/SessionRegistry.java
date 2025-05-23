package com.chat.util;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {
    private static final ConcurrentHashMap<String, HttpSession> sessions = new ConcurrentHashMap<>();

    public static void addSession(String username, HttpSession session) {
        sessions.put(username, session);
    }

    public static HttpSession getSession(String username) {
        return sessions.get(username);
    }

}

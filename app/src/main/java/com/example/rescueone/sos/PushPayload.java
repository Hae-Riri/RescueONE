package com.example.rescueone.sos;

import androidx.annotation.Keep;

@Keep
public class PushPayload {
    String key;
    String title;
    String message;
    String token;

    public PushPayload() {
    }

    public PushPayload(String key, String title, String message, String token) {
        this.key = key;
        this.title = title;
        this.message = message;
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

package com.example.rescueone.db_server;

import androidx.annotation.Keep;

import java.util.LinkedHashMap;
import java.util.Map;

@Keep //난독화 방지
public class User {
    String name;
    String uid;
    String email;
    String phoneNumber;
    String token;
    Map<String,String >emergencyContact = new LinkedHashMap<>();

    public User() {//빈 생성자 반드시 필요
    }

    public User(String name, String uid, String email, String phoneNumber, String token, Map<String, String> emergencyContact) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.token = token;
        this.emergencyContact = emergencyContact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, String> getEmergencyContact() {
        return emergencyContact;
    }
    public void setEmergencyContact(Map<String, String> emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}


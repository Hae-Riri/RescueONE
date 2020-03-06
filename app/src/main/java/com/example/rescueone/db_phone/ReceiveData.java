package com.example.rescueone.db_phone;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ReceiveData {
    @PrimaryKey
    @NonNull
    private String phone;
    @NonNull
    private String name;

    public ReceiveData(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ReceiveData{" +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

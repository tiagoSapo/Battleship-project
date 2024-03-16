package com.example.projetofinalcm2022.models;

import android.graphics.Bitmap;

public class Profile {
    private long id;
    private String name;
    private String password;
    private byte[] bitmap;

    public Profile(long id, String name, String password, byte[] bitmap) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.bitmap = bitmap;
    }

    public Profile() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }

    public boolean isValid() {
        return (name != null && password != null && bitmap != null);
    }
}

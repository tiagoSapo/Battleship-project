package com.example.projetofinalcm2022.models.firebase;

import com.google.gson.Gson;

public class FirebaseMessage {

    String role;
    String message;
    int pos1, pos2, pos3;

    public FirebaseMessage(String role, String message, int pos1, int pos2, int pos3) {
        this.role = role;
        this.message = message;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
    }

    public FirebaseMessage(String role, String message) {
        this.role = role;
        this.message = message;
    }

    public FirebaseMessage() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPos1() {
        return pos1;
    }

    public void setPos1(int pos1) {
        this.pos1 = pos1;
    }

    public int getPos2() {
        return pos2;
    }

    public void setPos2(int pos2) {
        this.pos2 = pos2;
    }

    public int getPos3() {
        return pos3;
    }

    public void setPos3(int pos3) {
        this.pos3 = pos3;
    }

    @Override
    public String toString() {
        return "FirebaseMessage{" +
                "role='" + role + '\'' +
                ", message='" + message + '\'' +
                ", pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", pos3=" + pos3 +
                '}';
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
    public boolean isFromGuest() {
        return role.equals("guest");
    }
    public boolean isFromHost() {
        return role.equals("host");
    }


}

package com.example.projetofinalcm2022.models.firebase;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInfo implements Serializable {
    private String name;
    private Bitmap photo;
    private int[][] board;

    public PlayerInfo(String name, int[][] board, Bitmap photo) {
        this.name = name;
        this.board = board;
        this.photo = photo;
    }

    public PlayerInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}

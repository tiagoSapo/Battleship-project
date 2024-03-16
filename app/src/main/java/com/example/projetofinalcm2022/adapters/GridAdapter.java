package com.example.projetofinalcm2022.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.fragments.PiecesFragment;
import com.example.projetofinalcm2022.models.GameBoard;

import java.io.Serializable;

public class GridAdapter extends BaseAdapter {

    private GameBoard gameBoard;
    private MainActivity activity;
    private PiecesFragment fragment;

    public GridAdapter(GameBoard gameBoard, MainActivity activity, PiecesFragment fragment) {
        this.gameBoard = gameBoard;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return gameBoard.getBoardSize();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView iv = new ImageView(fragment.getContext());
        if (view == null) {
            if (activity.getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                iv.setLayoutParams(new GridView.LayoutParams(70, 70));
            } else {
                iv.setLayoutParams(new GridView.LayoutParams(85, 85));
            }
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(8,8,8,8);
        }
        else {
            iv = (ImageView) view;
        }

        return gameBoard.putImageBasedOnNumbers(iv, i);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
package com.example.projetofinalcm2022.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.models.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<History> {

    private Context context;

    public HistoryAdapter(Context context, List<History> games) {
        super(context, R.layout.history_line, games);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = LayoutInflater.from(context);
        convertView = li.inflate(R.layout.history_line, parent,false);

        TextView mode = convertView.findViewById(R.id.mode);
        TextView winner = convertView.findViewById(R.id.winner);
        TextView date = convertView.findViewById(R.id.date);
        TextView shots = convertView.findViewById(R.id.shots);
        TextView boatsHits = convertView.findViewById(R.id.boats_hits);
        TextView sunkenShips = convertView.findViewById(R.id.sunken_ships);

        History game = getItem(position);

        mode.setText(game.getMode());
        winner.setText(game.getWinner());
        date.setText(game.getDate());
        shots.setText(game.getNumberOfShots() + "");
        boatsHits.setText(String.valueOf(game.getBoatHits()));
        sunkenShips.setText(String.valueOf(game.getSunkenShips()));

        return convertView;
    }
}

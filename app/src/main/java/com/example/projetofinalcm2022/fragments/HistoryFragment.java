package com.example.projetofinalcm2022.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.adapters.HistoryAdapter;
import com.example.projetofinalcm2022.models.History;
import com.example.projetofinalcm2022.threads.TaskManager;

import java.util.List;

public class HistoryFragment extends Fragment implements TaskManager.CallbackGetAllHistory {

    private MainActivity activity;

    /** Views **/
    private ListView listView;
    private TextView emptyTextView, numberOfGamesTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        activity = (MainActivity) getActivity();

        listView = view.findViewById(android.R.id.list);
        numberOfGamesTextView = view.findViewById(R.id.number_of_games);
        emptyTextView = view.findViewById(android.R.id.empty);

        getGameHistory();
        return view;
    }

    private void updateNumberOfGamesPlayed(int numberOfGames) {
        String msg = numberOfGames + " games";
        numberOfGamesTextView.setText(msg);
    }
    private void getGameHistory() {
        new TaskManager().getHistories(activity.database, this);
    }

    @Override
    public void onCompleteGetAll(List<History> histories) {
        listView.setAdapter(new HistoryAdapter(activity, histories));
        listView.setEmptyView(emptyTextView);
        updateNumberOfGamesPlayed(histories.size());
    }

    /**
     *   TOOLBAR
     **/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_history_menu, menu);

        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Battleship");
        ab.setDisplayShowTitleEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


}
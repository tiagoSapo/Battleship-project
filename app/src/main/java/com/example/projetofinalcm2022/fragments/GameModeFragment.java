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
import android.widget.Button;
import android.widget.Toast;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.models.BatalhaNaval;

public class GameModeFragment extends Fragment {

    private MainActivity activity;

    /** Views **/
    private Button singlePlayerButton, multiplayerButton, arduinoButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_mode, container, false);
        activity = (MainActivity)getActivity();

        singlePlayerButton = view.findViewById(R.id.single_player);
        multiplayerButton = view.findViewById(R.id.multiplayer);
        arduinoButton = view.findViewById(R.id.arduino);

        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.viewModel.setGameMode(BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_CPU.toString());
                activity.startLoginFragment();
            }
        });
        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.viewModel.setGameMode(BatalhaNaval.ModoDeJogo.MULTIPLAYER.toString());
                activity.startLoginFragment();
            }
        });
        arduinoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.viewModel.setGameMode(BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_ARDUINO.toString());
                activity.startLoginFragment();
            }
        });

        return view;
    }

    /**
     *   TOOLBAR
     **/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_intro_menu, menu);

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
            case R.id.about_app:
                Toast.makeText(activity, "Select game mode (Opponent CPU or another player on the network)",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
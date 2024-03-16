package com.example.projetofinalcm2022.fragments;

import static com.example.projetofinalcm2022.utils.Utils.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.adapters.GridAdapter;
import com.example.projetofinalcm2022.models.BatalhaNaval;
import com.example.projetofinalcm2022.models.GameBoard;
import com.example.projetofinalcm2022.models.Profile;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class PiecesFragment extends Fragment {

    /** Buttons counters **/
    private int cnt_b1, cnt_b2, cnt_b3, cnt_b5;
    private int selected_boat = 1;

    /** Game board **/
    private GameBoard gameBoard;

    /** Activity **/
    private MainActivity activity;

    /** Adapters **/
    private GridAdapter boardGridAdapter;

    /** User that will play the game **/
    private Profile profile;

    /** Views **/
    private Button boat1Button, boat2Button, boat3Button, boat5Button, clearBoatsButton;
    private Switch switchPosition, switchBoat5;
    private TextView selectedBoatTextView;
    private GridView boardGridView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pieces, container, false);
        activity = (MainActivity) getActivity();

        Log.e("abc", String.valueOf(activity.getSupportFragmentManager().getFragments()));

        gameBoard = new GameBoard();
        boardGridAdapter = new GridAdapter(gameBoard, activity, this);

        selectedBoatTextView = view.findViewById(R.id.selected_boat);
        boat1Button = view.findViewById(R.id.boat1_button);
        boat2Button = view.findViewById(R.id.boat2_button);
        boat3Button = view.findViewById(R.id.boat3_button);
        boat5Button = view.findViewById(R.id.boat5_button);
        clearBoatsButton = view.findViewById(R.id.clear_boats);

        switchPosition = view.findViewById(R.id.switch_vertical_horizontal);
        switchPosition.setChecked(true);
        switchPosition.setText("Vertical");
        switchPosition.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    compoundButton.setText("Vertical");
                else
                    compoundButton.setText("Horizontal");
            }
        });
        switchBoat5 = view.findViewById(R.id.switch_boat_5);
        switchBoat5.setChecked(false);
        switchBoat5.setText("Normal position (boat 5 only)");
        switchBoat5.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    compoundButton.setText("Alternative position (boat 5 only)");
                else
                    compoundButton.setText("Normal position (boat 5 only)");
            }
        });
        resetCounterForButtons();
        initializeButtons();

        clearBoatsButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                gameBoard.reset();
                resetCounterForButtons();
                boardGridAdapter.notifyDataSetChanged();
            }
        });

        boardGridView = view.findViewById(R.id.grid_board);
        boardGridView.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                putBoatsOnTheBoard(i);
            }
        });
        boardGridView.setAdapter(boardGridAdapter);

        return view;
    }

    /**
     * TOOLBAR
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_pieces_menu, menu);

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
            case R.id.start_pieces:
                startGame();
                break;
            case R.id.help_pieces:
                help();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * LOGIC METHODS
     */
    private void resetCounterForButtons() {
        cnt_b1 = cnt_b2 = cnt_b3 = 2;
        cnt_b5 = 1;

        boat1Button.setText("Boat 1 " + " (" + cnt_b1 + ")");
        boat2Button.setText("Boat 2 " + " (" + cnt_b2 + ")");
        boat3Button.setText("Boat 3 " + " (" + cnt_b3 + ")");
        boat5Button.setText("Boat 5 "+ " (" + cnt_b5 + ")");
    }
    private void initializeButtons() {
        boat1Button.setTag(1);
        boat2Button.setTag(2);
        boat3Button.setTag(3);
        boat5Button.setTag(5);
        Button.OnClickListener buttonsListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                selected_boat = (Integer) button.getTag();
                selectedBoatTextView.setText("Boat " + selected_boat + " selected");
            }
        };
        boat1Button.setOnClickListener(buttonsListener);
        boat2Button.setOnClickListener(buttonsListener);
        boat3Button.setOnClickListener(buttonsListener);
        boat5Button.setOnClickListener(buttonsListener);
    }

    private void putBoatsOnTheBoard(Integer selectedPosition) {

        String titleCnt = "All boats used";
        String msgCnt = "You already putted all boats of type ";

        String titlePos = "Invalid position";
        String msgPos = "Please leave one cell distance from the other boats";

        switch (selected_boat) {
            case 1:
                if (cnt_b1 <= 0) {
                    Utils.createInfoDialog(activity, titleCnt, msgCnt + "[1]", R.drawable.warning).show();
                    return;
                }
                if (gameBoard.insertBoat1(selectedPosition)) {
                    boardGridAdapter.notifyDataSetChanged();
                    cnt_b1--;
                    boat1Button.setText("Boat 1 (" + cnt_b1 + ")");
                } else {
                    Utils.createInfoDialog(activity, titlePos, msgPos, R.drawable.warning).show();
                }
                break;
            case 2:
                if (cnt_b2 <= 0) {
                    Utils.createInfoDialog(activity, titleCnt, msgCnt + "[2]", R.drawable.warning).show();
                    return;
                }
                if (gameBoard.insertBoat2(selectedPosition, switchPosition.isChecked())) {
                    boardGridAdapter.notifyDataSetChanged();
                    cnt_b2--;
                    boat2Button.setText("Boat 2 (" + cnt_b2 + ")");
                } else {
                    Utils.createInfoDialog(activity, titlePos, msgPos, R.drawable.warning).show();
                }
                break;
            case 3:
                if (cnt_b3 <= 0) {
                    Utils.createInfoDialog(activity, titleCnt, msgCnt + "[3]", R.drawable.warning).show();
                    return;
                }
                if (gameBoard.insertBoat3(selectedPosition, switchPosition.isChecked())) {
                    boardGridAdapter.notifyDataSetChanged();
                    cnt_b3--;
                    boat3Button.setText("Boat 3 (" + cnt_b3 + ")");
                } else {
                    Utils.createInfoDialog(activity, titlePos, msgPos, R.drawable.warning).show();
                }
                break;
            case 5:
                if (cnt_b5 <= 0) {
                    Utils.createInfoDialog(activity, titleCnt, msgCnt + "[5]", R.drawable.warning).show();
                    return;
                }
                if (gameBoard.insertBoat5(selectedPosition, switchPosition.isChecked(), switchBoat5.isChecked())) {
                    boardGridAdapter.notifyDataSetChanged();
                    cnt_b5--;
                    boat5Button.setText("Boat 5 (" + cnt_b5 + ")");
                } else {
                    Utils.createInfoDialog(activity, titlePos, msgPos, R.drawable.warning).show();
                }
                break;
        }
    }

    private void help() {
        Toast.makeText(activity, "Put all boats in the desired positions", Toast.LENGTH_SHORT).show();
    }

    private void startGame() {
        if (cnt_b1 == 0 && cnt_b2 == 0 && cnt_b3 == 0 && cnt_b5 == 0) {
            activity.viewModel.setGameBoard(gameBoard.getBoardIn2D());
            String gameMode = activity.viewModel.getGameMode().getValue();
            switch (gameMode) {
                case "SINGLE_PLAYER_CPU":
                    startSinglePlayerGame();
                    break;
                case "SINGLE_PLAYER_ARDUINO":
                    startArduinoGame();
                    break;
                case "MULTIPLAYER":
                    startMultiplayerGame();
                    break;
                default:
            }

        }
        else
            Toast.makeText(activity, "You did not put all the boats!", Toast.LENGTH_SHORT).show();
    }

    private void startSinglePlayerGame() {
        activity.startSinglePlayerGameFragment();
    }
    private void startMultiplayerGame() {

        String playerName;
        DatabaseReference playerRef;

        playerName = activity.viewModel.getProfileName().getValue();
        playerRef = activity.firebaseDB.getReference("players/" + playerName);
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activity.startRoomFragment(); // INICAR JOGO MULTIPLAYER
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("firebase", "Error!");
            }
        });
        playerRef.setValue("");
    }
    private void startArduinoGame() {
        activity.startArduinoGameFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if (boardGridAdapter != null) {
            outState.putInt(BUTTON1_COUNTER, cnt_b1);
            outState.putInt(BUTTON2_COUNTER, cnt_b2);
            outState.putInt(BUTTON3_COUNTER, cnt_b3);
            outState.putInt(BUTTON5_COUNTER, cnt_b5);
            outState.putString(BOARD_TEMP, new Gson().toJson(boardGridAdapter.getGameBoard()));
            //outState.putSerializable(BOARD_TEMP, boardGridAdapter.getGameBoard());
            outState.putString(TV_SELECTED_BOAT, selectedBoatTextView.getText().toString());
            outState.putBoolean(SW_POSITION, switchPosition.isChecked());
            outState.putBoolean(SW_BOAT_5, switchBoat5.isChecked());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.e("abc", "[Pieces Frag] onViewStateRestored");

        if (savedInstanceState != null && savedInstanceState.getString(BOARD_TEMP) != null) {
            cnt_b1 = savedInstanceState.getInt(BUTTON1_COUNTER, 2);
            cnt_b2 = savedInstanceState.getInt(BUTTON2_COUNTER, 2);
            cnt_b3 = savedInstanceState.getInt(BUTTON3_COUNTER, 2);
            cnt_b5 = savedInstanceState.getInt(BUTTON5_COUNTER, 1);
            boat1Button.setText("Boat 1 (" + cnt_b1 + ")");
            boat2Button.setText("Boat 2 (" + cnt_b2 + ")");
            boat3Button.setText("Boat 3 (" + cnt_b3 + ")");
            boat5Button.setText("Boat 5 (" + cnt_b5 + ")");

            selectedBoatTextView.setText(savedInstanceState.getString(TV_SELECTED_BOAT));

            switchPosition.setChecked(savedInstanceState.getBoolean(SW_POSITION));
            switchBoat5.setChecked(savedInstanceState.getBoolean(SW_BOAT_5));

            //gameBoard = (GameBoard) savedInstanceState.getSerializable(BOARD_TEMP);
            gameBoard = new Gson().fromJson(savedInstanceState.getString(BOARD_TEMP), GameBoard.class);
            boardGridAdapter.setGameBoard(gameBoard);
            boardGridAdapter.notifyDataSetChanged();
        }
        super.onViewStateRestored(savedInstanceState);
    }


}
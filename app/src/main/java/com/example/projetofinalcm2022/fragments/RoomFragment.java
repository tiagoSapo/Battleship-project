package com.example.projetofinalcm2022.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.models.MyViewModel;
import com.example.projetofinalcm2022.models.firebase.PlayerInfo;
import com.example.projetofinalcm2022.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {

    /** Activity **/
    private MainActivity activity;
    private MyViewModel viewModel;
    private FirebaseDatabase firebaseDB;

    /** Views **/
    private ListView listView;
    private Button button;

    /** Firebase References **/
    private DatabaseReference roomRef;
    private DatabaseReference roomsRef;

    /** Rooms list **/
    private List<String> roomsList;

    /** Player's name and room **/
    private String playerName;
    private String roomName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        /* activity **/
        activity = (MainActivity)getActivity();
        viewModel = activity.viewModel;
        firebaseDB = activity.firebaseDB;

        /* player and room names */
        playerName = viewModel.getProfileName().getValue();
        roomName = playerName;

        /* views */
        listView = view.findViewById(R.id.listView);
        button = view.findViewById(R.id.button);

        /* all  available (existing) rooms */
        roomsList = new ArrayList<>();

        /* view's listeners */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* create room and add yourself as player1 (HOST) */
                button.setText("CREATING ROOM");
                button.setEnabled(false);
                roomName = playerName;
                roomRef = firebaseDB.getReference("rooms/" + roomName + "/player1");
                addRoomListener();

                String playerInfo = new Gson().toJson(new PlayerInfo(playerName, viewModel.getGameBoard().getValue(), Utils.getBitmapFromBytes(viewModel.getProfilePhoto().getValue())));
                roomRef.setValue(playerInfo);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /* join existing room and add yourself as player2 (GUEST)*/
                roomName = roomsList.get(position);
                roomRef = firebaseDB.getReference("rooms/" + roomName + "/player2");
                addRoomListener();

                String playerInfo = new Gson().toJson(new PlayerInfo(playerName, viewModel.getGameBoard().getValue(), Utils.getBitmapFromBytes(viewModel.getProfilePhoto().getValue())));
                roomRef.setValue(playerInfo);
            }
        });

        /* show if new room is available */
        addRoomsListener();
        return view;
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

    private void addRoomListener() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /* join the room */
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                viewModel.setRoomName(roomName);
                activity.startMultiplayerGameFragment(); // START MULTIPLAYER FRAGMENT (as HOST or GUEST)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Toast.makeText(activity, "Error joining room!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsListener() {
        roomsRef = firebaseDB.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /* show list of rooms */
                roomsList.clear();
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for (DataSnapshot snapshot: rooms) {
                    roomsList.add(snapshot.getKey());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {Log.e("firebase", "Error adding rooms to adapter");}
        });
    }
}
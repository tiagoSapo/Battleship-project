package com.example.projetofinalcm2022.fragments.gamefragments;

import static com.example.projetofinalcm2022.models.BatalhaNaval.JOGADAS;
import static com.example.projetofinalcm2022.models.BatalhaNaval.ModoDeJogo.MULTIPLAYER;
import static com.example.projetofinalcm2022.models.BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_CPU;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.database.Database;
import com.example.projetofinalcm2022.models.BatalhaNaval;
import com.example.projetofinalcm2022.models.History;
import com.example.projetofinalcm2022.models.MyViewModel;
import com.example.projetofinalcm2022.models.Profile;
import com.example.projetofinalcm2022.models.firebase.FirebaseMessage;
import com.example.projetofinalcm2022.models.firebase.PlayerInfo;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameMultiPlayerFragment extends Fragment implements Serializable {

    /** Activity **/
    public MainActivity activity;
    private MyViewModel viewModel;
    private static final String BATALHA_NAVAL = "BATTLE_SHIP_GAME";

    /** Views **/
    private View view;
    private ImageView player1, player2;
    private GridView playerGrid;
    private GridView opponentGrid;

    /** BattleShip Game **/
    private BatalhaNaval battleshipGame;

    /** Game info **/
    private String roomName;
    private String playerName, playerName2;
    private String role;

    /** Game Status **/
    private boolean gameOver;
    private boolean gameReady;
    public boolean mutex; /* used to check if it's the player turn */
    private int[][] playerGameBoard;
    private int[][] opponentGameBoard;

    /** Firebase **/
    private FirebaseDatabase firebaseDB;
    private DatabaseReference messageRef;

    /** Last attack coordinates **/
    List<Integer> coordinates = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* View */
        View view = inflater.inflate(R.layout.fragment_multi_player, container, false);
        this.view = view;

        /* Activity and viewModel */
        activity = (MainActivity) getActivity();
        viewModel = activity.viewModel;
        firebaseDB = activity.firebaseDB;

        /* Player's role */
        roomName = viewModel.getRoomName().getValue();
        playerName = viewModel.getProfileName().getValue();
        role = getMyRole();
        gameReady = false;
        gameOver = false;
        mutex = getMutex();

        /* Prepare profile pictures of players */
        setupPhotos(view);

        /* send READY message to the opponent */
        String message = new FirebaseMessage(role, "READY").toJson();
        messageRef = firebaseDB.getReference("rooms/" + roomName + "/message");
        messageRef.setValue(message);

        /* References to get board of opponent (as host or as guest) */
        setBoardsListener();

        /* Setup player game */
        initializeGame(savedInstanceState);
        setPlayerBoard(view);

        /* Listen for incoming messages */
        setFirebaseListener();

        /* Show ProgressDialog - Wait for HOST to play first */
        if (role.equals("host")) new TaskManager().asyncWaitMultiplayerPreparation(this);
        return view;
    }


    /**
     * Firebase Reference Listeners
     */
    private void setBoardsListener() {
        DatabaseReference ref1 = firebaseDB.getReference("rooms/" + roomName + "/player1");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            // So' para o GUEST
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() == null)
                    return;
                if (snapshot.getValue(String.class).isEmpty())
                    return;

                if (!gameReady && role.equals("guest")) {
                    PlayerInfo playerInfo = new Gson().fromJson(snapshot.getValue(String.class), PlayerInfo.class);

                    /* Put photo */
                    /*Bitmap photo = playerInfo.getPhoto();
                    Drawable profilePic = new BitmapDrawable(getResources(), photo);
                    player2.setBackground(profilePic);*/

                    playerName2 = playerInfo.getName();

                    opponentGameBoard = playerInfo.getBoard();
                    battleshipGame.setFirebaseBoard(opponentGameBoard);
                    GameMultiPlayerFragment.this.setOpponentBoard();
                    Log.w("firebase", "[Guest] Board do host atualizado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (role.equals("guest")) {
                    Log.e("firebase", "onCancelled - player 1");
                }
            }
        });
        DatabaseReference ref2 = firebaseDB.getReference("rooms/" + roomName + "/player2");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e("firebase", snapshot.toString());

                if (snapshot.getValue() == null)
                    return;
                if (snapshot.getValue(String.class).isEmpty())
                    return;

                if (!gameReady && role.equals("host")) {
                    PlayerInfo playerInfo = new Gson().fromJson(snapshot.getValue(String.class), PlayerInfo.class);

                    /* Put photo */
                    /*Bitmap photo = playerInfo.getPhoto();
                    Drawable profilePic = new BitmapDrawable(getResources(), photo);
                    player2.setBackground(profilePic);*/

                    playerName2 = playerInfo.getName();

                    opponentGameBoard = playerInfo.getBoard();
                    battleshipGame.setFirebaseBoard(opponentGameBoard);
                    GameMultiPlayerFragment.this.setOpponentBoard();
                    Log.w("firebase", "[Guest] Board do host atualizado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (role.equals("host")) {
                    Log.e("firebase", "onCancelled - player 1");
                }
            }
        });
    }
    private void setFirebaseListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;
                if (snapshot.getValue(String.class).isEmpty())
                    return;

                FirebaseMessage msg = new Gson().fromJson(snapshot.getValue(String.class), FirebaseMessage.class);
                if (role.equals("host")) processHost(msg);
                else if (role.equals("guest")) processGuest(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error - retry
                Log.e("Firebase", "[Multiplayer] onCancelled error" + error);
            }

            private void processHost(FirebaseMessage firebaseMessage) {

                /* Ignorar as minhas mensagens (como Host) */
                if (!firebaseMessage.isFromGuest())
                    return;

                /* se for uma msg de READY -> preparar o tabuleiro do adversario */
                if (!gameReady && firebaseMessage.getMessage().equals("READY")) {
                    gameReady = true;
                    mutex = true;
                    Log.w("firebase", "[Host] Estamos prontos");
                }


                // FROM HERE on, all message are from the GUEST
                if (gameReady && !mutex && firebaseMessage.getMessage().equals("ATTACK")) {
                    // PUT other player's play
                    battleshipGame.jogaMultiplayer(firebaseMessage.getPos1(), GameMultiPlayerFragment.this);
                    battleshipGame.jogaMultiplayer(firebaseMessage.getPos2(), GameMultiPlayerFragment.this);
                    battleshipGame.jogaMultiplayer(firebaseMessage.getPos3(), GameMultiPlayerFragment.this);
                    mutex = true;
                    if (battleshipGame.jogoAcabou() == 2) {
                        gameOver = true;
                        //sendGameOverToFirebase(true);
                        saveHistory(battleshipGame, playerName);
                        AlertDialog.Builder builder = new AlertDialog.Builder(GameMultiPlayerFragment.this.getContext());
                        builder.setCancelable(false)
                                .setTitle("Game is over")
                                .setMessage(R.string.WinMessage)
                                .setIcon(R.drawable.trophy)
                                .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        activity.startIntroFragment();
                                    }
                                }).show();
                        return;
                        //clearFirebaseRoom();
                    }
                    else if (battleshipGame.jogoAcabou() == 1) {
                        gameOver = true;
                        //sendGameOverToFirebase(false);
                        saveHistory(battleshipGame, playerName2);
                        AlertDialog.Builder builder = new AlertDialog.Builder(GameMultiPlayerFragment.this.getContext());
                        builder.setCancelable(false)
                                .setTitle("Game is over")
                                .setMessage(R.string.OpponentWonMessage)
                                .setIcon(R.drawable.lose)
                                .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        activity.startIntroFragment();
                                    }
                                }).show();
                        return;
                        //clearFirebaseRoom();
                    }
                    // I'm ready to play
                }

            }

            private void processGuest(FirebaseMessage firebaseMessage) {
                Log.w("firebase", "[Guest] Recebi isto: " + firebaseMessage);

                if (firebaseMessage.isFromGuest() && !gameReady && firebaseMessage.getMessage().equals("READY")) {
                    gameReady = true;
                    new TaskManager().asyncWaitMultiplayerGuestFirst(GameMultiPlayerFragment.this);
                    Log.w("firebase", "[Guest] Estamos prontos");
                }

                if (!firebaseMessage.isFromHost())
                    return;

                // FROM HERE on, all message are from the HOST
                if (gameReady && !mutex && firebaseMessage.getMessage().equals("ATTACK")) {
                    battleshipGame.jogadas_firebase = JOGADAS;
                    // PUT other player's play
                    battleshipGame.jogaMultiplayer(firebaseMessage.getPos1(), GameMultiPlayerFragment.this);
                    battleshipGame.jogaMultiplayer(firebaseMessage.getPos2(), GameMultiPlayerFragment.this);
                    battleshipGame.jogaMultiplayer(firebaseMessage.getPos3(), GameMultiPlayerFragment.this);
                    updateViews();
                    mutex = true;
                    if (battleshipGame.jogoAcabou() == 2) {
                        gameOver = true;
                        //sendGameOverToFirebase(true);
                        saveHistory(battleshipGame, playerName);
                        AlertDialog.Builder builder = new AlertDialog.Builder(GameMultiPlayerFragment.this.getContext());
                        builder.setCancelable(false)
                                .setTitle("Game is over")
                                .setMessage(R.string.WinMessage)
                                .setIcon(R.drawable.trophy)
                                .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        activity.startIntroFragment();
                                    }
                                }).show();
                        return;
                        //clearFirebaseRoom();
                    }
                    else if (battleshipGame.jogoAcabou() == 1) {
                        gameOver = true;
                        //sendGameOverToFirebase(false);
                        saveHistory(battleshipGame, playerName2);
                        AlertDialog.Builder builder = new AlertDialog.Builder(GameMultiPlayerFragment.this.getContext());
                        builder.setCancelable(false)
                                .setTitle("Game is over")
                                .setMessage(R.string.OpponentWonMessage)
                                .setIcon(R.drawable.lose)
                                .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        activity.startIntroFragment();
                                    }
                                }).show();
                        return;
                        //clearFirebaseRoom();
                    }
                    // Ok I'm ready to play
                }
            }
        });
    }

    /**
     * AUX
     */
    private String getMyRole() {
        return playerName.equals(roomName) ? "host" : "guest";
    }
    private boolean getMutex() {
        return playerName.equals(roomName) ? true : false;
    }
    private void setupPhotos(View view) {
        player1 = view.findViewById(R.id.photo_player1);
        player2 = view.findViewById(R.id.photo_player2);
        Drawable profilePic = new BitmapDrawable(getResources(), Utils.getBitmapFromBytes(
                activity.database.getProfile(activity.viewModel.getProfileId().getValue())
                        .getBitmap()));
        if (player1 != null && player2 == null) {
            player1.setBackground(profilePic);
            player2.setBackground(profilePic);
        }
        //player2.setImageResource(R.drawable.pc);
    }
    private void initializeGame(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            playerGameBoard = viewModel.getGameBoard().getValue();
            battleshipGame = new BatalhaNaval(playerGameBoard, MULTIPLAYER);
            battleshipGame.startGame(this);
        }
        else {
            battleshipGame = (BatalhaNaval) savedInstanceState.getSerializable(BATALHA_NAVAL);
        }
    }
    private void setOpponentBoard() {

        /* Update opponent's board (in the game object) */

        opponentGrid = view.findViewById(R.id.gridTabuleiroAdversario);
        opponentGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return battleshipGame.obtemTamanhoDosTabuleiros();
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
                ImageView iv;
                if (view == null) {
                    iv = new ImageView(GameMultiPlayerFragment.this.getContext());
                    if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                        // tablet or big phone
                        iv.setLayoutParams(new GridView.LayoutParams(61, 61));
                    } else {
                        // phone
                        iv.setLayoutParams(new GridView.LayoutParams(71, 71));
                    }
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setPadding(8,8,8,8);
                }
                else
                    iv = (ImageView) view;

                return battleshipGame.colocaImagemBaseadoNosNumerosMultiplayerFirebase(iv, i);
            }
        });
        opponentGrid.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!mutex) // Se nao for a minha vez de jogar -> ignorar
                    return;
                if (coordinates.size() >= JOGADAS) // ja esgotei as jogadas
                    return;

                // So' eu estou a usar o MUTEX a partir daqui:
                if (battleshipGame.jogaJogador(i, GameMultiPlayerFragment.this)) {
                    opponentGrid.invalidateViews();
                    playerGrid.invalidateViews();
                    coordinates.add(i); // se ataque valido -> guardar posicao de ataque
                }
                if (battleshipGame.jogoAcabou() == 2) {
                    coordinates.add(i);
                    gameOver = true;
                    //sendGameOverToFirebase(true);
                    saveHistory(battleshipGame, playerName);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameMultiPlayerFragment.this.getContext());
                    builder.setCancelable(false)
                            .setTitle("Game is over")
                            .setMessage(R.string.WinMessage)
                            .setIcon(R.drawable.trophy)
                            .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    activity.startIntroFragment();
                                }
                            }).show();
                    //clearFirebaseRoom();
                }
                else if (battleshipGame.jogoAcabou() == 1) {
                    coordinates.add(i);
                    gameOver = true;
                    //sendGameOverToFirebase(false);
                    saveHistory(battleshipGame, playerName2);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameMultiPlayerFragment.this.getContext());
                    builder.setCancelable(false)
                            .setTitle("Game is over")
                            .setMessage(R.string.OpponentWonMessage)
                            .setIcon(R.drawable.lose)
                            .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    activity.startIntroFragment();
                                }
                            }).show();
                    //clearFirebaseRoom();
                }
                if (coordinates.size() >= JOGADAS) sendAttackToFirebase(); // enviar ataque para o outro jogador
            }
        });
    }
    private void setPlayerBoard(View view) {
        playerGrid = view.findViewById(R.id.gridTabuleiroJogador);
        playerGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return battleshipGame.obtemTamanhoDosTabuleiros();
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
                ImageView iv;
                if (view == null) {
                    iv = new ImageView(GameMultiPlayerFragment.this.getContext());
                    if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                        // tablet or big phone
                        iv.setLayoutParams(new GridView.LayoutParams(61, 61));
                    } else {
                        // phone
                        iv.setLayoutParams(new GridView.LayoutParams(71, 71));
                    }
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setPadding(8,8,8,8);

                }
                else
                    iv = (ImageView) view;

                return battleshipGame.colocaImagemBaseadoNosNumerosMultiplayerPlayer(iv, i);
            }
        });
    }
    public void updateViews() {
        playerGrid.invalidateViews();
        opponentGrid.invalidateViews();
    }
    public void sendAttackToFirebase() {

        // get player's attack coordinates
        int firstAttack = coordinates.get(0);
        int secondAttack = coordinates.get(1);
        int thirdAttack = coordinates.get(2);

        // send attack coordinates to the other player (via Firebase)
        FirebaseMessage attackMessage = new FirebaseMessage(role, "ATTACK",
                firstAttack,
                secondAttack,
                thirdAttack);
        messageRef.setValue(attackMessage.toJson());

        // Clear old attack coordinates
        coordinates.clear();

        // Pass game's turn to the other player
        mutex = false;
    }
    public void sendGameOverToFirebase(boolean ganheiEu) {

        int pos1;

        if (ganheiEu) pos1 = -1; else pos1 = -2;

        // send attack coordinates to the other player (via Firebase)
        FirebaseMessage attackMessage = new FirebaseMessage(role, "GAMEOVER",
                pos1,
                0,
                0);
        messageRef.setValue(attackMessage.toJson());

        // Clear old attack coordinates
        //coordinates.clear();

        // Pass game's turn to the other player
        //mutex = false;
    }
    private void clearFirebaseRoom() {
        if (role.equals("host")) {
            FirebaseDatabase.getInstance().getReference()
                    .child("rooms").child(roomName).removeValue(); // APAGAR ROOM FIREBASE
        }
    }
    public synchronized boolean isGameReady() { return gameReady;}
    public synchronized boolean playing() {return mutex;}
    public synchronized boolean gameOver() {return gameOver;}

    /** Save game to History */
    private void saveHistory(final BatalhaNaval game, String winner) {
        String gameMode = MULTIPLAYER.toString();
        String date = new Date().toString();
        int shots = game.obtemNumeroTirosMultiplayer();
        int arr[] = game.barcosAtingidosAfundadosMultiplayer();
        int boatHits = arr[0];
        int boatSunken = arr[1];

        History history = new History(gameMode, date, winner, shots, boatHits, boatSunken);
        new TaskManager().addHistory(activity.database, null, history);
    }

    /**
     * TOOLBAR
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_single_player_menu, menu);

        /*Database db = activity.database;
        long id = activity.viewModel.getProfileId().getValue();
        Profile profile = db.getProfile(id);

        Drawable profilePic = new BitmapDrawable(getResources(), Utils.getBitmapFromBytes(profile.getBitmap()));
        menu.getItem(0).setIcon(profilePic);
        menu.getItem(1).setIcon(profilePic);*/

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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BATALHA_NAVAL, battleshipGame);
        super.onSaveInstanceState(outState);
    }

    public void otherPlayerNotResponding() {
        /*activity.runOnUiThread(new Runnable() {
            public void run() {
                activity.onBackPressed();
                Utils.createInfoDialog(activity, "Other player is not responding","120 seconds Timeout reached", R.drawable.warning);
            }
        });*/
    }
}
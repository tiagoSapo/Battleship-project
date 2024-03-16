package com.example.projetofinalcm2022.fragments.gamefragments;

import static com.example.projetofinalcm2022.models.BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_ARDUINO;
import static com.example.projetofinalcm2022.mqtt.MQTTHelper.ARDUINO_ASK_TOPIC;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.models.BatalhaNaval;
import com.example.projetofinalcm2022.models.History;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;
import java.util.Date;

public class GameArduinoFragment extends Fragment implements MqttCallbackExtended, Serializable {

    /** Activity **/
    private MainActivity activity;
    private String playerName;

    /** Views **/
    private ImageView player1, player2;
    public GridView playerGridView, arduinoGridView;
    private static final String BATALHA_NAVAL = "BATTLE_SHIP_GAME"; /** Key for Bundle (rotation) **/

    /** BattleShip Game **/
    private BatalhaNaval battleshipGame;
    private boolean gameOver = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_player, container, false);
        activity = (MainActivity) getActivity();
        setupPhotos(view);

        playerName = activity.viewModel.getProfileName().getValue();

        if (savedInstanceState != null) {
            battleshipGame = (BatalhaNaval) savedInstanceState.getSerializable(BATALHA_NAVAL);
        }
        else {
            int [][] gameBoard = activity.viewModel.getGameBoard().getValue();
            battleshipGame = new BatalhaNaval(gameBoard, SINGLE_PLAYER_ARDUINO);
            battleshipGame.startGame(this);
        }

        activity.setMqttCallback(this); // ----- CALLBACK ARDUINO ------

        final GridView player_grid = view.findViewById(R.id.gridTabuleiroJogador);
        player_grid.setAdapter(new BaseAdapter() {
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
                    iv = new ImageView(GameArduinoFragment.this.getContext());
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

                return battleshipGame.colocaImagemBaseadoNosNumeros(iv, i, BatalhaNaval.Jogadores.JOGADOR);
            }
        });

        final GridView opponent_grid = view.findViewById(R.id.gridTabuleiroAdversario);
        opponent_grid.setAdapter(new BaseAdapter() {
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
                    iv = new ImageView(GameArduinoFragment.this.getContext());
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

                return battleshipGame.colocaImagemBaseadoNosNumeros(iv, i, BatalhaNaval.Jogadores.ARDUINO);
            }
        });

        playerGridView = player_grid;
        arduinoGridView = opponent_grid;

        opponent_grid.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (battleshipGame.jogaJogador(i, GameArduinoFragment.this)) {
                    GameArduinoFragment.this.updateViews();
                }
                if (battleshipGame.jogoAcabou() == 2) {
                    saveGameToHistory(battleshipGame, playerName);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameArduinoFragment.this.getContext());
                    builder.setCancelable(false)
                            .setTitle("Game over")
                            .setMessage(R.string.WinMessage)
                            .setIcon(R.drawable.trophy)
                            .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    activity.startIntroFragment();
                                }
                            });
                    builder.show();
                }
                else if (battleshipGame.jogoAcabou() == 1) {
                    saveGameToHistory(battleshipGame, BatalhaNaval.Jogadores.ARDUINO.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameArduinoFragment.this.getContext());
                    builder.setCancelable(false)
                            .setTitle("Game over")
                            .setMessage(R.string.OpponentWonMessage)
                            .setIcon(R.drawable.lose)
                            .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    activity.startIntroFragment();
                                }
                            });
                    builder.show();
                }
            }
        });


        return view;
    }

    /**
     * MQTT
     */
    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        Log.w("Arduino_Tiago", "[Arduino] Received: ");

        try {
            /*if (modoDeJogo != BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_ARDUINO)
                return;*/

            String msg = message.toString();
            int position = Integer.valueOf(msg).intValue();

            boolean jogadaOk = battleshipGame.jogaArduino(position, this);
            if (jogadaOk) {
                BatalhaNaval.CoordXY coordinates = battleshipGame.converter1DPara2D(position);
                //Toast.makeText(activity, "[Arduino] Arduino chose coordinates =" + coordinates, Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException ex) {
            Log.w("Arduino_Tiago", "[Arduino] Unknown message, ignoring it");
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.w("Arduino_Tiago", "[Arduino] Ligacao estabelecida");
    }

    /**
     * AUX
     */
    private void setupPhotos(View view) {
        player1 = view.findViewById(R.id.photo_player1);
        player2 = view.findViewById(R.id.photo_player2);
        Drawable profilePic = new BitmapDrawable(getResources(), Utils.getBitmapFromBytes(
                activity.database.getProfile(activity.viewModel.getProfileId().getValue())
                        .getBitmap()));
        player1.setBackground(profilePic);
        player2.setImageResource(R.drawable.arduino);
    }
    public void updateViews() {
        arduinoGridView.invalidateViews();
        playerGridView.invalidateViews();
    }
    private void saveGameToHistory(final BatalhaNaval game, String winnerPlayer) {
        String gameMode = SINGLE_PLAYER_ARDUINO.toString();
        String date = new Date().toString();
        String winner = winnerPlayer;
        int shots = game.obtemNumeroTiros();
        int arr[] = game.barcosAtingidosAfundados();
        int boatHits = arr[0];
        int boatSunken = arr[1];

        History history = new History(gameMode, date, winner, shots, boatHits, boatSunken);
        new TaskManager().addHistory(activity.database, null, history);

        gameOver = true;
    }

    /**
     * TOOLBAR
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_single_player_menu, menu);

        /*Drawable profilePic = new BitmapDrawable(getResources(), Utils.getBitmapFromBytes(
                activity.database.getProfile(activity.viewModel.getProfileId().getValue())
                        .getBitmap()));
        menu.getItem(0).setIcon(profilePic);
        menu.getItem(1).setIcon(activity.getDrawable(R.drawable.arduino));*/

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

    /**
     * MQTT
     */
    public void askArduinoPositions() {

        /* Publish message to Arduino asking for it to play (choose random positions on board) */
        try {
            MqttMessage msg = Utils.convertStringToMQTT("askPositions");
            activity.mqtt.mqttAndroidClient.publish(ARDUINO_ASK_TOPIC, msg);

            Log.w("abc","Message sent to Arduino");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void otherPlayerNotResponding() {
        activity.onBackPressed();
    }

    public boolean gameOver() {
        return gameOver;
    }
}
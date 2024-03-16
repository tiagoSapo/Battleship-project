package com.example.projetofinalcm2022.fragments.gamefragments;

import static com.example.projetofinalcm2022.models.BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_ARDUINO;
import static com.example.projetofinalcm2022.models.BatalhaNaval.ModoDeJogo.SINGLE_PLAYER_CPU;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class GameSinglePlayerFragment extends Fragment implements Serializable {

    /** Activity **/
    private MainActivity activity;

    /** Views **/
    private ImageView player1, player2;
    private GridView gj, ga;
    private static final String BATALHA_NAVAL = "BATTLE_SHIP_GAME"; /** Key for Bundle (rotation) **/

    /** BattleShip Game **/
    private BatalhaNaval battleshipGame;


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

        if (savedInstanceState != null) {
            battleshipGame = (BatalhaNaval) savedInstanceState.getSerializable(BATALHA_NAVAL);
        }
        else {
            int [][] gameBoard = activity.viewModel.getGameBoard().getValue();
            battleshipGame = new BatalhaNaval(gameBoard, SINGLE_PLAYER_CPU);
            battleshipGame.startGame(this);
        }

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
                    iv = new ImageView(GameSinglePlayerFragment.this.getContext());
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
                    iv = new ImageView(GameSinglePlayerFragment.this.getContext());
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

                return battleshipGame.colocaImagemBaseadoNosNumeros(iv, i, BatalhaNaval.Jogadores.CPU);
            }
        });

        gj = player_grid;
        ga = opponent_grid;

        opponent_grid.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (battleshipGame.jogaJogador(i, GameSinglePlayerFragment.this)) {
                    opponent_grid.invalidateViews();
                    player_grid.invalidateViews();
                }
                if (battleshipGame.jogoAcabou() == 2) {
                    saveGameToHistory(battleshipGame, BatalhaNaval.Jogadores.JOGADOR);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameSinglePlayerFragment.this.getContext());
                    builder.setCancelable(false)
                            .setTitle("Game over")
                            .setIcon(R.drawable.trophy)
                            .setMessage(R.string.WinMessage)
                            .setNeutralButton(R.string.ExitMessage, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    activity.startIntroFragment();
                                }
                            });
                    builder.show();
                }
                else if (battleshipGame.jogoAcabou() == 1) {
                    saveGameToHistory(battleshipGame, BatalhaNaval.Jogadores.CPU);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameSinglePlayerFragment.this.getContext());
                    builder.setCancelable(false);
                    builder.setTitle("Game over")
                            .setIcon(R.drawable.lose)
                            .setMessage(R.string.OpponentWonMessage)
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
     * AUX
     */
    private void setupPhotos(View view) {
        player1 = view.findViewById(R.id.photo_player1);
        player2 = view.findViewById(R.id.photo_player2);

        long id = activity.viewModel.getProfileId().getValue();

        Drawable profilePic = new BitmapDrawable(getResources(), Utils.getBitmapFromBytes(
                activity.database.getProfile(id)
                        .getBitmap()));
        player1.setBackground(profilePic);
        player2.setImageResource(R.drawable.pc);
    }
    private void saveGameToHistory(final BatalhaNaval game, BatalhaNaval.Jogadores winnerPlayer) {
        String gameMode = SINGLE_PLAYER_CPU.toString();
        String date = new Date().toString();
        String winner = winnerPlayer.toString();
        int shots = game.obtemNumeroTiros();
        int arr[] = game.barcosAtingidosAfundados();
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

        /*Drawable profilePic = new BitmapDrawable(getResources(), Utils.getBitmapFromBytes(
                activity.database.getProfile(activity.viewModel.getProfileId().getValue())
                        .getBitmap()));
        menu.getItem(0).setIcon(profilePic);
        menu.getItem(1).setIcon(activity.getDrawable(R.drawable.pc));*/

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
}
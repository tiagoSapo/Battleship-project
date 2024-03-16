package com.example.projetofinalcm2022;

import static com.example.projetofinalcm2022.mqtt.MQTTHelper.ARDUINO_ASK_TOPIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;

import com.example.projetofinalcm2022.database.Database;
import com.example.projetofinalcm2022.fragments.EditProfileFragment;
import com.example.projetofinalcm2022.fragments.GameModeFragment;
import com.example.projetofinalcm2022.fragments.RoomFragment;
import com.example.projetofinalcm2022.fragments.gamefragments.GameArduinoFragment;
import com.example.projetofinalcm2022.fragments.gamefragments.GameMultiPlayerFragment;
import com.example.projetofinalcm2022.fragments.gamefragments.GameSinglePlayerFragment;
import com.example.projetofinalcm2022.fragments.HistoryFragment;
import com.example.projetofinalcm2022.fragments.IFragment;
import com.example.projetofinalcm2022.fragments.IntroFragment;
import com.example.projetofinalcm2022.fragments.LoginFragment;
import com.example.projetofinalcm2022.fragments.PiecesFragment;
import com.example.projetofinalcm2022.fragments.RegistrationFragment;
import com.example.projetofinalcm2022.models.MyViewModel;
import com.example.projetofinalcm2022.mqtt.MQTTHelper;
import com.example.projetofinalcm2022.utils.Utils;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements IFragment {

    /** Toolbar **/
    public Toolbar toolbar;

    /** ViewModel **/
    public MyViewModel viewModel = new MyViewModel();

    /** Database **/
    public Database database;

    /** Firebase **/
    public FirebaseDatabase firebaseDB;

    /** MQTT **/
    public MQTTHelper mqtt;

    /** Active fragment **/
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        database = new Database(this);
        startMQTT();
        firebaseDB = FirebaseDatabase.getInstance();

        viewModel.setProfileId(-1L);
        viewModel.setProfilePhoto(null);
        viewModel.setProfileName("");

        // Check if a fragment already exists
        if (savedInstanceState != null) {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentNumber");

            viewModel.setProfileId(savedInstanceState.getLong("viewModel-id"));
            viewModel.setProfileName(savedInstanceState.getString("viewModel-name"));
            viewModel.setProfilePhoto((byte[]) savedInstanceState.getSerializable("viewModel-photo"));

            viewModel.setRoomName(savedInstanceState.getString("viewModel-room"));
            viewModel.setGameMode(savedInstanceState.getString("viewModel-mode"));
            viewModel.setGameBoard((int[][]) savedInstanceState.getSerializable("viewModel-board"));


            //viewModel.setProfileId(savedInstanceState.getLong("viewModel"));
            //viewModel = (MyViewModel) savedInstanceState.getSerializable("viewModel");
            //startFragment(activeFragment);
        }
        else {
            startIntroFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeFragment.isAdded()) {
            outState.putLong("viewModel-id", viewModel.getProfileId().getValue());
            outState.putString("viewModel-name", viewModel.getProfileName().getValue());
            outState.putSerializable("viewModel-photo", viewModel.getProfilePhoto().getValue());

            outState.putString("viewModel-room", viewModel.getRoomName().getValue());
            outState.putString("viewModel-mode", viewModel.getGameMode().getValue());
            outState.putSerializable("viewModel-board", viewModel.getGameBoard().getValue());

            getSupportFragmentManager().putFragment(outState, "fragmentNumber", activeFragment);
        }
    }

    @Override
    protected void onPause() {
        database.close();
        database = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (database == null) {
            database = new Database(this);
        }
        super.onResume();
    }

    /**
     *  MQTT
     **/
    private void startMQTT() {
        mqtt = new MQTTHelper(this, "tiago");
        mqtt.connect();
    }
    public void setMqttCallback(MqttCallbackExtended callback) {
        mqtt.setCallback(callback);
    }

    /**
     *   FRAGMENTS
     **/
    @Override
    public void startFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frameLayout, activeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void startIntroFragment() {
        activeFragment = new IntroFragment();
        startFragment(activeFragment);
    }
    @Override
    public void startGameMode() {
        activeFragment = new GameModeFragment();
        startFragment(activeFragment);
    }
    @Override
    public void startRegistrationFragment() {
        activeFragment = new RegistrationFragment();
        startFragment(activeFragment);
    }
    public void startLoginFragment() {
        activeFragment = new LoginFragment();
        startFragment(activeFragment);
    }
    @Override
    public void startHistoryFragment() {
        activeFragment = new HistoryFragment();
        startFragment(activeFragment);
    }
    @Override
    public void startPiecesFragment() {
        activeFragment = new PiecesFragment();
        startFragment(activeFragment);
    }
    @Override
    public void startEditProfileFragment() {
        activeFragment = new EditProfileFragment();
        startFragment(activeFragment);
    }
    @Override
    public void startRoomFragment() {
        activeFragment = new RoomFragment();
        startFragment(activeFragment);
    }


    @Override
    public void startSinglePlayerGameFragment() {
        activeFragment = new GameSinglePlayerFragment();
        startFragment(activeFragment);
    }

    @Override
    public void startMultiplayerGameFragment() {
        activeFragment = new GameMultiPlayerFragment();
        startFragment(activeFragment);
    }

    @Override
    public void startArduinoGameFragment() {
        activeFragment = new GameArduinoFragment();
        startFragment(activeFragment);
    }

    @Override
    public void onBackPressed() {
        if (activeFragment == null) return;
        else if (activeFragment instanceof IntroFragment) finish();
        else if (activeFragment instanceof GameModeFragment) startIntroFragment();
        else if (activeFragment instanceof RegistrationFragment) startLoginFragment();
        else if (activeFragment instanceof LoginFragment) startIntroFragment();
        else if (activeFragment instanceof EditProfileFragment) startLoginFragment();
        else if (activeFragment instanceof PiecesFragment) startLoginFragment();
        else if (activeFragment instanceof HistoryFragment) startIntroFragment();
        else if (activeFragment instanceof RoomFragment) startIntroFragment();
        else if (activeFragment instanceof GameSinglePlayerFragment) startIntroFragment();
        else if (activeFragment instanceof GameMultiPlayerFragment) startIntroFragment();
        else if (activeFragment instanceof GameArduinoFragment) startIntroFragment();
        return;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
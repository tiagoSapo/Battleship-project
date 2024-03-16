package com.example.projetofinalcm2022.fragments;

import androidx.fragment.app.Fragment;

public interface IFragment {
    void startFragment(Fragment fragment);
    void startIntroFragment();
    void startGameMode();
    void startRegistrationFragment();
    void startLoginFragment();
    void startHistoryFragment();
    void startPiecesFragment();
    void startEditProfileFragment();
    void startRoomFragment();

    void startSinglePlayerGameFragment();
    void startMultiplayerGameFragment();
    void startArduinoGameFragment();
}

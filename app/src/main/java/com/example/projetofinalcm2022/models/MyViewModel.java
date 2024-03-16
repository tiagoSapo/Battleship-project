package com.example.projetofinalcm2022.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;

public class MyViewModel extends ViewModel {

    /** User Profile **/
    private MutableLiveData<Long> profileId = new MutableLiveData<>();
    private MutableLiveData<String> profileName = new MutableLiveData<>();
    private MutableLiveData<byte[]> profilePhoto = new MutableLiveData<>();

    /** Room name **/
    private MutableLiveData<String> roomName = new MutableLiveData<>();

    /** Game mode selected and User's gameboard **/
    private MutableLiveData<String> gameMode = new MutableLiveData<>();
    private MutableLiveData<int[][]> gameBoard = new MutableLiveData<>();

    public MyViewModel() {}

    public MutableLiveData<Long> getProfileId() {
        return profileId;
    }
    public void setProfileId(Long profileId) {
        this.profileId.setValue(profileId);
    }

    public MutableLiveData<int[][]> getGameBoard() {
        return gameBoard;
    }
    public void setGameBoard(int[][] gameBoard) {
        this.gameBoard.setValue(gameBoard);
    }

    public MutableLiveData<String> getGameMode() {
        return gameMode;
    }
    public void setGameMode(String gameMode) {
        this.gameMode.setValue(gameMode);
    }

    public MutableLiveData<String> getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName.setValue(profileName);
    }

    public MutableLiveData<String> getRoomName() {
        return roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName.setValue(roomName);
    }

    public MutableLiveData<byte[]> getProfilePhoto() {
        return profilePhoto;
    }
    public void setProfilePhoto(byte[] photo) {
        this.profilePhoto.setValue(photo);
    }

}

package com.example.projetofinalcm2022.threads;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.database.Database;
import com.example.projetofinalcm2022.fragments.gamefragments.GameArduinoFragment;
import com.example.projetofinalcm2022.fragments.gamefragments.GameMultiPlayerFragment;
import com.example.projetofinalcm2022.models.BatalhaNaval;
import com.example.projetofinalcm2022.models.History;
import com.example.projetofinalcm2022.models.Profile;
import com.example.projetofinalcm2022.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskManager {

    final Executor executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());

    public void asyncAskArduino(GameArduinoFragment fragment, BatalhaNaval game) {

        if (fragment.gameOver())
            return;

        ProgressDialog pd = Utils.createProgressDialog(fragment.getActivity(), "Arduino's turn", "Please wait 15 seconds for Arduino decision...");
        pd.show();
        executor.execute(() -> {
            try {
                /* Ask Arduino (15 times) for positions */
                for(int i = 0; i < 15; i++) {
                    if (fragment.gameOver())
                        break;
                    fragment.askArduinoPositions();
                    if (game.arduinoFinished())
                        break;
                    Thread.sleep(1000);
                }
                if (!fragment.gameOver() && !game.arduinoFinished()) {
                    Log.e("abc", "ARDUINO NAO ESTA A RESPONDER!!!");
                    fragment.otherPlayerNotResponding();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                pd.dismiss();
            });

        });
    }

    public void asyncWaitMultiplayerPreparation(GameMultiPlayerFragment fragment) {
        String title = "Waiting for the other Player";
        String msg = "Please wait for 120 seconds for the other Player to ENTER";
        ProgressDialog pd = Utils.createProgressDialog(fragment.activity, title, msg);
        pd.show();
        executor.execute(() -> {
            try {
                /* Ask Arduino (120 times) for positions */
                for(int i = 0; i < 120; i++) {
                    if (fragment.isGameReady())
                        break;
                    Thread.sleep(1000);
                }
                if (!fragment.isGameReady()) {
                    Log.w("firebase", "OTHER PLAYER IS NOT RESPONDING ENTER!!!");
                    fragment.otherPlayerNotResponding();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                pd.dismiss();
            });

        });
    }
    public void asyncWaitMultiplayerGuestFirst(GameMultiPlayerFragment fragment) {
        String title = "Waiting for the other Player";
        String msg = "Please wait for 120 seconds for the other Player to PLAY";
        ProgressDialog pd = Utils.createProgressDialog(fragment.activity, title, msg);
        pd.show();
        executor.execute(() -> {
            try {
                /* Ask Arduino (120 times) for positions */
                for(int i = 0; i < 120; i++) {
                    if (fragment.playing())
                        break;
                    Thread.sleep(1000);
                }
                if (!fragment.playing()) {
                    Log.w("firebase", "OTHER PLAYER IS NOT RESPONDING!!!");
                    fragment.otherPlayerNotResponding();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                pd.dismiss();
            });

        });
    }
    public void asyncWaitMultiplayer(GameMultiPlayerFragment fragment, BatalhaNaval game) {

        if (fragment.gameOver())
            return;

        String title = "Waiting for the other Player";
        String msg = "Please wait for 120 seconds for the other Player to PLAY";
        ProgressDialog pd = Utils.createProgressDialog(fragment.activity, title, msg);
        pd.show();
        executor.execute(() -> {
            try {
                /* Ask Arduino (120 times) for positions */
                for(int i = 0; i < 120; i++) {
                    if (fragment.gameOver())
                        break;
                    if (game.firebaseFinished() && fragment.isGameReady())
                        break;
                    Thread.sleep(1000);
                }
                if (!fragment.gameOver() && !game.firebaseFinished()) {
                    Log.w("firebase", "OTHER PLAYER IS NOT RESPONDING PLAY!!!");
                    fragment.otherPlayerNotResponding();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                pd.dismiss();
            });

        });
    }

    public interface CallbackGetAll {
        void onCompleteGetAll(List<Profile> profiles);
    }
    public interface CallbackAdd {
        void onCompleteAdd(Profile profile, String invalidName);
    }
    public interface CallBackDelete {
        void onCompleteDelete(boolean deleteOk);
    }
    public interface CallbackUpdate {
        void onCompleteUpdate(boolean updateOk, String name, String password, byte[] photo);
    }
    public interface CallbackGet {
        void onComplete(Profile profile);
    }

    public interface CallbackGetAllHistory {
        void onCompleteGetAll(List<History> histories);
    }
    public interface CallbackAddHistory {
        void onCompleteAdd();
    }
    public interface CallBackDeleteHistory {
        void onCompleteDelete();
    }
    public interface CallbackGetHistory {
        void onComplete(History history);
    }

    public void getProfiles(Database database, CallbackGetAll callback) {
        executor.execute(() -> {
            List<Profile> profiles = database.getProfiles();
            /*Map<String, Profile> map =
                    profiles.stream().collect(Collectors.toMap(Profile::getName, prof -> prof));*/
            handler.post(() -> {
                callback.onCompleteGetAll(profiles);
            });
        });
    }
    public void getProfile(Database database, CallbackGet callback, long profileId) {
        executor.execute(() -> {
            Profile profile = database.getProfile(profileId);
            handler.post(() -> {
                callback.onComplete(profile);
            });
        });
    }
    public void addProfile(Database database, CallbackAdd callback, Profile newProfile) {
        executor.execute(() -> {
            Profile profile = database.addProfile(newProfile);
            handler.post(() -> {
                if (callback != null)
                    callback.onCompleteAdd(profile, newProfile.getName());
            });
        });
    }
    public void deleteProfile(Database database, CallBackDelete callback, long profileId) {
        executor.execute(() -> {
            boolean result = database.deleteProfile(profileId);
            handler.post(() -> {
                callback.onCompleteDelete(result);
            });
        });
    }
    public void updateProfile(Database database, CallbackUpdate callback, long profileId,
                              String newName, String newPassword, byte[] newPhoto) {
        executor.execute(() -> {
            boolean result = database.updateProfile(profileId, newName, newPassword, newPhoto);
            handler.post(() -> {
                callback.onCompleteUpdate(result, newName, newPassword, newPhoto);
            });
        });
    }

    public void getHistories(Database database, CallbackGetAllHistory callback) {
        executor.execute(() -> {
            List<History> profiles = database.getAllHistory();
            handler.post(() -> {
                callback.onCompleteGetAll(profiles);
            });
        });
    }
    public void getHistory(Database database, CallbackGetHistory callback, long id) {
        executor.execute(() -> {
            History history = database.getHistory(id);
            handler.post(() -> {
                callback.onComplete(history);
            });
        });
    }
    public void addHistory(Database database, CallbackAddHistory callback, History historyNew) {
        executor.execute(() -> {
            database.addHistory(historyNew);
            handler.post(() -> {
                if (callback != null)
                    callback.onCompleteAdd();
            });
        });
    }
    public void deleteProfile(Database database, CallBackDeleteHistory callback, long id) {
        executor.execute(() -> {
            boolean result = database.deleteHistory(id);
            handler.post(() -> {
                callback.onCompleteDelete();
            });
        });
    }

}

package com.example.projetofinalcm2022.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.projetofinalcm2022.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class Utils {

    /** Keys for bundle **/
    public static final String BUTTON1_COUNTER = "BUTTON1_COUNTER";
    public static final String BUTTON2_COUNTER = "BUTTON2_COUNTER";
    public static final String BUTTON3_COUNTER = "BUTTON3_COUNTER";
    public static final String BUTTON5_COUNTER = "BUTTON5_COUNTER";
    public static final String BOARD_TEMP = "BOARD_TEMP";
    public static final String SW_POSITION = "SW_POSITION";
    public static final String SW_BOAT_5 = "SW_BOAT_5";
    public static final String TV_SELECTED_BOAT = "TV_SELECTED_BOAT";

    public static final int BOARD_TAM = 64;
    public static final int BOARD_COLUMNS = 8;

    public static AlertDialog createInfoDialog(Context context, String title, String message, Integer icon) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (icon != null) {
            alertDialog.setIcon(icon);
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertDialog;
    }
    public static AlertDialog createInfoDialogNonCancelable(Context context, String title, String message, Integer icon) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (icon != null) {
            alertDialog.setIcon(icon);
        }
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertDialog;
    }
    public static AlertDialog.Builder createQuestionDialog(Context context, String title, String message, String yesMsg, String noMsg, Integer icon, DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title).setMessage(message)
                .setPositiveButton(yesMsg, dialogClickListener)
                .setNegativeButton(noMsg, dialogClickListener);
        if (icon != null)
            builder.setIcon(icon);
        return builder;
    }
    public static ProgressDialog createProgressDialog(Context context, String title, String msg) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle(title);
        pd.setMessage(msg);
        pd.setIcon(R.drawable.clock);
        pd.setCancelable(false);
        return pd;
    }

    public static boolean askForPermissionCamera(Activity activity, Fragment fragment) {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, 0);
        if (ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.w("abc", "Permission to use the camera was DENIED");
            return false;
        }
        Log.i("abc", "Permission to use the camera was GRANTED");
        return true;
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    // convert from byte array to bitmap
    public static Bitmap getBitmapFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public static MqttMessage convertStringToMQTT(String str) throws UnsupportedEncodingException {

        byte[] encodedPayload;

        encodedPayload = str.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setQos(0);
        return message;
    }
}

package com.example.projetofinalcm2022.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.models.Profile;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;

import java.util.List;
import java.util.Map;

public class RegistrationFragment extends Fragment implements TaskManager.CallbackAdd {

    private MainActivity activity;

    /** Views **/
    private ImageView photoImageView;
    private Button takePhotoButton, clearPhotoButton;
    private EditText nameEditText, passwordEditText;

    /** Profile photo taken by the camera **/
    private Bitmap photoBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        activity = (MainActivity)getActivity();
        photoBitmap = null;

        nameEditText = view.findViewById(R.id.profile_name);
        passwordEditText = view.findViewById(R.id.profile_password);
        photoImageView = view.findViewById(R.id.profile_photo);
        clearPhotoButton = view.findViewById(R.id.clear_photo);
        clearPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoImageView.setImageResource(R.drawable.blank_profile);
                photoBitmap = null;
            }
        });
        takePhotoButton = view.findViewById(R.id.take_photo);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            ActivityResultLauncher<Intent> activityResultPhoto = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                /** Replace imageView with photo taken by the user **/
                                Intent data = result.getData();
                                photoBitmap = (Bitmap) data.getExtras().get("data");
                                photoImageView.setImageBitmap(photoBitmap);
                            }
                        }
                    }
            );
            @Override
            public void onClick(View view) {
                /** Ask for permission to use the camera **/
                boolean permission = Utils.askForPermissionCamera(activity,RegistrationFragment.this);
                if (permission == false)
                    return;

                /** Take a photo **/
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultPhoto.launch(intent);
            }
        });
        return view;
    }

    /**
     *   TOOLBAR
     **/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_registration_menu, menu);

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
            case R.id.save_profile:
                saveProfile();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * LOGIC METHODS
     */
    private void saveProfile() {
        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        StringBuilder errors = new StringBuilder();
        if (photoBitmap == null) {
            photoBitmap = ((BitmapDrawable)photoImageView.getDrawable()).getBitmap();
        }
        if (name.isEmpty()) {
            errors.append("name, ");
        }
        if (password.isEmpty()) {
            errors.append("password, ");
        }
        if (errors.length() != 0) {
            errors.append("is/are invalid. Please fill these parameters");
            Log.w("abc", errors.toString());
            Utils.createInfoDialog(activity, "Invalid parameters", errors.toString(), R.drawable.warning).show();
            return;
        }
        if (password.length() < 4) {
            Utils.createInfoDialog(activity, "Short password", "Password is too short. Please choose another one", R.drawable.warning).show();
            return;
        }

        Profile profile = new Profile();
        profile.setName(name);
        profile.setPassword(password);
        profile.setBitmap(Utils.getBytesFromBitmap(photoBitmap));

        TaskManager taskManager = new TaskManager();
        taskManager.addProfile(activity.database, this, profile);
    }

    @Override
    public void onCompleteAdd(Profile profile, String invalidName) {
        if (profile == null) {
            String error = "A profile with name \"" + invalidName + "\" already exists. Please chose another name.";
            Log.w("abc", error);
            Utils.createInfoDialog(activity, "Invalid profile name", error, R.drawable.warning).show();
            return;
        }
        Log.w("abc", "New profile \"" + profile.getName() + "\" created.");
        activity.startLoginFragment();
    }

}
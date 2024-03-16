package com.example.projetofinalcm2022.fragments;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.models.Profile;
import com.example.projetofinalcm2022.adapters.ProfileAdapter;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginFragment extends Fragment implements TaskManager.CallbackGetAll, TaskManager.CallBackDelete {

    private MainActivity activity;

    private ProfileAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyLayout, recyclerViewLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        activity = (MainActivity)getActivity();
        adapter = new ProfileAdapter(activity, this, new ArrayList<>());
        recyclerView = (RecyclerView) view.findViewById(R.id.list_of_profiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);

        /** Get all saved notes **/
        TaskManager taskManager = new TaskManager();
        taskManager.getProfiles(activity.database,this);

        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_view);
        recyclerViewLayout = (LinearLayout) view.findViewById(R.id.recycle_view_layout);

        return view;
    }

    public void checkIfEmpty(int numberOfProfiles) {
        if (numberOfProfiles <= 0){
            recyclerViewLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    /**
     *   TOOLBAR
     **/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_login_menu, menu);

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
            case R.id.create_profile:
                activity.startRegistrationFragment();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callbacks
     */
    public void callbackOnClickItemAdapter(Profile profile, int position) {
        String title = "Play as \"" + profile.getName() + "\"";
        String msg = "Play as \"" + profile.getName() + "\"?";
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        activity.viewModel.setProfileId(profile.getId());
                        activity.viewModel.setProfileName(profile.getName());
                        activity.viewModel.setProfilePhoto(profile.getBitmap());
                        activity.startPiecesFragment();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        Utils.createQuestionDialog(activity, title, msg, "Yes", "No", R.drawable.question, dialogClickListener).show();
    }
    public void callbackOnLongClickAdapterEditProfile(Profile profile) {
        editProfile(profile);
    }

    public void editProfile(Profile profile) {
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Please insert " + profile.getName() + "'s password: " )
                .setView(input)
                .setIcon(R.drawable.password)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String insertedPassword = input.getText().toString();
                        if (!insertedPassword.equals(profile.getPassword())) {
                            Utils.createInfoDialog(activity,"Invalid password",
                                    "Inserted password is not correct. Permission denied",
                                    R.drawable.access_denied).show();
                            return;
                        }
                        activity.viewModel.setProfileId(profile.getId());
                        activity.viewModel.setProfileName(profile.getName());
                        activity.viewModel.setProfilePhoto(profile.getBitmap());
                        activity.startEditProfileFragment();
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onCompleteGetAll(List<Profile> profiles) {
        adapter.registerNotes(profiles);
        adapter.notifyDataSetChanged();
        checkIfEmpty(profiles.size());
        Log.i("abc", "LoginFragment is fetching all profiles from DB " + profiles.toString());
    }
    @Override
    public void onCompleteDelete(boolean deleteOk) {
        Log.i("abc", "LoginFragment is updating recycleView");
    }
}
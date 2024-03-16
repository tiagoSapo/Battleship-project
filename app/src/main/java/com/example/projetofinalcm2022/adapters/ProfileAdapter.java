package com.example.projetofinalcm2022.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.database.Database;
import com.example.projetofinalcm2022.fragments.LoginFragment;
import com.example.projetofinalcm2022.models.Profile;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> implements TaskManager.CallBackDelete {

    private MainActivity activity;
    private Database db;
    private LoginFragment fragment;

    private LayoutInflater inflater;
    public List<Profile> profiles;
    public List<Profile> profilesOriginal;

    public ProfileAdapter(Context context, LoginFragment fragment, List<Profile> profiles) {
        this.inflater = LayoutInflater.from(context);
        this.profiles = profiles;
        this.profilesOriginal = profiles;
        this.activity = (MainActivity) context;
        this.db = activity.database;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custome_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int positionNotUpdated) {
        int position = holder.getAdapterPosition();
        long id = profiles.get(position).getId();
        String name = profiles.get(position).getName();
        String password = profiles.get(position).getPassword();
        byte[] photo = profiles.get(position).getBitmap();

        holder.id.setText(String.valueOf(id));
        holder.name.setText(name);
        holder.password.setText(password);
        holder.photo.setImageBitmap(Utils.getBitmapFromBytes(photo));

        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                fragment.callbackOnClickItemAdapter(profiles.get(pos), pos);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos = holder.getAdapterPosition();
                deleteOrEditProfile(profiles.get(pos), pos);
                return true; // if false activates onClickListener
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void registerNotes(List<Profile> profiles) {
        this.profiles = profiles;
        this.profilesOriginal = profiles;
    }

    public void deleteProfile(Profile profile, int holderPosition) {
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
                        TaskManager taskManager = new TaskManager();
                        taskManager.deleteProfile(db, ProfileAdapter.this, profile.getId());
                        profiles.remove(holderPosition);
                        ProfileAdapter.this.notifyItemRemoved(holderPosition);
                        fragment.checkIfEmpty(profiles.size());
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

    public void deleteOrEditProfile(Profile profile, int holderPosition) {
        String title = "Delete profile";
        String msg = "Do you want to DELETE or EDIT profile \"" + profile.getName() + "\"?";
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteProfile(profile, holderPosition);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        fragment.callbackOnLongClickAdapterEditProfile(profile);
                        break;
                }
            }
        };
        Utils.createQuestionDialog(activity, title, msg, "DELETE", "EDIT", R.drawable.question, dialogClickListener).show();
    }

    @Override
    public void onCompleteDelete(boolean deleteOk) {
        Log.i("abc","[ProfileAdapter] Profile delete from db");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, password;
        ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.profile_id_adapter);
            name = (TextView) itemView.findViewById(R.id.profile_name_adapter);
            password = (TextView) itemView.findViewById(R.id.profile_password_adapter);
            photo = (ImageView) itemView.findViewById(R.id.profile_photo_adapter);
        }
    }
}

package com.example.ticketplease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    Button resetPassword;
    private final static String TAG = "SettingsActivity";
    private FirebaseAuth firebaseAuth;
    SwitchCompat notificationsSwitch;
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){
                //Do anything here which needs to be done after signout is complete
                Intent logoutIntent = new Intent(SettingsActivity.this,LoginActivity.class);
                logoutIntent.putExtra("logoutCode", 2137);
                startActivity(logoutIntent);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        notificationsSwitch = findViewById(R.id.Notifications);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

        resetPassword = findViewById(R.id.ResetButton);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(getApplicationContext(), getString(R.string.passwdReset), Toast.LENGTH_LONG).show();
                                    firebaseAuth.signOut();
                                }
                            }
                        });
            }
        });

        setSwitchButton();
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("NotificationPermission", notificationsSwitch.isChecked());
                editor.apply();
            }
        });
    }

    public void CloseDiscount(View view) {
        finish();
    }

    private void setSwitchButton() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationsSwitch.setChecked(preferences.getBoolean("NotificationPermission", true));
    }
}
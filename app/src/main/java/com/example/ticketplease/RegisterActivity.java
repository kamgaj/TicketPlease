package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import io.opencensus.metrics.LongGauge;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Registration";
    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        Button registerButton = findViewById(R.id.loginBar2);
        EditText loginEdit = findViewById(R.id.login);
        EditText emailEdit = findViewById(R.id.email);
        EditText passwordEdit = findViewById(R.id.password);

        mFirebaseAuth = FirebaseAuth.getInstance(); //opening connection with firebase authentication module
        FirebaseFirestore fStore = FirebaseFirestore.getInstance(); // opening connection with firebase


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User credentials
                final String email = emailEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                final  String username = loginEdit.getText().toString();


         //Checking if user credentials arent empty and requesting users to insert missing data
        if(username.isEmpty()) {
            loginEdit.setError(getString(R.string.EmptyLogin));
            loginEdit.requestFocus();
        }
        else if(email.isEmpty()) {
            emailEdit.setError(getString(R.string.EmptyEmail));
            emailEdit.requestFocus();
        }
        else if(password.isEmpty()) {
            passwordEdit.setError(getString(R.string.EmptyPassword));
            passwordEdit.requestFocus();
        }
        //If everything is ok we are creating new user account
        else {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                //if tasks fails we get entry in a Log
                if(!task.isSuccessful())
                {
                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
                    Log.e(TAG, "Failed Registration", e);
                    Toast.makeText(RegisterActivity.this, getString(R.string.RegistrationFailToast), Toast.LENGTH_LONG).show();
                }
                //if task is successful we store current user and go to MainActivity or if not we are making a log entry
                else
                {
                    FirebaseUser fUser = mFirebaseAuth.getCurrentUser();
                    Objects.requireNonNull(fUser).sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, getString(R.string.VerificationEmailSent), Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, getString(R.string.onFailureEmail) + e.getMessage());
                        }
                    });
                }
            });
        }
            }
        });

    }


    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}

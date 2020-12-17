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
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class RegisterActivity extends AppCompatActivity {
    //email validation method
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

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
        else {
            if(username.contains(" ")){
                Toast.makeText(getApplicationContext(), "Nazwa użytkownika nie może zawierać spacji", Toast.LENGTH_LONG).show();
            }
            else if(password.contains(" ")){
                Toast.makeText(getApplicationContext(), "Hasło nie może zawierać spacji", Toast.LENGTH_LONG).show();
            }
            else if(email.contains(" ")){
                Toast.makeText(getApplicationContext(), "Email nie może zawierać spacji", Toast.LENGTH_LONG).show();
            }
            else if(username.length()<5){
                Toast.makeText(getApplicationContext(), "Nazwa użytkownika musi zawierać minimum 5 znaków", Toast.LENGTH_LONG).show();
            }
            else if(password.length()<8){
                Toast.makeText(getApplicationContext(), "Hasło musi zawierać minimum 8 znaków", Toast.LENGTH_LONG).show();
            }
            else if(!username.matches("[a-zA-Z0-9]*")){
                Toast.makeText(getApplicationContext(), "Nazwa użytkownika zawiera znaki specjalne", Toast.LENGTH_LONG).show();
            }
            else if (!isEmailValid(email)){
                Toast.makeText(getApplicationContext(), "Błędny adres email", Toast.LENGTH_LONG).show();
            }
            //If everything is ok we are creating new user account
            else {
                mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                    //if tasks fails we get entry in a Log
                    if (!task.isSuccessful()) {
                        FirebaseAuthException e = (FirebaseAuthException) task.getException();
                        Log.e(TAG, "Failed Registration", e);
                        Toast.makeText(RegisterActivity.this, getString(R.string.RegistrationFailToast), Toast.LENGTH_LONG).show();
                    }
                    //if task is successful we store current user and go to LoginActivity or if not we are making a log entry
                    else {
                        FirebaseUser fUser = mFirebaseAuth.getCurrentUser();
                        Objects.requireNonNull(fUser).sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.VerificationEmailSent), Toast.LENGTH_LONG).show();
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                Intent goToHome = new Intent(RegisterActivity.this, HomeActivity.class);
                                goToHome.putExtra("disableBackButton", 7312);
                                startActivity(goToHome);
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
            }
        });

    }


    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}

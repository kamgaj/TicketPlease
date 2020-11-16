package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText loginEdit, passwordEdit;
    FirebaseAuth mFirebaseAuth;
    Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mFirebaseAuth = FirebaseAuth.getInstance();
        loginEdit = findViewById(R.id.Login);
        passwordEdit = findViewById(R.id.Password);
        loginButton = findViewById(R.id.loginBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storing the user credentials
                final String email = loginEdit.getText().toString();
                final String password = passwordEdit.getText().toString();

                //signing the user
                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if everything is ok we go to the MainActivity
                        if(task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        //if not then we got an Error Toast
                        else {
                            Toast.makeText(getApplicationContext(), getString(R.string.loginError), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }


    public void goToRegistration(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}

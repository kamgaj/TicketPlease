package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;

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
                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Email i hasło nie mogą być puste", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email nie może być pusty", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Hasło nie może być puste", Toast.LENGTH_LONG).show();
                }
                else {
                    //signing the user
                    {
                        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            //@Override
                            public void onComplete (@NonNull Task < AuthResult > task) {
                                mFirebaseAuth.getCurrentUser().reload();
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                //checking if email was verified
                                if(user.isEmailVerified()) {
                                    //if everything is ok we go to the MainActivity
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                       // startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                    //if not then we got an Error Toast
                                    else {
                                        Toast.makeText(getApplicationContext(), "Email lub hasło są niepoprawne", Toast.LENGTH_LONG).show();
                                    }
                                }
                                //if email was not verified
                                else{
                                    Toast.makeText(getApplicationContext(), "Email nie został zweryfikowany", Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    }
                }
            }
        });
    }


    public void goToRegistration(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}

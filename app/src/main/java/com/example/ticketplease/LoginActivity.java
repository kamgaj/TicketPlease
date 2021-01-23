package com.example.ticketplease;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    EditText loginEdit, passwordEdit;
    FirebaseAuth mFirebaseAuth;
    Button loginButton;
    TextView forgetPass;
    Dialog Forget;
    ImageView close;
    Chip confirm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mFirebaseAuth = FirebaseAuth.getInstance();
        loginEdit = findViewById(R.id.Login);
        passwordEdit = findViewById(R.id.Password);
        loginButton = findViewById(R.id.loginBar);
        if(mFirebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        TextView show;
        show=findViewById(R.id.ShowPassword);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordEdit.getTransformationMethod()==null){
                    int start =passwordEdit.getSelectionStart();
                    int end =passwordEdit.getSelectionEnd();
                    passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEdit.setSelection(start,end);
                    show.setText("Pokaż");
                }else{
                    int start =passwordEdit.getSelectionStart();
                    int end =passwordEdit.getSelectionEnd();
                    passwordEdit.setTransformationMethod(null);
                    passwordEdit.setSelection(start,end);
                    show.setText("Ukryj");
                }
            }
        });
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
                            public void onComplete (@NonNull Task < AuthResult > task) {
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                //checking if email was verified
                                if(user!=null){
                                    if(user.isEmailVerified()) {
                                        //if everything is ok we go to the MainActivity
                                        if (task.isSuccessful()) {
                                            Intent goToHome = new Intent(LoginActivity.this, HomeActivity.class);
                                            goToHome.putExtra("disableBackButton", 7312);
                                            startActivity(goToHome);
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
                                }else {
                                    Toast.makeText(getApplicationContext(), "Email lub hasło są niepoprawne", Toast.LENGTH_LONG).show();
                                }

                            }

                        });
                    }
                }
            }
        });
        Forget=new Dialog(LoginActivity.this);
        Forget.setContentView(R.layout.forgot_password_popup);
        Forget.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        Forget.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        forgetPass=findViewById(R.id.ForgetPassword);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Forget.show();
            }
        });
        close=(ImageView)Forget.findViewById(R.id.CloseButtonForget);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Forget.dismiss();
            }
        });
        confirm=Forget.findViewById(R.id.ResetPasswordConfirmButton);
        EditText resetEmail = (EditText) Forget.findViewById(R.id.recoverPasswordEmail);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = resetEmail.getText().toString();

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.passwdReset), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                Forget.dismiss();
            }
        });
    }

    public void goToRegistration(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {

        int requestCode = getIntent().getIntExtra("logoutCode", 1234);
        Log.d("RESPONSE: ", String.valueOf(requestCode));
        if(requestCode == 2137) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }else {
            super.onBackPressed();
        }
    }
}

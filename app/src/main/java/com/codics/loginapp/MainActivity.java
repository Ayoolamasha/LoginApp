package com.codics.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText loginEmail, loginPassword;
    private TextView newUserSignUp, warningText;
    private Button login;
    private int counter = 5;

    // FIREBASE SETUP
    private FirebaseAuth firebaseAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUi();


        newUserSignUp.setOnClickListener(this);
        login.setOnClickListener(this);

        // TO CHECK IF THE USER IS NOT NULL MEANING IF THERE IS NO USER
        // IF THERE IS ONE LOG THE USER IN AUTOMATICALLY
        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }





    }


    private void setupUi() {
        loginEmail = findViewById(R.id.login_userName);
        loginPassword = findViewById(R.id.login_password);
        newUserSignUp = findViewById(R.id.newUserSignUp);
        warningText = findViewById(R.id.login_warning);
        login = findViewById(R.id.login_Button);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void validate(String userEmail, String userPassword){
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), "Login Not Successful", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.newUserSignUp:
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                break;

            case R.id.login_Button:
                validate(loginEmail.getText().toString(), loginPassword.getText().toString());
        }

    }
}

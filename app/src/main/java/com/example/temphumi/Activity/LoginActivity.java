package com.example.temphumi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.temphumi.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void Login(View view) {
        //TODO: sign in the user
    }

    public void ForgotPassword(View view) {
        //TODO: reset password
    }

    public void GoToSignupActivity(View view) {
        Intent intent =new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}

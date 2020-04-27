package com.example.temphumi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.temphumi.Model.SharedPreferencesModel;
import com.example.temphumi.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CheckUserAuthentication();
    }

    private void CheckUserAuthentication() {
        if (SharedPreferencesModel.getInstance(this).isLoggedIn()) {
            finish();
            Splash(MainActivity.class);
            return;
        }
        else
        {
            Splash(LoginActivity.class);
        }
    }

    private void Splash(final Class nextActivity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(SplashActivity.this, nextActivity);
                startActivity(intent);
            }
        }, 2500);
    }
}

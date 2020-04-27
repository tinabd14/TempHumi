package com.example.temphumi.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.temphumi.Activity.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SharedPreferencesModel {
    private static final String SHARED_PREF_NAME = "temphumisharedpreferences";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_SURNAME = "keysurname";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PASSWORD = "keypassword";

    private static SharedPreferencesModel mInstance;
    private static Context myContext;

    private SharedPreferencesModel(Context context) {
        myContext = context;
    }

    public static synchronized SharedPreferencesModel getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesModel(context);
        }
        return mInstance;
    }

    public void login(UserModel user) {
        SharedPreferences sharedPreferences = myContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_SURNAME, user.getSurname());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = myContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public UserModel getUser() throws ParseException {
        SharedPreferences sharedPreferences = myContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return new UserModel(
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_SURNAME, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PASSWORD, null)
        );
    }

    public void logout() {
        SharedPreferences sharedPreferences = myContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        myContext.startActivity(new Intent(myContext, LoginActivity.class));
    }
}

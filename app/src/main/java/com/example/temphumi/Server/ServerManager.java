package com.example.temphumi.Server;

import android.app.Application;
import android.util.Log;

import com.example.temphumi.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class ServerManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "938466448267");
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("parse", "ok");
                    subscribe("TempHumi");
                } else {
                    Log.i("parse", "not ok");
                    e.printStackTrace();
                }
            }
        });    }

    public void subscribe(String channel){
        ParsePush.subscribeInBackground(channel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Push", "subscribed");
                } else {
                    Log.i("Push", "not subscribed");
                    e.printStackTrace();
                }
            }
        });
    }
}

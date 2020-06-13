package com.example.temphumi.Server;

import android.app.Application;

import com.example.temphumi.R;
import com.parse.Parse;
import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryClient;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

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

        LiveQueryClient.init("wss://temphumi.back4app.io", getString(R.string.back4app_app_id), true);
        LiveQueryClient.connect();
    }
}

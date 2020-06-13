package com.example.temphumi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.temphumi.Model.Institution;
import com.example.temphumi.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView name;
    EditText accessCodeEditText;
    TextView insideTemp;
    TextView insideHum;
    TextView insideCarbon;
    TextView outsideTemp;
    TextView outsideHum;
    TextView outsideCarbon;

    EditText score;
    String accessCode;
    int count = 0;
    double sum = 0;
    double avgScore;
    boolean pushSent;

    Institution institution;
    ParseLiveQueryClient parseLiveQueryClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessCodeEditText = findViewById(R.id.institutionEditText);
        name = findViewById(R.id.name);
        insideTemp = findViewById(R.id.insideTemp);
        insideHum = findViewById(R.id.insideHum);
        insideCarbon = findViewById(R.id.insideClean);

        outsideTemp = findViewById(R.id.outsideTemp);
        outsideHum = findViewById(R.id.outsideHum);
        outsideCarbon = findViewById(R.id.outsideClean);

        score = findViewById(R.id.scoreEditText);

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://temphumi.back4app.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        final int delay = 10000;

        handler.postDelayed(new Runnable(){
            public void run(){
                pushSent = false;
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.resetPassword)
        {
            try {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.checkEmail), Toast.LENGTH_LONG).show();
                ParseUser.getCurrentUser().requestPasswordReset(ParseUser.getCurrentUser().getString("EMAIL"));
                ParseUser.logOut();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if(item.getItemId() == R.id.aboutUs)
        {
            Intent i = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(i);
        }
        else if(item.getItemId() == R.id.logout)
        {
            ParseUser.logOut();
            alertDisplayer("So, you're going...", "Ok...Bye-bye then");
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void SubmitInstitution(View view) {
        FindInstitution();
    }

    private void FindInstitution() {
        accessCode = accessCodeEditText.getText().toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Institution");
        query.whereEqualTo("accessCode", accessCode);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        ParseObject object = objects.get(0);
                        String name = object.getString("name");

                        double idealTemp = object.getDouble("idealTemp");
                        double idealHum = object.getDouble("idealHum");
                        double idealCarbon = object.getDouble("idealCarbon");
                        double idealScore = object.getDouble("idealScore");

                        double insideTemp = object.getDouble("insideTemp");
                        double insideHum = object.getDouble("insideHum");
                        double insideCarbon = object.getDouble("insideCarbon");

                        double outsideTemp = object.getDouble("outsideTemp");
                        double outsideHum = object.getDouble("outsideHum");
                        double outsideCarbon = object.getDouble("outsideCarbon");

                        institution = new Institution(name,accessCode,
                                idealTemp,idealHum,idealCarbon,idealScore,
                                insideTemp,insideHum,insideCarbon,
                                outsideTemp,outsideHum,outsideCarbon);

                        UpdateUI();
                        SetupLiveQuery();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Couldn't find institution...", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetupLiveQuery() {
        if (parseLiveQueryClient != null) {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Institution");
            parseQuery.whereEqualTo("accessCode", institution.getAccessCode());
            SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
                @Override
                public void onEvent(ParseQuery<ParseObject> query, final ParseObject object) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            double insideTemp = object.getDouble("insideTemp");
                            double insideHum = object.getDouble("insideHum");
                            double insideCarbon = object.getDouble("insideCarbon");

                            double outsideTemp = object.getDouble("outsideTemp");
                            double outsideHum = object.getDouble("outsideHum");
                            double outsideCarbon = object.getDouble("outsideCarbon");

                            institution.setInsideTemp(insideTemp);
                            institution.setInsideHum(insideHum);
                            institution.setInsideCarbon(insideCarbon);

                            institution.setOutsideTemp(outsideTemp);
                            institution.setOutsideHum(outsideHum);
                            institution.setOutsideCarbon(outsideCarbon);

                            UpdateUI();

                            if(!pushSent)
                            {
                                try {
                                    SendPushNotification(institution.getIdealTemp(), institution.getIdealHum(), institution.getIdealCarbon(),
                                            insideTemp, insideHum, insideCarbon,
                                            outsideTemp, outsideHum, outsideCarbon);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void SendPushNotification(double idealTemp, double idealHum, double idealCarbon,
                                      double insideTemp, double insideHum, double insideCarbon,
                                      double outsideTemp, double outsideHum, double outsideCarbon) throws JSONException {
        JSONObject data = new JSONObject();
        String title;
        String alert;

        if(insideTemp != idealTemp)
        {
            if(insideTemp > idealTemp)
            {
                title = "It is hot inside";
                if(outsideTemp < insideTemp)
                {
                    if(outsideCarbon >= idealCarbon)
                    {
                        alert = (insideTemp - idealTemp) + "'C hotter.\nAir is clean outside. Open the window to get cooler";
                    }
                    else
                    {
                        alert = (insideTemp - idealTemp) + "'C hotter.\nAir is dirty outside. Use clima to get cooler";
                    }
                }
                else
                {
                    alert = (insideTemp - idealTemp) + "'C hotter.\nUse clima to get cooler";
                }
            }
            else
            {
                title = "It is cold inside";
                if(outsideTemp > insideTemp)
                {
                    if(outsideCarbon >= idealCarbon)
                    {
                        alert = (idealTemp - insideTemp) + "'C colder.\nAir is clean outside. Open the window to get hotter";
                    }
                    else
                    {
                        alert = (idealTemp - insideTemp) + "'C colder.\nAir is dirty outside. Use clima to get hotter";
                    }
                }
                else
                {
                    alert = (idealTemp - insideTemp) + "'C colder.\nUse clima to get cooler";
                }
            }
            data.put("title", title);
            data.put("alert", alert);
            ParsePush push = new ParsePush();
            push.setChannel("Temperature");
            push.setData(data);
            push.sendInBackground();
            pushSent = true;
        }
        if(insideHum != idealHum)
        {
            if(insideHum > idealHum)
            {
                title = "High humidity inside";
                if(outsideHum < insideHum)
                {
                    if(outsideCarbon >= idealCarbon)
                    {
                        alert = (insideHum - idealHum) + "% more humid.\nAir is clean outside. Open the window to reduce humidity";
                    }
                    else
                    {
                        alert = (insideHum - idealHum) + "% more humid.\nAir is dirty outside. Use dehumidifier to reduce humidity";
                    }
                }
                else
                {
                    alert = (insideHum - idealHum) + "% more humid.\nUse dehumidifier to reduce humidity";
                }
            }
            else
            {
                title = "Low humidity inside";
                if(outsideHum > insideHum)
                {
                    if(outsideCarbon >= idealCarbon)
                    {
                        alert = (idealHum - insideHum) + "% less humid.\nAir is clean outside. Open the window to increase humidity";
                    }
                    else
                    {
                        alert = (idealHum - insideHum) + "% less humid.\nAir is dirty outside. Use humidifier to to increase humidity";
                    }
                }
                else
                {
                    alert = (idealTemp - insideTemp) + "% less humid.\nUse humidifier to to increase humidity";
                }
            }
            data.put("title", title);
            data.put("alert", alert);
            ParsePush push = new ParsePush();
            push.setChannel("Humidity");
            push.setData(data);
            push.sendInBackground();
            pushSent = true;
        }
        if(insideCarbon < idealCarbon)
        {
            title = "Dirty weather inside";
            if(outsideCarbon >= idealCarbon)
            {
                alert = "Air is clean outside.\nOpen the window for fresh air";
            }
            else

            {
                alert = "Air is dirty outside.\nUse purifier for fresh air";
            }

            data.put("title", title);
            data.put("alert", alert);
            ParsePush push = new ParsePush();
            push.setChannel("Carbon");
            push.setData(data);
            push.sendInBackground();
            pushSent = true;
        }
    }

    private void UpdateUI() {
        name.setText(institution.getName());

        insideTemp.setText((String.format(Locale.US,"%.2f", institution.getInsideTemp())));
        insideHum.setText((String.format(Locale.US,"%.2f", institution.getInsideHum())));
        insideCarbon.setText((String.format(Locale.US,"%.2f", institution.getInsideCarbon())));

        outsideTemp.setText((String.format(Locale.US,"%.2f", institution.getOutsideTemp())));
        outsideHum.setText((String.format(Locale.US,"%.2f", institution.getOutsideHum())));
        outsideCarbon.setText((String.format(Locale.US,"%.2f", institution.getOutsideCarbon())));
    }

    public void nextButtonClicked(View view) {
        if(score.getText().length() > 0)
        {
            sum += Double.valueOf(score.getText().toString());
            count++;
            avgScore = sum /count;
            score.setText("");
        }
    }

    public void finishButtonClicked(View view) {
        if(institution != null)
        {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Institution");
            query.whereEqualTo("accessCode", institution.getAccessCode());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null)
                    {
                        if(objects.size() > 0)
                        {
                            ParseObject parseObject = objects.get(0);
                            if(avgScore > parseObject.getDouble("idealScore"))
                            {
                                double idealTemp = parseObject.getDouble("idealTemp");
                                double idealHum = parseObject.getDouble("idealHum");
                                double idealCarbon = parseObject.getDouble("idealCarbon");
                                double idealScore = avgScore;

                                institution.setIdealTemp(idealTemp);
                                institution.setIdealHum(idealHum);
                                institution.setIdealCarbon(idealCarbon);
                                institution.setIdealScore(idealScore);

                                parseObject.put("idealTemp", idealTemp);
                                parseObject.put("idealHum", idealHum);
                                parseObject.put("idealCarbon", idealCarbon);
                                parseObject.put("idealScore", idealScore);
                                parseObject.saveInBackground();
                            }
                        }
                    }
                }
            });
        }
    }

    public void cancelButtonClicked(View view) {
        sum = 0;
        count = 0;
        avgScore = 0;
        Toast.makeText(MainActivity.this, "Values are reset!", Toast.LENGTH_SHORT).show();
    }
}

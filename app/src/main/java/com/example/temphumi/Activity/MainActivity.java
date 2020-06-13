package com.example.temphumi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;
import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView name;
    EditText accessCode;
    TextView insideTemp;
    TextView insideHum;
    TextView insideCarbon;
    TextView outsideTemp;
    TextView outsideHum;
    TextView outsideCarbon;

    EditText score;
    int count = 0;
    double sum = 0;
    double avgScore;

    Institution institution;
    boolean valid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessCode = findViewById(R.id.institutionEditText);
        insideTemp = findViewById(R.id.insideTemp);
        insideHum = findViewById(R.id.insideHum);
        insideCarbon = findViewById(R.id.insideClean);

        outsideTemp = findViewById(R.id.outsideTemp);
        outsideHum = findViewById(R.id.outsideHum);
        outsideCarbon = findViewById(R.id.outsideClean);

        score = findViewById(R.id.scoreEditText);
        valid = false;
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Institution");
        query.whereEqualTo("accessCode", accessCode);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        valid = true;
                        SetupLiveQuery();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Couldn't find institution...", Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            }
        });
    }

    private void SetupLiveQuery() {
        final Subscription sub = new BaseQuery.Builder("Institution")
                .where("objectId", institution.getId())
                .build()
                .subscribe();

        sub.on(LiveQueryEvent.CREATE, new OnListener() {
            @Override
            public void on(JSONObject object) {
                final JSONObject object1 = object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String id = object1.getString("objectId");
                            String name = object1.getString("name");

                            double idealTemp = object1.getDouble("idealTemp");
                            double idealHum = object1.getDouble("idealHum");
                            double idealCarbon = object1.getDouble("idealCarbon");
                            double idealScore = object1.getDouble("idealScore");

                            double insideTemp = object1.getDouble("insideTemp");
                            double insideHum = object1.getDouble("insideHum");
                            double insideCarbon = object1.getDouble("insideCarbon");

                            double outsideTemp = object1.getDouble("outsideTemp");
                            double outsideHum = object1.getDouble("outsideHum");
                            double outsideCarbon = object1.getDouble("outsideCarbon");

                            institution = new Institution(id,name,accessCode.getText().toString(),
                                    idealTemp,idealHum,idealCarbon,idealScore,
                                    insideTemp,insideHum,insideCarbon,
                                    outsideTemp,outsideHum,outsideCarbon);

                            UpdateUI();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
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
            query.whereEqualTo("objectId", institution.getId());
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

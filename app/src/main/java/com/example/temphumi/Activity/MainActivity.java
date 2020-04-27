package com.example.temphumi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.temphumi.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText institutionEditText;
    TextView idealTempTextView;
    TextView idealHumTextView;
    EditText scoreEditText;

    private double idealTemp;
    private double idealHum;
    private double idealScore;

    int count = 0;
    int sum = 0;
    String institutionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        institutionEditText = findViewById(R.id.institutionEditText);
        idealTempTextView = findViewById(R.id.idealTempTextView);
        idealHumTextView = findViewById(R.id.idealHumTextView);
        scoreEditText = findViewById(R.id.scoreEditText);
        idealTemp = 0;
        idealHum = 0;
        idealScore = 0;
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

        institutionText = institutionEditText.getText().toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Institution");
        query.whereEqualTo("name", institutionText);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        idealTemp = objects.get(0).getDouble("idealTemp");
                        idealHum = objects.get(0).getDouble("idealHum");
                        idealScore = objects.get(0).getDouble("idealScore");
                        ShowCalculations();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Couldn't find such an institution...", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        UpdateUI();
    }

    private void ShowCalculations() {

    }

    private void UpdateUI() {
        idealTempTextView.setText(String.valueOf(Double.valueOf(idealTemp).intValue()));
        idealHumTextView.setText(String.valueOf(Double.valueOf(idealHum).intValue()));
    }

    public void nextButtonClicked(View view) {
        if(scoreEditText.getText().length() > 0)
        {
            sum += Double.valueOf(scoreEditText.getText().toString());
            count++;
            scoreEditText.setText("");
        }
    }

    public void finishButtonClicked(View view) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Institution");
        query.whereEqualTo("name", institutionText);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        ParseObject parseObject = objects.get(0);
                        double avgScore = sum/count;
                        if(avgScore > parseObject.getDouble("idealScore"))
                        {
                            idealTemp = (idealTemp + parseObject.getInt("idealTemp")) / 2;
                            idealHum = (idealHum + parseObject.getInt("idealHum")) / 2;
                            idealScore = avgScore;

                            Log.i("Name: ", parseObject.get("name").toString());
                            parseObject.put("idealTemp", idealTemp);
                            parseObject.put("idealHum", idealHum);
                            parseObject.put("idealScore", idealScore);
                            parseObject.saveInBackground();

                            UpdateUI();
                            sum = 0;
                            count = 0;
                        }
                    }
                }
            }
        });
    }

    public void cancelButtonClicked(View view) {
        sum = 0;
        count = 0;
        Toast.makeText(MainActivity.this, "Values are reset!", Toast.LENGTH_SHORT).show();
    }
}

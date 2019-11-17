package com.example.etoolroom;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private long currentTime;
    private long blockTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        Button b_toolroom = findViewById(R.id.btn_toolroom);
        Button b_manufacture = findViewById(R.id.btn_manufacturing);

        blockPeriod();

        b_toolroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //login dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                @SuppressLint("InflateParams") final View inflater = getLayoutInflater().inflate(R.layout.login_dialog, null);
                builder.setView(inflater).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_password = inflater.findViewById(R.id.password);
                        String e_password = et_password.getText().toString();
                        if (e_password.equals("1122")) {
                            //toolroom Activity
                            Intent i = new Intent(getApplicationContext(), ToolRoomActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //report activity
                        Intent i = new Intent(getApplicationContext(), ReportActivity.class);
                        startActivity(i);
                    }
                }).setCancelable(true).create().show();
            }
        });

        b_manufacture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                @SuppressLint("InflateParams") final View inflater = getLayoutInflater().inflate(R.layout.login_dialog, null);
                builder.setView(inflater).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_password = inflater.findViewById(R.id.password);
                        String e_password = et_password.getText().toString();
                        if (e_password.equals("9988")) {
                            //manufacturing activity
                            Intent i = new Intent(getApplicationContext(), ManufacturingActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), ReportActivity.class);
                        startActivity(i);
                    }
                }).setCancelable(true).create().show();
            }
        });
    }

    private void blockPeriod() {
        currentTime = System.currentTimeMillis();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2020);
        cal.set(Calendar.MONTH,3);
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.HOUR,9);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        blockTime = cal.getTimeInMillis();

        if (currentTime >= blockTime){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert")
                    .setMessage("Contact the Administrator for more info")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setCancelable(false).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                //dev details dialog box
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setTitle("Developed By")
                        .setMessage("S.Dhanuvanth\ndhanuvanth@gmail.com\n+91 7010 384 896")
                        .create().show();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //exit
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Exit").setMessage("Do you want to exit?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
}

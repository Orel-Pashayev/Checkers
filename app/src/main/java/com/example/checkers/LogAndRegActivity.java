package com.example.checkers;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

public class LogAndRegActivity extends AppCompatActivity implements View.OnClickListener {
    private final ActivityResultHelper<Intent, ActivityResult> activityLauncher = ActivityResultHelper.registerActivityForResult(this);
    Button bt_reg, bt_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_and_reg);

        bt_log = findViewById(R.id.bt_log);
        bt_log.setOnClickListener(this);
        bt_reg = findViewById(R.id.bt_reg);
        bt_reg.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onClick(View v) {
        if (v == bt_log){
            Intent intent = new Intent(this, LoginActivity.class);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data == null){
                        Toast.makeText(LogAndRegActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this,"Successful login",Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }

        if (v == bt_reg){
            Intent intent = new Intent(this, RegisterActivity.class);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data == null){
                        Toast.makeText(LogAndRegActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this,"Successful registration",Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }
    }
}
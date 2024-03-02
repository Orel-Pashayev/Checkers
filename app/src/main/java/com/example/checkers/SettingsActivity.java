package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button sign_out_bt, blue, green, brown, yellow, save_bt;
    String lastColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sign_out_bt = findViewById(R.id.sign_out_bt);
        sign_out_bt.setOnClickListener(this);
        blue = findViewById(R.id.blue_bt);
        blue.setOnClickListener(this);
        green = findViewById(R.id.green_bt);
        green.setOnClickListener(this);
        brown = findViewById(R.id.brown_bt);
        brown.setOnClickListener(this);
        yellow = findViewById(R.id.yellow_bt);
        yellow.setOnClickListener(this);
        save_bt = findViewById(R.id.save_bt);
        save_bt.setOnClickListener(this);
        lastColor ="";
    }

    @Override
    public void onClick(View v) {
        if (sign_out_bt == v){
            Intent intent = new Intent();
            intent.putExtra("data", "sign out");
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (v == save_bt){
            Intent intent = new Intent();
            intent.putExtra("data", lastColor);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if(v == green)
            lastColor = "green";
        else if(v == blue)
            lastColor = "blue";
        else if(v == yellow)
            lastColor = "yellow";
        else if(v == brown)
            lastColor = "brown";
    }
}
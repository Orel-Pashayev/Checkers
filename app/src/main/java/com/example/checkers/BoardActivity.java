package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.view.View;
import android.graphics.Canvas;
import android.widget.TextView;

public class BoardActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "user_details";
    public static final String COLOR_MODE = "color_mode";
    BoardView bv;
    LinearLayout l;
    String roomId, player1Id;
    long gameTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        roomId = getIntent().getExtras().getString("room_id", "");
        player1Id = getIntent().getExtras().getString("firstPlayerId", "");
        gameTime = getIntent().getExtras().getLong("gameTime", 0);

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int colorMode = sp.getInt(COLOR_MODE, 0);
        bv = new BoardView(this, roomId, player1Id, colorMode, gameTime);
        l=(LinearLayout)findViewById(R.id.myCanvas);
        l.addView(bv);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    bv.postInvalidate(); // Request a redraw
                    try {
                        Thread.sleep(1000); // Sleep for 1 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
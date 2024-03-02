package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final ActivityResultHelper<Intent, ActivityResult> activityLauncher = ActivityResultHelper.registerActivityForResult(this);
    public static final String SHARED_PREFS = "user_details";
    private final String EMAIL = "user", PASSWORD="password";
    public static final String COLOR_MODE = "color_mode";
    public static final String MEDIA = "media_mode";
    private FirebaseAuth mAuth;

    MyApp myApp;
    SharedPreferences sp;
    Button play, rules, settings_bt;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        play = findViewById(R.id.play_bt);
        play.setOnClickListener(this);
        rules = findViewById(R.id.rules_bt);
        rules.setOnClickListener(this);
        settings_bt = findViewById(R.id.settings_bt);
        settings_bt.setOnClickListener(this);
        if (sp.getString(EMAIL, "").equals("")){
            openLoginAndRegister();
            return;
        }
        myApp = (MyApp) getApplication();
        System.out.println(sp.getString(MEDIA, "")+" AOSPODFSFPOAS");
        if (!(sp.getString(MEDIA, "").equals("off"))){
            if (sp.getString(MEDIA, "").equals("electric"))
                myApp.startBackgroundMusic("electric");
            else if (sp.getString(MEDIA, "").equals("chill"))
                myApp.startBackgroundMusic("chill");
        }
        mAuth.signInWithEmailAndPassword(sp.getString(EMAIL, ""), sp.getString(PASSWORD, ""));
    }

    public void openLoginAndRegister(){
        Intent intent = new Intent(this, LogAndRegActivity.class);
        activityLauncher.launch(intent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
                if (data != null){
                    FirebaseUser user = mAuth.getCurrentUser();
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString(EMAIL, user.getEmail());
                    editor.putString(PASSWORD, data.getExtras().getString("pass"));
                    editor.putInt(COLOR_MODE, 0);
                    editor.apply(); // Apply changes to SharedPreferences
                }
            }
            else{
                Toast.makeText(this,"Result Canceled",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
    }

    public void createEndGameDialog(String doesWin){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (doesWin.equals("won")){
            builder.setTitle("Congratulations!")
                    .setMessage("You've emerged as the champion! Your skills and strategy paid off.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Handle the positive button click
                            dialog.dismiss();
                        }
                    });
        }
        else {
            builder.setTitle("Defeat")
                    .setMessage("Don't be too hard on yourself. Remember, every defeat is an opportunity to learn and grow.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Handle the positive button click
                            dialog.dismiss();
                        }
                    });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (play == v){
            Intent intent = new Intent(this, RoomActivity.class);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Intent intent2 = new Intent(this, BoardActivity.class);
                    intent2.putExtra("room_id", data.getExtras().getString("room_id",""));
                    intent2.putExtra("firstPlayerId", data.getExtras().getString("firstPlayerId",""));
                    intent2.putExtra("gameTime", data.getExtras().getLong("gameTime", 0));
                    activityLauncher.launch(intent2, result2 -> {
                        if (result2.getResultCode() == Activity.RESULT_OK) {
                            Intent data2 = result.getData();
                            createEndGameDialog(data.getExtras().getString("game_result"));
                        }
                    });
                }
            });
            return;
        }
        if (rules == v){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_rules, null);
            builder.setView(dialogView);
            TextView textView = dialogView.findViewById(R.id.textView);
            textView.setText("1. Board: Play on an 8x8 square board with alternating light and dark squares.\n" +
                    "\n" +
                    "2. Pieces: Each player starts with 12 pieces placed on the dark squares of the three rows closest to them.\n" +
                    "\n" +
                    "3. Movement: Pieces can only move diagonally forward on the dark squares. Regular pieces can only move forward, while kings (promoted pieces) can move forward or backward.\n" +
                    "\n" +
                    "4. Capturing: If a player's piece is adjacent to an opponent's piece with an empty space diagonally beyond it, they can jump over the opponent's piece and remove it from the board. Multiple captures can be made in a single turn if the opportunity arises.\n" +
                    "\n" +
                    "5. King Promotion: When a regular piece reaches the last row on the opponent's side, it becomes a king. Kings are marked by stacking an additional piece of the same color on top of it. Kings can move and capture both forward and backward.\n" +
                    "\n" +
                    "6. Turn Alternation: Players take turns moving their pieces, with the player controlling the dark-colored pieces moving first. Once a player makes a move, their turn is completed, and it's the other player's turn.\n" +
                    "\n" +
                    "7. End of the Game: The game ends when:\n" +
                    "A player captures all of the opponent's pieces.\n" +
                    "A player blocks all of the opponent's pieces, making them unable to move.\n" +
                    "The players agree to a draw or a tie.\n" +
                    "\n" +
                    "Enjoy your game of English Draughts!");
            builder.setNegativeButton("Back", (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        if (v == settings_bt){
            Intent intent = new Intent(this, SettingsActivity.class);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null || data.getExtras().getString("data", "").equals("")) {
                        return;
                    }
                    SharedPreferences.Editor editor = sp.edit();
                    if (data.getExtras().getString("data", "").equals("sign out")){
                        mAuth.signOut();

                        editor.putString(EMAIL, "");
                        editor.putString(PASSWORD, "");
                        editor.putInt(COLOR_MODE, 0);
                        editor.putString(MEDIA, "off");
                        editor.apply(); // Apply changes to SharedPreferences
                        openLoginAndRegister();
                        return;
                    }
                    else if (data.getExtras().getString("data", "").equals("blue")){
                        editor.putInt(COLOR_MODE, 0); // blue = 0
                    }
                    else if (data.getExtras().getString("data", "").equals("green")){
                        editor.putInt(COLOR_MODE, 1); // green = 1
                    }
                    else if (data.getExtras().getString("data", "").equals("brown")){
                        editor.putInt(COLOR_MODE, 2); // brown = 2
                    }
                    else if (data.getExtras().getString("data", "").equals("yellow")){
                        editor.putInt(COLOR_MODE, 3); // yellow = 3
                    }
                    editor.putString(MEDIA, "off");
                    if (data.getExtras().getString("music", "").equals("off")){
                        editor.putString(MEDIA, "off");
                        myApp.stopBackgroundMusic();
                    }
                    else if (data.getExtras().getString("music", "").equals("electric")){
                        editor.putString(MEDIA, "electric");
                        myApp.startBackgroundMusic("electric");
                    }
                    else if (data.getExtras().getString("music", "").equals("chill")){
                        editor.putString(MEDIA, "chill");
                        myApp.startBackgroundMusic("chill");
                    }
                    editor.apply();
                }
            });
        }
    }
}
package com.example.checkers;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener {
    private final ActivityResultHelper<Intent, ActivityResult> activityLauncher = ActivityResultHelper.registerActivityForResult(this);
    private ListView listView;
    private RoomAdapter adapter;
    private static boolean running;
    private long gameTime;
    AlertDialog loadingDialog;
    Button createBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        createBt = findViewById(R.id.create_bt);
        createBt.setOnClickListener(this);
        gameTime = 0;
        running = true;

        listView = findViewById(R.id.listView);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    fetchAvailableRooms();
                    try {
                        Thread.sleep(2000); // Sleep for 2 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        if (v == createBt){
            createRoom();
        }
    }

    private void createRoom() {
        //Get game time
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // Disable dialog cancellation
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_gametime, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Button one_min = dialogView.findViewById(R.id.one_min);
        one_min.setOnClickListener(v -> {
            gameTime = 60_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button three_min = dialogView.findViewById(R.id.three_min);
        three_min.setOnClickListener(v -> {
            gameTime = 180_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button five_min = dialogView.findViewById(R.id.five_min);
        five_min.setOnClickListener(v -> {
            gameTime = 300_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button ten_min = dialogView.findViewById(R.id.ten_min);
        ten_min.setOnClickListener(v -> {
            gameTime = 600_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button fifteen_min = dialogView.findViewById(R.id.fifteen_min);
        fifteen_min.setOnClickListener(v -> {
            gameTime = 900_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button thirty_min = dialogView.findViewById(R.id.thirty_min);
        thirty_min.setOnClickListener(v -> {
            gameTime = 1_800_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button forty_five_min = dialogView.findViewById(R.id.fortyfive_min);
        forty_five_min.setOnClickListener(v -> {
            gameTime = 2_700_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        Button sixty_min = dialogView.findViewById(R.id.sixty_min);
        sixty_min.setOnClickListener(v -> {
            gameTime = 3_600_000;
            dialog.cancel();
            writeToFirebase();
            waitForUser();
        });
        dialog.show();

    }
    private void waitForUser()
    {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(RoomActivity.this);
        builder2.setTitle("Loading").setMessage("Waiting for another player to join...").setCancelable(false);
        loadingDialog = builder2.create();
        loadingDialog.show();

    }
    private void writeToFirebase() {
        String roomId = generateRoomId();
        String currentPlayerId = getCurrentUserId();
        Room room = new Room(roomId, currentPlayerId, null, "Waiting", gameTime);
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        // Set the room information in the database
        roomsRef.child(roomId).setValue(room)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference roomRef = roomsRef.child(roomId);
                        roomRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Room updatedRoom = dataSnapshot.getValue(Room.class);
                                if (updatedRoom != null) {
                                    int playerCount = updatedRoom.getPlayerCount();
                                    if(playerCount==2)
                                    {
                                        loadingDialog.dismiss();

                                        enterGame(roomId, currentPlayerId, gameTime);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors during the listening process
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Room creation failed
                        // Handle the error
                    }
                });
    }


    private void fetchAvailableRooms() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Room> availableRooms = new ArrayList<>();

                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);

                    // Check if the room is available (not full and not in progress)
                    if (room != null && !isRoomFull(room) && !isRoomInProgress(room)) {
                        availableRooms.add(room);
                    }
                }

                // Process the list of available rooms
                handleAvailableRooms(availableRooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                setResult(RESULT_CANCELED, null);
                Toast.makeText(RoomActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void handleAvailableRooms(List<Room> availableRooms){
        // Add data to the dataList

        adapter = new RoomAdapter(this, availableRooms);
        listView.setAdapter(adapter);
    }

    private void enterGame(String roomId, String player1Id, long gameTime){
        running = false;
        Intent intent = new Intent(this, BoardActivity.class);
        intent.putExtra("room_id", roomId);
        intent.putExtra("firstPlayerId", player1Id);
        intent.putExtra("gameTime", gameTime);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void joinRoom(Room room) {
        String currentUserId = getCurrentUserId();

        if (currentUserId != null) {
            // Set the second player ID and update the game state
            room.setPlayer2Id(currentUserId);
            room.setGameState("In Progress");

            DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(room.getRoomId());

            roomRef.setValue(room)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            enterGame(room.getRoomId(), room.getPlayer1Id(), room.getGameTime());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Room joining failed
                            // Handle the error
                        }
                    });
        }
    }
    private String generateRoomId() {
        // Generate a unique room ID
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(1000);
        return "room_" + timestamp + "_" + random;
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        }
        return null;
    }

    private boolean isRoomFull(Room room) {
        return room.getPlayer1Id() != null && room.getPlayer2Id() != null;
    }

    private boolean isRoomInProgress(Room room) {
        return "In Progress".equals(room.getGameState());
    }
}
package com.example.checkers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RoomAdapter extends BaseAdapter {

    private List<Room> roomList; // Replace with your actual data type
    private LayoutInflater inflater;
    private Context context;
    public RoomAdapter(Context context, List<Room> roomList) {
        this.roomList = roomList;
        this.inflater = LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_layout, parent, false);
        }
        Room data = roomList.get(position);
        TextView name = convertView.findViewById(R.id.name);
        TextView time = convertView.findViewById(R.id.gameTime);
        TextView roomId = convertView.findViewById(R.id.roomId);
        roomId.setText(String.valueOf(data.getRoomId()));
        name.setText(data.getPlayer1Id());
        time.setText(data.getGameTime() / 60_000 + " min");
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms")
                .child(data.getRoomId());

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room updatedRoom = dataSnapshot.getValue(Room.class);
                if (updatedRoom != null) {
                    int playerCount=0;
                    if(updatedRoom.getPlayer2Id()!=null)
                    {
                        playerCount=2;
                    }
                    if(playerCount==2)
                    {
                        Toast.makeText(context, "Two players waiting for game", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors during the listening process
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomActivity activity = (RoomActivity) context;
                activity.joinRoom(data);
            }
        });

        return convertView;
    }
}
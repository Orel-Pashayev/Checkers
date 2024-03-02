package com.example.checkers;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {
    private String roomId;
    private String player1Id;
    private String player2Id;
    private String gameState;
    private long gameTime;
    private String stringPiece;
    private int[][] indexPieces;

    public Room(){

    }

    public Room(String roomId, String player1Id, String player2Id, String gameState, long gameTime) {
        this.roomId = roomId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.gameState = gameState;
        this.gameTime = gameTime;

        this.indexPieces = new int[8][8];
        for (int i=0; i<3; i++){
            for (int j=0; j<8; j++){
                if ((i+j) % 2 == 1) {this.indexPieces[i][j] = 4;}
            }
        }
        for (int i=5; i<8; i++){
            for (int j=0; j<8; j++){
                if ((i+j) % 2 == 1) {this.indexPieces[i][j] = 1;}
            }
        }
        this.stringPiece = Arrays.deepToString(indexPieces);
    }

    public int getPlayerCount(){
        if (player2Id != null)
            return 2;
        return 1;
    }


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }
    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public String getStringPiece() {
        return stringPiece;
    }

    public void setStringPiece(String stringPiece) {
        this.stringPiece = stringPiece;
    }
}

package com.example.checkers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;

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

public class GameLogic {
    private final int EMPTY_COLOR = 0;
    private final int BOARD_SIZE = 8;
    private int squareSize, margin;
    private List<Piece> piecesList;
    private Square[][] squares;
    private int[][] indexPieces;
    private int myColor, colorMode;
    private int yMargin;
    private int opponentColor;
    private boolean doesMyMove;
    private String room_id;
    private CountDownTimer countDownTimer;
    private long gameTime;
    private long timeRemaining;
    private boolean checkIfTimerOn;

    public String gameOverStatus = "";

    public GameLogic(int squareSize, int margin, String room_id, String player1Id, int colorMode, long gameTime){
        this.squareSize = squareSize;
        this.margin = margin;
        this.room_id = room_id;
        this.colorMode = colorMode;
        this.gameTime = gameTime;
        checkIfTimerOn = false;
        yMargin = 200;
        myColor = 4;
        opponentColor = 1;
        doesMyMove = true;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (player1Id.equals(currentUser.getEmail())){
            myColor = 1;
            opponentColor = 4;
            doesMyMove = false;
        }
        if (doesMyMove){
            checkIfTimerOn = true;
            startCountdown(gameTime);
        }
        indexPieces = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i=0; i<3; i++){
            for (int j=0; j<8; j++){
                if ((i+j) % 2 == 1) {indexPieces[i][j] = opponentColor;}
            }
        }
        for (int i=5; i<8; i++){
            for (int j=0; j<8; j++){
                if ((i+j) % 2 == 1) {indexPieces[i][j] = myColor;}
            }
        }
        setPiecesAndSquares();

        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms").child(room_id).child("stringPiece");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String updatedBoard = (String) dataSnapshot.getValue();
                deSerialize(updatedBoard);
                if (myColor == 4)
                    indexPieces = rotateArray180(indexPieces);
                List<Piece> newList = new ArrayList<>();
                for (int i=0; i<BOARD_SIZE; i++){
                    for (int j=0; j<BOARD_SIZE; j++){
                        if (indexPieces[i][j] != 0){
                            Piece p;
                            if (indexPieces[i][j] > 10)
                                p = new King(i, j, indexPieces[i][j], squareSize, margin);
                            else
                                p = new Piece(i, j, indexPieces[i][j], squareSize, margin);
                            newList.add(p);
                            squares[i][j].setEmpty(false);
                        }
                        else{
                            squares[i][j].setEmpty(true);
                        }
                    }
                }
                for (int[] row : indexPieces) {
                    for (int value : row) {
                        System.out.print(value + " ");
                    }
                    System.out.println();
                }
                changeMyMove();
                piecesList = newList;
                checkGameOver(myColor);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeMyMove(){
        doesMyMove = !doesMyMove;
        if (doesMyMove){
            if (!checkIfTimerOn){
                startCountdown(gameTime);
                checkIfTimerOn = true;
            }
            else
                resumeCountdown();
        }
        else{
            pauseCountdown();
        }
    }

    private void startCountdown(long milliseconds) {
        timeRemaining = milliseconds;

        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                gameOverStatus = "lost";
            }
        };

        countDownTimer.start();
    }

    private void pauseCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void resumeCountdown() {
        startCountdown(timeRemaining);
    }

    public long getTimeRemain(){
        return timeRemaining;
    }

    private int[][] rotateArray180(int[][] arr ){
        int rows = arr.length;
        int cols = arr[0].length;

        // Reverse each row
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                int temp = arr[i][j];
                arr[i][j] = arr[i][cols - 1 - j];
                arr[i][cols - 1 - j] = temp;
            }
        }

        // Reverse the entire array
        for (int i = 0; i < rows / 2; i++) {
            for (int j = 0; j < cols; j++) {
                int temp = arr[i][j];
                arr[i][j] = arr[rows - 1 - i][j];
                arr[rows - 1 - i][j] = temp;
            }
        }
        return arr;
    }

    private int[][] undoRotateArray180(int[][] arr){
        int rows = arr.length;
        int cols = arr[0].length;

        // Reverse the entire array
        for (int i = 0; i < rows / 2; i++) {
            for (int j = 0; j < cols; j++) {
                int temp = arr[i][j];
                arr[i][j] = arr[rows - 1 - i][j];
                arr[rows - 1 - i][j] = temp;
            }
        }

        // Reverse each row
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                int temp = arr[i][j];
                arr[i][j] = arr[i][cols - 1 - j];
                arr[i][cols - 1 - j] = temp;
            }
        }
        return arr;
    }

    private void setPiecesAndSquares(){
        squares = new Square[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int color = ((i+j) % 2 == 0) ? 0 : 1;
                squares[i][j] = new Square(j*squareSize+margin,i*squareSize+margin*2+yMargin, color, squareSize, colorMode);
            }
        }

        piecesList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int temp = (i % 2 == 0) ? 1 : 0;
            for (int j = 0; j < 4; j++) {
                Piece p = new Piece(i, j * 2 + temp, getOpponentColor(), squareSize, margin);
                piecesList.add(p);
                squares[i][j].setEmpty(false);
            }
        }
        for (int i = 5; i < 8; i++) {
            int temp = (i % 2 == 0) ? 1 : 0;
            for (int j = 0; j < 4; j++) {
                Piece p = new Piece(i, j * 2 + temp, getMyColor(), squareSize, margin);
                piecesList.add(p);
                squares[i][j].setEmpty(false);
            }
        }
    }

    public boolean checkValidMove(Piece p, int row, int col) {
        if (!doesMyMove)
            return false;
        if (p.getColor() == opponentColor){
            return false;
        }
        if (!(p instanceof King)){
            if (checkValidMoveForMan(p, row, col)){
                updateFireBoard();
                return true;
             }
        }
        else if (checkValidMoveForKing(p, row, col)){
            updateFireBoard();
            return true;
        }
        return false;
    }

    private boolean checkValidMoveForKing(Piece p, int row, int col){
        //King Move
        if ((p.getRow() - 1 == row && (p.getCol() == col + 1 || p.getCol() == col - 1) || (p.getRow() + 1 == row && (p.getCol() == col + 1 || p.getCol() == col - 1)))) {
            changeIndexPieces(p.getRow(), p.getCol(), EMPTY_COLOR);
            squares[p.getRow()][p.getCol()].setEmpty(true);
            p.move(row, col);
            changeIndexPieces(p.getRow(), p.getCol(), myColor+10);
            squares[p.getRow()][p.getCol()].setEmpty(false);
            return true;
        }

        //King Eat
        if (((p.getCol() + 2 == col || p.getCol() - 2 == col) && p.getRow() - 2 == row) || ((p.getCol() + 2 == col || p.getCol() - 2 == col) && p.getRow() + 2 == row)){
            if (doesPieceCanEat(p, row, col)){
                Piece eatenP = null;
                for(Piece temp : piecesList){
                    if (temp.checkIndex((row+p.getRow())/2,(col+p.getCol())/2)){
                        eatenP = temp;
                        break;
                    }
                }
                piecesList.remove(eatenP);
                squares[eatenP.getRow()][eatenP.getCol()].setEmpty(true);
                squares[p.getRow()][p.getCol()].setEmpty(true);
                indexPieces[p.getRow()][p.getCol()] = 0;
                p.move(row, col);
                squares[p.getRow()][p.getCol()].setEmpty(false);
                indexPieces[eatenP.getRow()][eatenP.getCol()] = 0;
                indexPieces[p.getRow()][p.getCol()] = myColor+10;
                return true;
            }
        }
        return false;
    }

    private boolean checkValidMoveForMan(Piece p, int row, int col){
        if (row == p.getRow() + 1)
            return false;
        //Man Move
        if (p.getRow() - 1 == row && (p.getCol() == col + 1 || p.getCol() == col - 1)) {
            changeIndexPieces(p.getRow(), p.getCol(), EMPTY_COLOR);
            squares[p.getRow()][p.getCol()].setEmpty(true);
            p.move(row, col);
            changeIndexPieces(p.getRow(), p.getCol(), myColor);
            squares[p.getRow()][p.getCol()].setEmpty(false);
            return true;
        }
        // Man Eat
        if ((p.getCol() + 2 == col || p.getCol() - 2 == col) && p.getRow() - 2 == row){
            if (doesPieceCanEat(p, row, col)){
                Piece eatenP = null;
                for(Piece temp : piecesList){
                    if (temp.checkIndex((row+p.getRow())/2,(col+p.getCol())/2)){
                        eatenP = temp;
                        break;
                    }
                }
                piecesList.remove(eatenP);
                squares[eatenP.getRow()][eatenP.getCol()].setEmpty(true);
                squares[p.getRow()][p.getCol()].setEmpty(true);
                indexPieces[p.getRow()][p.getCol()] = 0;
                p.move(row, col);
                squares[p.getRow()][p.getCol()].setEmpty(false);
                indexPieces[eatenP.getRow()][eatenP.getCol()] = 0;
                indexPieces[p.getRow()][p.getCol()] = myColor;
                return true;
            }
        }
        return false;
    }

    public boolean canManEat(Piece p){
        int col = p.getCol(), row = p.getRow();
        return (doesPieceCanEat(p, row - 2, col + 2) || doesPieceCanEat(p, row - 2, col - 2));
    }

    private boolean canKingEat(Piece p){
        int col = p.getCol(), row = p.getRow();
        return (doesPieceCanEat(p, row + 2, col + 2)
                || doesPieceCanEat(p, row + 2, col - 2)
                || doesPieceCanEat(p, row - 2, col + 2)
                || doesPieceCanEat(p, row - 2, col - 2));
    }

    private boolean doesPieceCanEat(Piece p, int row, int col){
        if (row > 7 || row < 0 || col >7 || col < 0)
            return false;
        if (p.getColor() != myColor) return false;
        int pCol = p.getCol(), pRow = p.getRow();
        return (((indexPieces[(row+pRow)/2][(col+pCol)/2] == opponentColor) || (indexPieces[(row+pRow)/2][(col+pCol)/2] == opponentColor+10)) && squares[row][col].getEmpty());
    }

    private boolean checkIfEmptySpace(int row, int col){
        if (row > 7 || row < 0 || col > 7 || col < 0)
            return false;
        return indexPieces[row][col] == 0;
    }

    private boolean canManMove(Piece p){
        int row = p.getRow();
        int col = p.getCol();
        if (row == 7)
            return false;
        if (canManEat(p))
            return true;
        if (checkIfEmptySpace(row-1, col+1))
            return true;
        return checkIfEmptySpace(row - 1, col - 1);
    }

    private boolean canKingMove(Piece p){
        int row = p.getRow();
        int col = p.getCol();
        if (canKingEat(p))
            return true;
        if (checkIfEmptySpace(row-1, col+1))
            return true;
        if (checkIfEmptySpace(row-1, col-1))
            return true;
        if (checkIfEmptySpace(row+1, col+1))
            return true;
        return checkIfEmptySpace(row + 1, col - 1);
    }

    public boolean checkGameOver(int color){
        //check #1
        boolean checkIfPieceExist = false;
        for(Piece p : piecesList){
            if (p.getColor() == color || p.getColor() == color+10){
                checkIfPieceExist = true;
                break;
            }
        }
        if (checkIfPieceExist)
            return false;

        //check #2
        boolean checkIfPieceCanMove = false;
        for(Piece p : piecesList){
            if (p.getColor() == color || p.getColor() == color+10){
                if (p instanceof King)
                    if(canKingMove(p)){
                        checkIfPieceCanMove = true;
                        break;
                    }
                else if(canManMove(p)){
                        checkIfPieceCanMove = true;
                        break;
                    }
            }
        }
        if (checkIfPieceCanMove)
            return false;
        changeGameOverStatus(color);
        return true;
    }

    private void changeGameOverStatus(int color)
    {
        if (color == myColor)
            gameOverStatus = "lost";
        else gameOverStatus = "won";
    }

    public void updateFireBoard(){
        int[][] temp;
        temp = indexPieces;
        checkIfNewKing();
        if (myColor == 4)
            temp = undoRotateArray180(indexPieces);
        indexPieces = temp;
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms").child(room_id).child("stringPiece");
        roomsRef.setValue(Arrays.deepToString(temp));
        checkGameOver(opponentColor);
    }

    private void checkIfNewKing(){
        Piece temp = null;
        for (Piece p : piecesList){
            if (p.getColor() == myColor && p.getRow() == 0 && !(p instanceof King)){
                temp = p;
                break;
            }
        }
        if (temp == null)
            return;
        Piece newKing = new King(temp.getRow(), temp.getCol(), myColor+10, squareSize, margin);
        indexPieces[temp.getRow()][temp.getCol()] = myColor+10;
        piecesList.remove(temp);
        piecesList.add(newKing);
    }

    public void deSerialize(String stringPiece) {
        String trimmedStr = stringPiece.substring(1, stringPiece.length() - 1);
        String[] rows = trimmedStr.split("\\], \\[");

        int rowCount = rows.length;
        int colCount = rows[0].split(", ").length;
        int[][] array2D = new int[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            String rowValuesStr = rows[i].replaceAll("\\[|\\]", "");
            String[] rowValues = rowValuesStr.split(", ");
            for (int j = 0; j < colCount; j++) {
                array2D[i][j] = Integer.parseInt(rowValues[j]);
            }
        }
        this.indexPieces = array2D;
    }

    private void changeIndexPieces(int row, int col, int playerColor){
        indexPieces[row][col] = playerColor;
    }

    public boolean getDoesMyMove(){return this.doesMyMove;}
    public int getMyColor() {return myColor;}

    public int getOpponentColor() {return opponentColor;}

    public Square[][] getSquares(){return squares;}

    public List<Piece> getPiecesList(){return piecesList;}
}

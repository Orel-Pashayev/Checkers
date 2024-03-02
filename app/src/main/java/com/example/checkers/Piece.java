package com.example.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Piece {
    private int row, col, color; //color = 1 white, color = 4 black
    protected Paint p;
    public float x,y;
    protected int r;
    protected Context context;

    public Piece(int row, int col, int color, int squareSize, int margin){
        this.row = row;
        this.col = col;
        this.color = color;
        int yMargin = 200;
        p = new Paint();
        //p.setColor(Color.rgb(170, 20, 20));
        p.setColor(Color.rgb(45, 45, 45));
        if (color == 1) p.setColor(Color.rgb(250, 230, 210));


        int left = col * squareSize+ margin;
        int top = row * squareSize + 2*margin;
        int right = left + squareSize;
        int bottom = top + squareSize;
        this.r = (right-left)/2;
        this.x = right-this.r;
        this.y = bottom-this.r+yMargin;
    }

    public void draw(Canvas canvas){
        canvas.drawCircle(x,y,r-10,p);
    }

    public boolean didUserTouchMe(float xu, float yu){
        if(xu > x - r && xu < x + r && yu > y -r && yu < y + r )
            return true;
        return false;
    }

    public void updatePosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public boolean contains(int x, int y, int squareSize, int margin) {
        int left = col * squareSize + margin;
        int top = row * squareSize + 2 * margin;
        int right = left + squareSize;
        int bottom = top + squareSize;
        return x >= left && x < right && y >= top && y < bottom;
    }

    public void move(int row, int col) {
        this.row = row;
        this.col = col;
    }


    public boolean checkIndex(int row, int col){
        return (row == this.row && this.col == col);
    }


    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getColor(){return this.color;}
}

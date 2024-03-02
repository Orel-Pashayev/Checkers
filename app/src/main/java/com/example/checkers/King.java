package com.example.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

public class King extends Piece{
    private Paint crownPaint;

    public King(int row, int col, int color, int squareSize, int margin) {
        super(row, col, color-10, squareSize, margin); // white king = 11, black king = 14
        crownPaint = new Paint();
        crownPaint.setColor(Color.RED); // Set the color for the crown
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        canvas.drawCircle(x,y,r-10,p);
        canvas.drawCircle(x,y ,(r-10)/2f, crownPaint);
    }
}

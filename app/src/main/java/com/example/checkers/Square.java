package com.example.checkers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Square {

    private boolean isEmpty, isValid;
    private float x, y, len;
    private Paint p;

    public Square(float x, float y, int color, float len, int colorMode){
        this.x = x;
        this.y = y;
        this.len = len;
        this.isEmpty = true;
        p = new Paint();
        pickColorMode(colorMode, color);
    }

    private void pickColorMode(int colorMode, int color){
        if (colorMode == 0){
            p.setColor(Color.parseColor("#82A3B7"));
            isValid = true;
            if (color == 0){
                p.setColor(Color.parseColor("#D4E0E6"));
                isValid = false;
            }
            return;
        }
        if (colorMode == 1){
            p.setColor(Color.parseColor("#769656"));
            isValid = true;
            if (color == 0){
                p.setColor(Color.parseColor("#eeeed2"));
                isValid = false;
            }
            return;
        }
        if (colorMode == 2){
            p.setColor(Color.parseColor("#7D4314"));
            isValid = true;
            if (color == 0){
                p.setColor(Color.parseColor("#E3C16F"));
                isValid = false;
            }
            return;
        }
        if (colorMode == 3){
            p.setColor(Color.parseColor("#BBBE64"));
            isValid = true;
            if (color == 0){
                p.setColor(Color.parseColor("#EAF0CE"));
                isValid = false;
            }
        }
    }

    public void draw(Canvas canvas){
        canvas.drawRect(x, y, (x+len), (y+len), p);
    }

    public boolean didXAndYInSquare(float xu, float yu)
    {
        if (!isValid || !isEmpty){return false;}
        if(xu > x && xu < x + len && yu > y && yu < y + len )
            return true;
        return false;
    }

    public void setEmpty(boolean isEmpty){
        this.isEmpty = isEmpty;
    }

    public boolean getEmpty(){return this.isEmpty;}

    public float getCenterX(){return x+len/2;}

    public float getCenterY(){return y+len/2;}
}

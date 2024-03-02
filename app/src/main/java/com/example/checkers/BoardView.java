package com.example.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

public class BoardView extends View {
    private static final String TAG = "ChessBoard";
    private final int BOARD_SIZE = 8;
    private final float SQUARE_SIZE_FRACTION = 0.94f; // fraction of screen width to use for square size
    private int squareSize, margin;
    private GameLogic gl;
    private Paint blackPaint;
    private Paint creamPaint;
    private Paint backGroundPaint;
    private Piece draggedPiece = null;
    private boolean isDragging = false;
    private float lastX, lastY;
    private Paint timePaint, turnPaint;
    private int screenWidth, screenHeight;
    Context context;

    public BoardView(Context context, String room_id, String player1Id, int colorMode, long gameTime) {
        super(context);

        this.context = context;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        // Calculate the square size based on the screen width and
        squareSize = (int) (screenWidth * SQUARE_SIZE_FRACTION / BOARD_SIZE);
        margin = (int) ((screenWidth * (1f - SQUARE_SIZE_FRACTION))/2);

        timePaint = new Paint();
        timePaint.setColor(Color.GRAY);
        timePaint.setTextSize(50f);
        turnPaint = new Paint();
        turnPaint.setColor(Color.GRAY);
        turnPaint.setTextSize(85f);
        blackPaint = new Paint();
        blackPaint.setColor(Color.DKGRAY);
        creamPaint = new Paint();
        creamPaint.setColor(Color.rgb(255, 253, 208));
        backGroundPaint = new Paint();
        backGroundPaint.setColor(Color.rgb(49, 46, 43));
        gl = new GameLogic(squareSize, margin, room_id, player1Id, colorMode, gameTime);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (gl.gameOverStatus.equals("")){
            drawBoard(canvas);
            drawPieces(canvas);
            drawTime(canvas);
            drawTurn(canvas);
        }
        else {
            drawGameLost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isDragging) {
                    return true;
                }

                // Check if the touch event is inside any piece
                List<Piece> piecesList = gl.getPiecesList();
                for (Piece p : piecesList){
                    if (p != null && p.didUserTouchMe(event.getX(), event.getY())) {
                        // Start dragging the piece
                        isDragging = true;
                        draggedPiece = p;
                        lastX = p.x;
                        lastY = p.y;
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (isDragging) {
                    // Stop dragging the piece
                    Square[][] squares = gl.getSquares();
                    isDragging = false;
                    for (int i = 0; i < BOARD_SIZE; i++) {
                        for (int j = 0; j < BOARD_SIZE; j++) {
                            if (squares[i][j].didXAndYInSquare(event.getX(), event.getY())) {
                                if (gl.checkValidMove(draggedPiece, i, j)) {
                                    draggedPiece.updatePosition(squares[i][j].getCenterX(), squares[i][j].getCenterY());
                                    lastX = 0;
                                    lastY = 0;
                                    draggedPiece = null;
                                    invalidate();
                                    return true;
                                } else {
                                    draggedPiece.updatePosition(lastX, lastY);
                                    lastX = 0;
                                    lastY = 0;
                                    draggedPiece = null;
                                    invalidate();
                                    return true;
                                }
                            }
                        }
                    }
                    draggedPiece.updatePosition(lastX, lastY);
                    lastX = 0;
                    lastY = 0;
                    draggedPiece = null;
                    invalidate();
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (isDragging && draggedPiece != null) {
                    draggedPiece.updatePosition(event.getX(), event.getY());
                    invalidate();
                    return true;
                }
                break;
        }
        return true;
    }

    public void drawBoard(Canvas canvas){
        Square[][] squares = gl.getSquares();
        canvas.drawColor(backGroundPaint.getColor());
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squares[i][j].draw(canvas);
            }
        }
    }

    public void drawPieces(Canvas canvas){
        List<Piece> piecesList = gl.getPiecesList();
        for (Piece p : piecesList){
            p.draw(canvas);
        }
    }

    public void drawTime(Canvas canvas){
        long timeRemain = gl.getTimeRemain();
        canvas.drawText("Time left: "+convertMilliseconds(timeRemain), screenWidth*0.1f, screenHeight*0.1f, timePaint);
    }

    public void drawTurn(Canvas canvas){
        if (gl.getDoesMyMove()){
            canvas.drawText("Your turn", screenWidth*0.35f, screenHeight*0.8f, turnPaint);
        }
    }

    public void drawGameLost(Canvas canvas){
        canvas.drawText("Game "+gl.gameOverStatus, screenWidth*0.35f, screenHeight*0.6f, turnPaint);
    }

    private String convertMilliseconds(long milliseconds){
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }
}

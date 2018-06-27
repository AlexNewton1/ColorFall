package com.softwareoverflow.colorfall.game_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.softwareoverflow.colorfall.game.Level;


public abstract class GameObject{

    final int screenX;
    int x = 0, y =0;
    float speed = 1;
    int panelWidth;
    private int bitmapSize;

    private Context context;

    private Bitmap bitmap;
    private Colour colour;

    GameObject(Context context,  int screenX, int numPanels){
        this.context = context;
        this.screenX = screenX;
        this.panelWidth = screenX / numPanels;
        this.bitmapSize = (int) (panelWidth * 0.8);

        //placeholder to init bitmap
        setColour(Colour.BLUE);
    }

    Colour getColour(){
        return colour;
    }

    Bitmap getBitmap() {
        return bitmap;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
        createBitmap(colour.getBitmapRef());
    }

    public int getY(){
        return y;
    }

    private void createBitmap(int bitmapRes){
        bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapRes);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapSize, bitmapSize, true);
    }


    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }

    /**
     * @param touchX - x position of the touch event
     * @param touchY - y position of the touch event
     * @return - true if the player touched within the bounds, false otherwise
     */
    public boolean isObjectTouched(float touchX, float touchY){
        return (touchX > x && touchX < x + getBitmap().getWidth()
                && touchY > y && touchY < y + getBitmap().getHeight());
    }

    /**
     * @param frameTime The time of the frame, used to maintain a smooth ui
     */
    public abstract void update(double frameTime);
    /**
     *
     * @return true if the piece scored a point, false otherwise
     */
    public abstract boolean didPieceScore(Level level);
    /**
     * reset the piece
     */
    public abstract void resetPiece(Level level);
    /**
     * @param endX - The xValue at the end of the swipe. Used to calculate which panel the ball
     *             should be moved to.
     */
    public abstract void onSwipe(float endX);
}
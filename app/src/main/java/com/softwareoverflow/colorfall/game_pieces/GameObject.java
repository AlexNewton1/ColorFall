package com.softwareoverflow.colorfall.game_pieces;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.softwareoverflow.colorfall.game.Level;


public abstract class GameObject{

    final int screenX;
    int x = 0, y =0;
    final float BASE_SPEED;
    float speed = 1;
    int panelWidth;

    private Bitmap bitmap;
    private Colour colour;

    GameObject(int screenX, int screenY, int numPanels){
        this.screenX = screenX;
        this.panelWidth = screenX / numPanels;
        BASE_SPEED = screenY / 90f; //30 FPS for 3 seconds is base speed

        //placeholder to init bitmap
        setColour(Colour.BLUE);
    }

    Colour getColour(){
        return colour;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    void setColour(Colour colour) {
        this.colour = colour;
        this.bitmap = Bitmaps.getBitmap(colour);
    }

    public int getY(){
        return y;
    }

    public int getX() { return x; }

    public void draw(Canvas canvas, int[] position){
        canvas.drawBitmap(bitmap, position[0], position[1], null);
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
     */
    public abstract void update();
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
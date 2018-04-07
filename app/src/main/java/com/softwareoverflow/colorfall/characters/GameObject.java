package com.softwareoverflow.colorfall.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.softwareoverflow.colorfall.Colour;


public abstract class GameObject{

    final int screenX, screenY;
    protected int x = 0, y =0, speed = 1;
    private Context context;


    private Bitmap bitmap;
    private Colour colour;

    public GameObject(Context context,  int screenX, int screenY){
        this.context = context;

        this.screenX = screenX;
        this.screenY = screenY;

        //placeholder to init bitmap
        setColour(Colour.BLUE);
    }

    public Colour getColour(){
        return colour;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
        createBitmap(colour.getBitmapRef());
    }

    private void createBitmap(int bitmapRes){
        bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapRes);
        bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
    }

    public abstract void update(double frameTime);

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
     * @param direction - either 1 or -1, for right and left directions respectively
     */
    abstract public void onSwipe(int direction);
}
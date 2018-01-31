package com.softwareoverflow.colorfall.characters;

import android.content.Context;

import com.softwareoverflow.colorfall.R;

public class Player extends GameObject{

    private int direction = 0;

    private final String TAG = "debug";

    public Player(Context context, int screenX, int screenY){
        super(context, R.drawable.player, screenX, screenY);

        x = 50;
        y = 50;
    }


    @Override
    public void update() {
        y += 20;
        if(y > screenY) y = -getBitmap().getHeight();
    }


    public boolean isObjectTouched(int touchX, int touchY){
        return (touchX > x && touchX < x + getBitmap().getWidth()
                && touchY > y && touchY < y + getBitmap().getHeight());
    }
}

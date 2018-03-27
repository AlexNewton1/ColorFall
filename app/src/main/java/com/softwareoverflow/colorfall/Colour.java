package com.softwareoverflow.colorfall;

import android.graphics.Color;

/**
 * Created by Alex on 20/03/2018.
 */

public enum Colour {

    BLUE (R.drawable.blue_ball, Color.BLUE),
    YELLOW (R.drawable.yellow_ball, Color.YELLOW),
    RED (R.drawable.red_ball, Color.RED);


    private int bitmap;
    private int colour;

    Colour(int bitmap, int colour) {
        this.bitmap = bitmap;
        this.colour = colour;
    }

    public int getBitmapRef(){
        return bitmap;
    }

    public int getColour(){
        return colour;
    }
}

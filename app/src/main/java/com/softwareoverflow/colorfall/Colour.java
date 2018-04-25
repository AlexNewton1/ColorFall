package com.softwareoverflow.colorfall;

/**
 * Created by Alex on 20/03/2018.
 */

public enum Colour {

    BLUE (R.drawable.blue_ball, R.color.blue),
    ORANGE (R.drawable.orange_ball, R.color.orange),
    PINK (R.drawable.pink_ball, R.color.pink),
    PURPLE(R.drawable.purple_ball, R.color.purple);


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

package com.softwareoverflow.colorfall.characters;

import com.softwareoverflow.colorfall.R;

/**
 * Created by Alex on 07/02/2018.
 */

public enum Ball {

    BLUE (R.drawable.blue_ball),
    RED (R.drawable.red_ball),
    YELLOW (R.drawable.yellow_ball);

    private int bitmapRef;

    Ball(int bitmapRef){
        this.bitmapRef = bitmapRef;
    }

    public int getBitmapRef() {
        return bitmapRef;
    }
}

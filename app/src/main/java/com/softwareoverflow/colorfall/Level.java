package com.softwareoverflow.colorfall;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum Level {

    EASY(3, 3, 50, 100), MEDIUM(3, 3, 75, 125), HARD(4, 4, 75, 125), INSANE(4, 6, 100, 150);

    private Colour[] colours;
    private int numPanels, numBalls, minSpeed, maxSpeed;

    Level(int numPanels, int numBalls, int minSpeed, int maxSpeed){
        this.numPanels = numPanels;
        this.numBalls = numBalls;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;

        colours = new Colour[numPanels];

        ArrayList<Colour> allColours = new ArrayList<>(Arrays.asList(Colour.values()));

        Random random = new Random();
        for(int i=0; i<numPanels; i++){
            Log.d("debug", "" + allColours.size());
            int index = random.nextInt(allColours.size());
            colours[i] = allColours.get(index);
            allColours.remove(index);
        }
    }

    public Colour[] getColours() {
        return colours;
    }

    public int getNumPanels() {
        return numPanels;
    }

    public int getNumBalls() {
        return numBalls;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }
}

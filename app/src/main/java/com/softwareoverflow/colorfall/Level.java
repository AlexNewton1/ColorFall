package com.softwareoverflow.colorfall;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum Level {

    EASY(3, 3, 50, 100), MEDIUM(3, 3, 75, 125), HARD(4, 4, 75, 125), INSANE(4, 6, 85, 150);

    private Colour[] colours;
    private int numPanels, numBalls, minSpeed, maxSpeed;
    private final int DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED;

    Level(int numPanels, int numBalls, int minSpeed, int maxSpeed){
        this.numPanels = numPanels;
        this.numBalls = numBalls;
        this.minSpeed = this.DEFAULT_MIN_SPEED =minSpeed;
        this.maxSpeed = this.DEFAULT_MAX_SPEED = maxSpeed;

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

    public void speedUp(){
        this.maxSpeed *= 1.2;
        this.minSpeed *= 1.2;
    }

    public void resetSpeed(){
        minSpeed = DEFAULT_MIN_SPEED;
        maxSpeed = DEFAULT_MAX_SPEED;
    }
}

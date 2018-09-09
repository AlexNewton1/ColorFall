package com.softwareoverflow.colorfall.game;

import com.softwareoverflow.colorfall.game_pieces.Colour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum Level {

    BEGINNER(3, 3, 1, 1.6f), EASY(3, 4, 1.2f, 2f), MEDIUM(3, 4, 1.4f, 2.2f), HARD(4, 4, 1.5f, 2.2f), INSANE(4, 6, 1.6f, 2.5f);

    private Colour[] colours;
    private int numPanels, numBalls;

    private float minSpeedMultiplier, maxSpeedMultiplier;
    private final float DEFAULT_MIN_SPEED_MULTIPLIER, DEFAULT_MAX_SPEED_MULTIPLIER;

    Level(int numPanels, int numBalls, float minSpeedMultiplier, float maxSpeedMultiplier){
        this.numPanels = numPanels;
        this.numBalls = numBalls;
        this.minSpeedMultiplier = this.DEFAULT_MIN_SPEED_MULTIPLIER = minSpeedMultiplier;
        this.maxSpeedMultiplier = this.DEFAULT_MAX_SPEED_MULTIPLIER = maxSpeedMultiplier;

        colours = new Colour[numPanels];
        setColours();
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

    public float getMinSpeedMultiplier() {
        return minSpeedMultiplier;
    }

    public float getMaxSpeedMultiplier() {
        return maxSpeedMultiplier;
    }

    public void speedUp(){
        this.maxSpeedMultiplier *= 1.2;
        this.minSpeedMultiplier *= 1.2;

        final float MAX_SPEED_MULTIPLIER_LIMIT = 6, MIN_SPEED_MULTIPLIER_LIMIT = 5;
        if(maxSpeedMultiplier > MAX_SPEED_MULTIPLIER_LIMIT)
            maxSpeedMultiplier = MAX_SPEED_MULTIPLIER_LIMIT;
        if(minSpeedMultiplier > MIN_SPEED_MULTIPLIER_LIMIT)
            minSpeedMultiplier = MIN_SPEED_MULTIPLIER_LIMIT;
    }

    public void resetSpeed(){
        minSpeedMultiplier = DEFAULT_MIN_SPEED_MULTIPLIER;
        maxSpeedMultiplier = DEFAULT_MAX_SPEED_MULTIPLIER;
    }

    public void setColours(){
        ArrayList<Colour> allColours = new ArrayList<>(Arrays.asList(Colour.values()));

        Random random = new Random();
        for(int i=0; i<numPanels; i++){
            int index = random.nextInt(allColours.size());
            colours[i] = allColours.get(index);
            allColours.remove(index);
        }
    }
}

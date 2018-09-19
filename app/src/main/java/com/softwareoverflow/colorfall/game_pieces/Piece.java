package com.softwareoverflow.colorfall.game_pieces;

import com.softwareoverflow.colorfall.game.Level;

import java.util.Random;

public class Piece extends GameObject{

    private Random random;

    private int targetX;
    private float speedX;

    private int lerpX, lerpY;

    public Piece(int screenX, int screenY, int numPanels){
        super(screenX, screenY, numPanels);

        speedX = panelWidth / 3;
        random = new Random();
    }

    public int[] interpolate(double interpolation){
        lerpY = y + (int) (speed * interpolation);
        lerpX = x;
        if(targetX != x){
            int direction = (targetX > x) ? 1 : -1;
            lerpX = x + (int) (direction * speedX * interpolation);

            if(direction * (x - targetX) > 0) {
                lerpX = targetX;
            }
        }

        return new int[] {lerpX, lerpY};
    }

    /**
     * Set the position of the piece to the value it was linearly interpolated to as of the last frame.
     * This removes the unpleasant visuals of the ball moving back up slightly when the game is resumed.
     */
    public void setPositionToLerp(){
        x = lerpX;
        y = lerpY;
    }


    @Override
    public void update() {
        y += speed;

        if(targetX != x){
            int direction = (targetX > x) ? 1 : -1;
            x += direction * speedX;

            if(direction * (x - targetX) > 0) {
                x = targetX;
            }
        }
    }

    @Override
    public boolean didPieceScore(Level level){
        int panel = targetX / panelWidth;
        int panelColour = level.getColours()[panel].getColour();
        int pieceColour = this.getColour().getColour();

        return panelColour == pieceColour;
    }

    @Override
    public void onSwipe(float endX) {
        if(endX == screenX) endX--;

        int panelNum = (int) (endX / panelWidth);
        targetX = getPxFromPanelNum(panelNum);
    }

    public void resetPiece(Level level){
        int index = random.nextInt(level.getColours().length);
        setColour(level.getColours()[index]);

        int start = random.nextInt(3);
        targetX = x = getPxFromPanelNum(start);

        float maxSpeedMultiplier = level.getMaxSpeedMultiplier();
        float minSpeedMultiplier = level.getMinSpeedMultiplier();

        float randSpeedMultiplier = (maxSpeedMultiplier - minSpeedMultiplier)
                * random.nextFloat() + minSpeedMultiplier;
        speed = BASE_SPEED * randSpeedMultiplier;

        int minStartY = (int) (BASE_SPEED * minSpeedMultiplier * 50) + getBitmap().getHeight();
        int maxStartY = (int) (BASE_SPEED * maxSpeedMultiplier * 100);
        int startY = (random.nextInt(maxStartY - minStartY) + minStartY);
        y = -random.nextInt(startY) - getBitmap().getHeight();
    }

    private int getPxFromPanelNum(int panelNum){
        if(panelNum < 0 ) panelNum = 0;
        else if(panelNum >= (screenX / panelWidth)) panelNum = (screenX / panelWidth) - 1;
        return (int) (panelWidth * (panelNum + 0.5) - getBitmap().getWidth() / 2);
    }
}

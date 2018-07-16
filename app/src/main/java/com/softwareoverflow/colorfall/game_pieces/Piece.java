package com.softwareoverflow.colorfall.game_pieces;

import com.softwareoverflow.colorfall.game.Level;

import java.util.Random;

public class Piece extends GameObject{

    private Random random;

    private int targetX;
    private float speedX;

    public Piece(int screenX, int numPanels){
        super(screenX, numPanels);

        speedX = panelWidth / 20;
        random = new Random();
    }


    @Override
    public void update(double frameTime) {
        y += speed * frameTime;

        if(targetX != x){
            int direction = (targetX > x) ? 1 : -1;
            x += direction * speedX * frameTime;

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
        int panelNum = (int) (endX / panelWidth);
        targetX = getPxFromPanelNum(panelNum);
    }

    public void resetPiece(Level level){
        int index = random.nextInt(level.getColours().length);
        setColour(level.getColours()[index]);

        int start = random.nextInt(3);
        targetX = x = getPxFromPanelNum(start);

        int maxSpeed = level.getMaxSpeed();
        int minSpeed = level.getMinSpeed();
        speed = (random.nextInt(maxSpeed - minSpeed) + minSpeed) / 100f;

        int minStartY = minSpeed * 20;
        int maxStartY = maxSpeed * 50;
        int startY = (random.nextInt(maxStartY - minStartY) + minStartY);
        y = -random.nextInt(startY) - getBitmap().getHeight();
    }

    private int getPxFromPanelNum(int panelNum){
        if(panelNum < 0 ) panelNum = 0;
        else if(panelNum > screenX / panelWidth) panelNum = (screenX / panelWidth) - 1;
        return (int) (panelWidth * (panelNum + 0.5) - getBitmap().getWidth() / 2);
    }
}

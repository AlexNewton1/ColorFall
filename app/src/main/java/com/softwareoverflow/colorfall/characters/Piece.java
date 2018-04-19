package com.softwareoverflow.colorfall.characters;

import android.content.Context;

import com.softwareoverflow.colorfall.Colour;

import java.util.Random;

public class Piece extends GameObject{

    private Random random;

    private int targetX;
    private int panelWidth;
    private float speedX;

    public Piece(Context context, int screenX){
        super(context, screenX);
        panelWidth = screenX / 3;
        speedX = panelWidth / 50;
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
    public boolean didPieceScore(Colour[] colours){
        int panel = targetX / panelWidth;
        int panelColour = colours[panel].getColour();
        int pieceColour = this.getColour().getColour();

        return panelColour == pieceColour;
    }

    @Override
    public void onSwipe(float endX) {
        int panelNum = (int) (endX / panelWidth);
        targetX = getPxFromPanelNum(panelNum);
    }

    public void resetPiece(Colour[] colours){
        int index = random.nextInt(colours.length);
        setColour(colours[index]);

        int start = random.nextInt(3);
        targetX = x = getPxFromPanelNum(start);

        int maxSpeed = 100;
        int minSpeed = 30;
        speed = (random.nextInt(maxSpeed - minSpeed) + minSpeed) / 100f;

        int minStartY = 1000;
        int maxStartY = 2000;
        int startY = (random.nextInt(maxStartY - minStartY) + minStartY);
        y = -random.nextInt(startY) - getBitmap().getHeight();
    }

    private int getPxFromPanelNum(int panelNum){
        return (int) (panelWidth * (panelNum + 0.5) - getBitmap().getWidth() / 2);
    }
}

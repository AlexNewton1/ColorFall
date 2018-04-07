package com.softwareoverflow.colorfall.characters;

import android.content.Context;

import com.softwareoverflow.colorfall.GameThread;
import com.softwareoverflow.colorfall.GameView;

import java.util.Random;

public class Piece extends GameObject{

    private Random random;

    private int targetX;
    private int panelWidth;
    private float speedX;

    public Piece(Context context, int screenX, int screenY){
        super(context, screenX, screenY);

        panelWidth = screenX / 3;
        speedX = panelWidth / 50;

        random = new Random();
        y = screenY;
    }


    @Override
    public void update(double frameTime) {
        y += speed * frameTime;
        if(y > screenY) {
            checkIfScored();
            resetPiece();
        }

        if(targetX != x){
            int direction = (targetX > x) ? 1 : -1;
            x += direction * speedX * frameTime;

            if(direction * (x - targetX) > 0) {
                x = targetX;
            }
        }
    }

    private void checkIfScored(){
        int panel = targetX / panelWidth;
        int panelColour = GameView.colours[panel].getColour();
        int pieceColour = this.getColour().getColour();

         if(panelColour == pieceColour)
             GameThread.playerScored();
         else
             GameThread.playerLostLife();
    }

    @Override
    public void onSwipe(int direction) {
        targetX += direction * panelWidth;

        if(targetX < 0 || targetX > screenX) { targetX = x; }
        if(targetX > screenX) { targetX = x; }
    }

    private void resetPiece(){
        int index = random.nextInt(GameView.colours.length);
        setColour(GameView.colours[index]);

        int start = random.nextInt(3);
        targetX = x = (int) (panelWidth * (start + 0.5) - getBitmap().getWidth() / 2);

        int maxSpeed = 2;
        int minSpeed = 1;
        speed = random.nextInt(maxSpeed - minSpeed) + minSpeed;


        int maxY = speed * 5000;
        y = -random.nextInt(maxY) - getBitmap().getHeight();

        GameView.movePieceToFront(this);
    }
}

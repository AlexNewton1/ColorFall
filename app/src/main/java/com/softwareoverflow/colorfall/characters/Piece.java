package com.softwareoverflow.colorfall.characters;

import android.content.Context;

import com.softwareoverflow.colorfall.GameThread;
import com.softwareoverflow.colorfall.GameView;

import java.util.Random;

public class Piece extends GameObject{

    private Random random;

    private int targetX;
    private int panelWidth;
    private int xPerFrame;

    public Piece(Context context, int screenX, int screenY){
        super(context, screenX, screenY);

        panelWidth = screenX / 3;
        xPerFrame = panelWidth / 3;

        random = new Random();
        y = screenY;
    }


    @Override
    public void update() {
        y += speed;
        if(y > screenY) {
            int index = random.nextInt(GameView.balls.length);
            setBitmap(GameView.balls[index].getBitmapRef());

            resetPiece();
        }

        if(targetX != x){
            int direction = (targetX > x) ? 1 : -1;

            x += direction * xPerFrame;
        }
    }

    @Override
    public void onSwipe(int direction) {
        targetX += direction * panelWidth;

        if(targetX < 0) { targetX += screenX; }
        if(targetX > screenX) { targetX -= screenX; }
    }

    private void resetPiece(){
        int start = random.nextInt(3);
        targetX = x = (int) (panelWidth * (start + 0.5) - getBitmap().getWidth() / 2);

        int maxSpeed = 45;
        int minSpeed = 15;
        speed = random.nextInt(maxSpeed - minSpeed) + minSpeed;


        int maxY = speed * 2 * GameThread.TARGET_FPS;
        y = -random.nextInt(maxY) - getBitmap().getHeight();



    }
}

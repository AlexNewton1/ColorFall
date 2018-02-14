package com.softwareoverflow.colorfall;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.softwareoverflow.colorfall.characters.Ball;
import com.softwareoverflow.colorfall.characters.GameObject;
import com.softwareoverflow.colorfall.characters.Piece;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;

    private float x1, y1;
    private GameObject touchedObject;
    private long downTime = 0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_MAX_TIME = 1200;

    public static final Ball[] balls = { Ball.BLUE, Ball.RED, Ball.YELLOW };


    private ArrayList<GameObject> gameObjects = new ArrayList<>();


    public GameView(Context context, int screenX, int screenY) {
        super(context);

        Random random = new Random();

        for(int i=0; i < 3; i++){
            gameObjects.add(new Piece(context, screenX, screenY));

            int index = random.nextInt(balls.length);
            gameObjects.get(i).setBitmap(balls[index].getBitmapRef());
        }


        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("debug", "EVENT: "  + "" + event.getAction());

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                downTime = System.nanoTime();
                x1 = event.getX();
                y1 = event.getY();

                touchedObject = null;
                for(GameObject gameObject : gameObjects){
                    if(gameObject.isObjectTouched(x1, y1)){
                        touchedObject = gameObject;
                        break;
                    }
                }

                return true;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float y2 = event.getY();

                float deltaX = x2 - x1;
                float deltaY = y2 - y1;

                float deltaT = (System.nanoTime() - downTime) / 1000000;

                if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE &&
                        Math.abs(deltaY) < SWIPE_MAX_OFF_PATH && deltaT < SWIPE_MAX_TIME) {

                    Log.d("debug","OBJ: " +  touchedObject);
                    if(touchedObject != null){
                        Log.d("debug", "Updating obj");
                        touchedObject.onSwipe(deltaX > 0 ? 1 : -1);
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                gameThread.setRunning(false);
                gameThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        for(GameObject gameObject : gameObjects){
            gameObject.update();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(canvas!=null) {
            for(GameObject gameObject : gameObjects){
                gameObject.draw(canvas);
            }
        }
    }
}

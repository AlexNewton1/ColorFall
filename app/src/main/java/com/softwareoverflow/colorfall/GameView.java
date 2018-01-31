package com.softwareoverflow.colorfall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.softwareoverflow.colorfall.characters.GameObject;
import com.softwareoverflow.colorfall.characters.Player;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private long lastFrameTime;
    private final int MAX_FPS = 60;

    private GestureDetectorCompat gestureDetector = null;


    private ArrayList<GameObject> gameObjects = new ArrayList<>();


    public GameView(Context context, int screenX, int screenY) {
        super(context);

        if(gestureDetector == null){
            gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        }

        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setColor(Color.WHITE);

        gameObjects.add(new Player(context, screenX, screenY));
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            controlFPS();
        }
    }

    private void update() {
        for(GameObject gameObject : gameObjects){
            gameObject.update();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            for(GameObject gameObject : gameObjects){
                gameObject.draw(canvas, paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
        long timeToSleep = (1000 / MAX_FPS) - timeThisFrame;

        if (timeToSleep > 0) {
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        lastFrameTime = System.currentTimeMillis();
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }



    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Do flingy logic here!!
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}

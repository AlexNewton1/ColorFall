package com.softwareoverflow.colorfall.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private GameView gameView;
    private final SurfaceHolder surfaceHolder;
    private volatile boolean running;
    private Canvas canvas;

    private static final int TARGET_FPS = 40;
    private static final long TARGET_FRAME_TIME = 1000 / TARGET_FPS;


    GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;

        this.gameView = gameView;
        this.gameView = gameView;
    }


    @Override
    public void run() {

        long previousFrameTime = System.currentTimeMillis();


        while (running) {

            long currentTime = System.currentTimeMillis();
            long frameTime = currentTime - previousFrameTime;
            previousFrameTime = currentTime;

            gameView.update(frameTime);

            long sleep = TARGET_FRAME_TIME - frameTime;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            updateCanvas();
        }

    }

    public void updateCanvas(){
        canvas = null;
        try {

            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                gameView.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                try {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
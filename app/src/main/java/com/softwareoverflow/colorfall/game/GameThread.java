package com.softwareoverflow.colorfall.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private GameView gameView;
    private final SurfaceHolder surfaceHolder;
    private volatile boolean running;
    private Canvas canvas;


    GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;

        this.gameView = gameView;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        final double MS_PER_UPDATE = 1000/30f;
        double previous = System.currentTimeMillis();
        double accumulator = 0.0;

        while (running)
        {
            double current = System.currentTimeMillis();
            double elapsed = current - previous;
            previous = current;
            accumulator += elapsed;

            while (accumulator >= MS_PER_UPDATE)
            {
                gameView.update();
                accumulator -= MS_PER_UPDATE;
            }

            updateCanvas(accumulator / MS_PER_UPDATE);
        }
    }

    public void updateCanvas(double interpolation){
        canvas = null;
        try {

            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                gameView.draw(canvas, interpolation);
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
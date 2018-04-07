package com.softwareoverflow.colorfall;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private static GameView gameView;
    private final SurfaceHolder surfaceHolder;
    private boolean running;

    private static final int TARGET_FPS = 40;
    private static final long TARGET_FRAME_TIME = 1000 / TARGET_FPS;


    GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;

        GameThread.gameView = gameView;
    }


    @Override
    public void run()
    {
        Canvas canvas;

        long previousFrameTime = System.currentTimeMillis();

        while(running) {
            long currentTime=System.currentTimeMillis();
            long frameTime = currentTime - previousFrameTime;
            previousFrameTime=currentTime;

            gameView.update(frameTime);

            long sleep = TARGET_FRAME_TIME - frameTime;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch(InterruptedException e) {}
            }

            canvas = null;
            try {

                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                if(canvas!=null)
                {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static void playerScored(){
        ((Activity) gameView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameView.playerScored();
            }
        });
    }

    public static void playerLostLife() {
        ((Activity) gameView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameView.playerLostLife();
            }
        });
    }

    void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
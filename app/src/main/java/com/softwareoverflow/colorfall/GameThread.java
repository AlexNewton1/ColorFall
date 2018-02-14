package com.softwareoverflow.colorfall;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Alex on 07/02/2018.
 */

public class GameThread extends Thread {
    private GameView gameView;
    private final SurfaceHolder surfaceHolder;
    private boolean running;
    private static Canvas canvas;

    public static final int TARGET_FPS = 30;


    GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run()
    {

        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount =0;
        long targetTime = 1000/TARGET_FPS;


        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.update();
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

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime-timeMillis;

            if(waitTime < 2) waitTime = 2;

            try{
                Thread.sleep(waitTime);
            }catch(Exception e){
                e.printStackTrace();
            }

            totalTime += System.nanoTime()-startTime;
            frameCount++;
            if(frameCount == TARGET_FPS)
            {
                double averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount =0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }

    }

    void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
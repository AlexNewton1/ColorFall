package com.softwareoverflow.colorfall;

import android.view.MotionEvent;

import com.softwareoverflow.colorfall.characters.GameObject;

import java.util.concurrent.CopyOnWriteArrayList;

public class TouchEventHandler {

    private float x1, y1;
    private GameObject touchedObject;
    private long downTime = 0;
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_MAX_TIME = 1200;

    private GameView gameView;

    TouchEventHandler(GameView gameView){
        this.gameView = gameView;
    }

    public boolean handleEvent(MotionEvent event){

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.nanoTime();
                x1 = event.getX();
                y1 = event.getY();

                touchedObject = null;

                CopyOnWriteArrayList<GameObject> gameObjects = gameView.getGameObjects();

                for (int i = gameObjects.size() -1; i >= 0; i--) {
                    GameObject gameObject = gameObjects.get(i);
                    if (gameObject.isObjectTouched(x1, y1)) {
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

                    if (touchedObject != null) {
                        touchedObject.onSwipe(x2);
                    }
                }
                return true;
        }
        return false;

    }
}

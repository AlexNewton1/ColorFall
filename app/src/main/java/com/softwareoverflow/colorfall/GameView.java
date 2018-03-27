package com.softwareoverflow.colorfall;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.softwareoverflow.colorfall.characters.GameObject;
import com.softwareoverflow.colorfall.characters.Piece;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;

    private int screenX, screenY;
    private static int score = 0;
    private TextView scoreTextView;
    private Context context;

    private float x1, y1;
    private GameObject touchedObject;
    private long downTime = 0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_MAX_TIME = 1200;

    public static final Colour[] colours = {Colour.BLUE, Colour.RED, Colour.YELLOW};

    private Paint paint = new Paint();


    private static ArrayList<GameObject> gameObjects = new ArrayList<>();


    public GameView(Context context) {
        super(context);
        setup(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    @TargetApi(21)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context);
    }

    private void setup(Context context){

        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        setFocusable(true);
    }

    private void addGameObjects(Context context, int numObjects) {
        Random random = new Random();
        for (int i = 0; i < numObjects; i++) {
            gameObjects.add(new Piece(context, screenX, screenY));

            int index = random.nextInt(colours.length);
            gameObjects.get(i).setColour(colours[index]);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("debug", "EVENT: " + "" + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.nanoTime();
                x1 = event.getX();
                y1 = event.getY();

                touchedObject = null;
                for (int i = gameObjects.size() - 1; i >= 0; i--) {
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

                    Log.d("debug", "OBJ: " + touchedObject);
                    if (touchedObject != null) {
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
        Log.d("debug", holder.getSurfaceFrame().width() + ", " + holder.getSurfaceFrame().height());
        screenX = holder.getSurfaceFrame().width();
        screenY = holder.getSurfaceFrame().height();

        scoreTextView = ((View) getParent()).findViewById(R.id.scoreTextView);
        Log.d("debug", "DAS TEXT VIEW: " + scoreTextView);

        addGameObjects(this.getContext(), 3);

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

    public void update(double frameTime) {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(frameTime);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            for(int i=0; i<colours.length; i++){
                paint.setColor(colours[i].getColour());
                canvas.drawRect(screenX / 3 * i, 0, screenX / 3 * (i + 1), screenY, paint);
            }


            for (GameObject gameObject : gameObjects) {
                gameObject.draw(canvas);
            }
        }
    }

    //CURRENTLY UNIMPLEMENTED!
    public static void movePieceToFront(GameObject piece) {
    }

    public void playerScored(){
        score++;
        scoreTextView.setText(String.valueOf(score));
    }

    public void resume() {
        if(gameThread != null){
            gameThread.setRunning(true);
            gameThread = new GameThread(getHolder(), this);
        }

    }
}

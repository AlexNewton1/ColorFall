package com.softwareoverflow.colorfall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.softwareoverflow.colorfall.activities.EndGameActivity;
import com.softwareoverflow.colorfall.characters.GameObject;
import com.softwareoverflow.colorfall.characters.Piece;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread = null;
    private Activity gameActivity;
    private TouchEventHandler touchEventHandler;

    //UI items
    private int screenX, screenY;
    private static int score = -1, lives = 3;
    private TextView scoreTextView, livesTextView;

    Level level;

    private Paint paint = new Paint();


    private static CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();


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
        gameActivity = ((Activity) context);

        gameObjects.clear();
        lives = 3;
        score = 0;

        touchEventHandler = new TouchEventHandler(this);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public void setLevel(Level level){
        this.level = level;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return touchEventHandler.handleEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenX = holder.getSurfaceFrame().width();
        screenY = holder.getSurfaceFrame().height();

        scoreTextView = ((View) getParent()).findViewById(R.id.scoreTextView);
        livesTextView = ((View) getParent()).findViewById(R.id.livesTextView);
        livesTextView.setText(String.valueOf(lives));

        if(gameObjects.isEmpty()){
            addGameObjects(this.getContext());
        }

        scoreTextView.setText(String.valueOf(score));
        livesTextView.setText(String.valueOf(lives));

        gameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    private void addGameObjects(Context context) {
        gameObjects.clear();

        for (int i = 0; i < level.getNumBalls(); i++) {
            Piece piece = new Piece(context, screenX, level.getNumPanels());
            piece.resetPiece(level);
            gameObjects.add(piece);
        }
    }

    public CopyOnWriteArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }

    public void update(double frameTime) {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(frameTime);

            if(gameObject.getY() > screenY) {
                if(gameObject.didPieceScore(level)){
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerScored();
                        }
                    });
                } else {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerLostLife();
                        }
                    });
                }

                //bring to front when looping
                gameObjects.remove(gameObject);
                gameObjects.add(gameObject);

                gameObject.resetPiece(level);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            int panelWidth = screenX / level.getNumPanels();

            for(int i=0; i<level.getColours().length; i++){
                int colour = ContextCompat.getColor(gameActivity, level.getColours()[i].getColour());
                paint.setColor(colour);
                canvas.drawRect(panelWidth * i, 0, panelWidth * (i + 1), screenY, paint);
            }

            for (GameObject gameObject : gameObjects) {
                gameObject.draw(canvas);
            }
        }

    }

    public void playerScored(){
        score++;
        scoreTextView.setText(String.valueOf(score));
    }

    public void playerLostLife() {
        lives--;
        livesTextView.setText(String.valueOf(lives));

        if(lives <= 0){
            gameActivity.startActivity(new Intent(gameActivity, EndGameActivity.class));
            gameActivity.finish();
        }
    }

    public void onResume() {
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
    }

    public void onPause(){
        //stop the thread
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
}

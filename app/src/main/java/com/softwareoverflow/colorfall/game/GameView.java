package com.softwareoverflow.colorfall.game;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.activities.EndGameActivity;
import com.softwareoverflow.colorfall.activities.GameActivity;
import com.softwareoverflow.colorfall.animations.CountdownAnimation;
import com.softwareoverflow.colorfall.free_trial.FreeTrialCountdown;
import com.softwareoverflow.colorfall.game_pieces.Bitmaps;
import com.softwareoverflow.colorfall.game_pieces.GameObject;
import com.softwareoverflow.colorfall.game_pieces.Piece;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    //Game Items
    private Tutorial tutorial;
    private GameThread gameThread = null;
    private GameActivity gameActivity;
    private TouchEventHandler touchEventHandler;
    private SoundEffectHandler soundEffectHandler;
    private static CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private Level level;
    private Paint paint = new Paint();

    //UI items
    private int screenX, screenY;
    private static int score = -1, lives = 3;
    private int oldHiScore;
    private TextView scoreTextView, livesTextView;

    //Pause screen items
    ImageView countdownTimer;
    private ConstraintLayout pauseLayout;
    private boolean isPaused;

    //Free trial items
    private boolean isFreeTrial;
    private TextView freeTrialCountdownTV;
    private FreeTrialCountdown trialCountdown;



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
        soundEffectHandler = SoundEffectHandler.getInstance(context);

        gameActivity = ((GameActivity) context);

        gameObjects.clear();
        lives = 3;
        score = 0;

        touchEventHandler = new TouchEventHandler(this);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public void setFreeTrial(boolean freeTrial, TextView countdownTV){
        this.isFreeTrial = freeTrial;
        this.freeTrialCountdownTV = countdownTV;
        trialCountdown = new FreeTrialCountdown(countdownTV, gameActivity);
    }

    public void setLevel(Level level, Context context){
        this.level = level;

        SharedPreferences sharedPrefs = context.getSharedPreferences("scores", Context.MODE_PRIVATE);
        oldHiScore = sharedPrefs.getInt(level.name(), 0);
    }

    public Tutorial getTutorial(){
        return tutorial;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return isPaused || touchEventHandler.handleEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenX = holder.getSurfaceFrame().width();
        screenY = holder.getSurfaceFrame().height();

        int bitmapSize = (int) (screenX / level.getNumPanels() * 0.8);
        Bitmaps.initialize(gameActivity, bitmapSize);

        pauseLayout = ((View) getParent()).findViewById(R.id.game_paused_view);
        countdownTimer = ((View) getParent()).findViewById(R.id.countdown_image_view);
        scoreTextView = ((View) getParent()).findViewById(R.id.scoreTextView);
        livesTextView = ((View) getParent()).findViewById(R.id.livesTextView);
        livesTextView.setText(String.valueOf(lives));

        if(gameObjects.isEmpty()){
            addGameObjects();
        }

        scoreTextView.setText(String.valueOf(score));
        livesTextView.setText(String.valueOf(lives));

        if(isPaused) {
            pauseLayout.setVisibility(VISIBLE);
        } else {
            startCountdown();
        }

        boolean showTutorial = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("tutorial", true);
        if(showTutorial && level == Level.BEGINNER && tutorial == null){
            tutorial = new Tutorial(gameActivity, this, gameObjects);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    private void addGameObjects() {
        gameObjects.clear();

        for (int i = 0; i < level.getNumBalls(); i++) {
            Piece piece = new Piece(screenX, screenY, level.getNumPanels());
            piece.resetPiece(level);
            gameObjects.add(piece);
        }
    }

    public CopyOnWriteArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }

    public void update() {
        for (GameObject gameObject : gameObjects) {
            gameObject.update();

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

    public void draw(Canvas canvas, double interpolation) {
        super.draw(canvas);
        if (canvas != null) {
            int panelWidth = screenX / level.getNumPanels();

            for(int i=0; i<level.getColours().length; i++){
                int colour = ContextCompat.getColor(gameActivity,
                        level.getColours()[i].getColour());
                paint.setColor(colour);
                canvas.drawRect(panelWidth * i, 0, panelWidth * (i + 1),
                        screenY, paint);
            }


            for (GameObject gameObject : gameObjects) {
                int[] position =  ((Piece) gameObject).interpolate(interpolation);
                gameObject.draw(canvas, position);
            }
        }

    }

    public void playerScored(){
        score++;
        scoreTextView.setText(String.valueOf(score));

        int levelUpInterval = 25;
        if(score % levelUpInterval == 0){
            soundEffectHandler.playSound(SoundEffectHandler.Sound.LEVEL_UP);
            level.speedUp();
        } else {
            soundEffectHandler.playSound(SoundEffectHandler.Sound.SCORE);
        }

        if(score == oldHiScore + 1){
            soundEffectHandler.newHiScore();
        }
    }

    public void playerLostLife() {
        lives--;
        livesTextView.setText(String.valueOf(lives));

        if(lives <= 0){
            soundEffectHandler.playSound(SoundEffectHandler.Sound.GAME_OVER);
            endGame();
        } else {
            soundEffectHandler.playSound(SoundEffectHandler.Sound.LOSE_LIFE);
        }
    }

    /**
     * End the game. Sends the user to the EndGameActivity
     */
    public void endGame(){
        Intent endGameIntent = new Intent(gameActivity, EndGameActivity.class);
        endGameIntent.putExtra("score", score);
        endGameIntent.putExtra("difficulty", level.name());
        gameActivity.startActivity(endGameIntent);

        gameActivity.finish();
        FreeTrialCountdown.reset();
    }

    /**
     * Start the countdown. Used at the start or on resuming the game.
     */
    public void startCountdown(){
        pauseLayout.setVisibility(GONE);
        gameThread.updateCanvas(1);
        countdownTimer.setVisibility(VISIBLE);

        float scaleX = screenX / 2;
        float scaleY = screenY / 2;
        CountdownAnimation countdownAnimation = new CountdownAnimation(countdownTimer, scaleX, scaleY,
                this);
        countdownAnimation.start();
    }

    /**
     * Start the game and start the free trial countdown if needed
     */
    public void startGame(){
        isPaused = false;
        pauseLayout.setVisibility(GONE);
        countdownTimer.setVisibility(GONE);


        if(tutorial != null){
            Log.d("debug2", "startGame - tutorial resume");
            tutorial.resume();
        }

        if(tutorial == null || !tutorial.isCurrentlyShowing) {
            Log.d("debug2", "StartGame run");
            for(GameObject gameObject : gameObjects){
                //remove unpleasant jitter on resume
                ((Piece) gameObject).setPositionToLerp();
            }

            gameThread.setRunning(true);

            if(!gameThread.isAlive()){
                gameThread.start();
            }

            if(isFreeTrial){
                if(trialCountdown != null)
                    trialCountdown.cancel();

                trialCountdown = new FreeTrialCountdown(freeTrialCountdownTV, gameActivity);
                trialCountdown.start();
            }
        }
    }

    public void pauseGame(){
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

        for(GameObject gameObject : gameObjects){
            ((Piece) gameObject).setPositionToLerp();
        }
    }


    /**
     * Resume the game - hide the pause layout and show the countdown
     */
    public void onResume() {
        gameThread = new GameThread(getHolder(), this);
        if(isPaused){
            pauseLayout.setVisibility(VISIBLE);
            countdownTimer.setVisibility(GONE);
        } else {
            gameThread.setRunning(true);
        }
    }

    /**
     * Pause the game - show the pause screen.
     */
    public void onPause(){
        pauseGame();

        CountdownAnimation.setInCountdown(false);

        if(isFreeTrial && trialCountdown != null){
            trialCountdown.cancel();
        }

        //don't need to pause the tutorial if we are changing activity
        if(tutorial != null){
            tutorial.pause();
        }

        isPaused = true;
    }
}
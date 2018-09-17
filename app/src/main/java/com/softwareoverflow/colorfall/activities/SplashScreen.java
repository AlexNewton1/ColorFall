package com.softwareoverflow.colorfall.activities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.free_trial.AdvertHandler;
import com.softwareoverflow.colorfall.free_trial.UpgradeManager;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class SplashScreen extends AppCompatActivity {

    private BackgroundLoader loader;

    private int loadingTextUpdateCount;
    private final int LOADING_UPDATE_FREQUENCY = 500;
    private boolean animationFinished, loadingFinished;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loaderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    private void setupLoadingScreen() {
        UpgradeManager.getInstance(SplashScreen.this);
        new AdvertHandler().setupGameBanner(SplashScreen.this);

        View backgroundOverlay = findViewById(R.id.splash_screen_background_overlay);
        final TextView loadingTV = findViewById(R.id.loading_text_view);
        final String baseText = getResources().getString(R.string.add_color);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;


        Animation animation = new TranslateAnimation(0, 0, 0, screenHeight);
        animation.setStartOffset(LOADING_UPDATE_FREQUENCY);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animationFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        backgroundOverlay.startAnimation(animation);


        loaderRunnable = new Runnable() {
            @Override
            public void run() {
                loadingTextUpdateCount++;
                StringBuilder sb = new StringBuilder().append(baseText);
                for (int i = 0; i < loadingTextUpdateCount % 4; i++) {
                    sb.append(" .");
                }
                loadingTV.setText(sb.toString());

                if(loadingFinished && animationFinished){
                    startGame();
                } else {
                    handler.postDelayed(loaderRunnable, LOADING_UPDATE_FREQUENCY);
                }
            }
        };
        handler.postDelayed(loaderRunnable, LOADING_UPDATE_FREQUENCY);
    }

    private void startGame(){
        BackgroundMusicService.changingActivity = true;
        startActivity(new Intent(this, MainActivity.class));

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loader.cancel(true);
        handler.removeCallbacks(loaderRunnable);
        try {
            handler.getLooper().quit();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }

        if (!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationFinished = loadingFinished = false;
        loader = new BackgroundLoader(SplashScreen.this);
        loader.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class BackgroundLoader extends AsyncTask<Void, Void, Void> {

        private Application application;

        private BackgroundLoader(Context context) {
            this.application = (Application) context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupLoadingScreen();
                }
            });

            //set the preferences
            SharedPreferences sharedPreferences = application.getSharedPreferences("settings", MODE_PRIVATE);
            BackgroundMusicService.setPlayMusic(sharedPreferences.getBoolean(
                    "music", true));
            SoundEffectHandler.setPlaySounds(sharedPreferences.getBoolean(
                    "sounds", true));

            //start the background music
            startService(new Intent(application, BackgroundMusicService.class));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingFinished = true;
        }
    }
}

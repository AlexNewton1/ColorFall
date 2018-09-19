package com.softwareoverflow.colorfall.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.free_trial.AdvertHandler;
import com.softwareoverflow.colorfall.free_trial.UpgradeManager;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class SplashScreen extends AppCompatActivity {

    private BackgroundLoader loader;

    private ImageView background;
    private  TextView loadingTV;

    private int loadingTextUpdateCount;
    private final int LOADING_UPDATE_FREQUENCY = 500;
    private boolean animationFinished, loadingFinished;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loaderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loadingTV = findViewById(R.id.loading_ellipsis);
        background = findViewById(R.id.splash_screen_background_img);
    }

    private void setupLoadingScreen() {
        loadingTextUpdateCount = 0;
        animationFinished = loadingFinished = false;

        UpgradeManager.setup(SplashScreen.this);
        if(UpgradeManager.isFreeUser())
            new AdvertHandler().setupGameBanner(SplashScreen.this);

        final ColorMatrix matrix = new ColorMatrix();
        final Drawable drawable = background.getDrawable();

        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(2500);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                matrix.setSaturation(animation.getAnimatedFraction());
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                drawable.setColorFilter(filter);

                if(animation.getAnimatedFraction() == 1){
                    animationFinished = true;
                }
            }

        });
        animation.start();

        loaderRunnable = new Runnable() {
            @Override
            public void run() {
                updateLoadingText();

                if(loadingFinished && animationFinished){
                    startGame();
                } else {
                    handler.postDelayed(this, LOADING_UPDATE_FREQUENCY);
                }
            }
        };

        handler.postDelayed(loaderRunnable, LOADING_UPDATE_FREQUENCY);
    }

    private void updateLoadingText(){
        loadingTextUpdateCount++;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < loadingTextUpdateCount % 4; i++) {
            sb.append(". ");
        }
        loadingTV.setText(sb.toString());
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

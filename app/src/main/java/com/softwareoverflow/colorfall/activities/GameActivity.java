package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.softwareoverflow.colorfall.GameView;
import com.softwareoverflow.colorfall.Level;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class GameActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_screen);

        //default value
        Level level  = Level.EASY;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String levelDifficulty = extras.getString("difficulty");
            level = Level.valueOf(levelDifficulty);
        }
        level.resetSpeed();

        gameView = findViewById(R.id.gameView);
        gameView.setLevel(level);
    }

    public void resumeGame(View v){
        gameView.startCountdown();
    }

    public void quitGame(View v){
        onBackPressed();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        BackgroundMusicService.stopMusic();
        if(gameView != null){
            gameView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BackgroundMusicService.resumeMusic();
        if(gameView != null){
            gameView.onResume();
        }
    }
}

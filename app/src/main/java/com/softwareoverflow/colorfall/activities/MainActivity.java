package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.softwareoverflow.colorfall.GameView;
import com.softwareoverflow.colorfall.R;

public class MainActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);

        Log.d("debug", "end of onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gameView != null)
            gameView.resume();
    }
}

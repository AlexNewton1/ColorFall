package com.softwareoverflow.colorfall.game;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.free_trial.AdvertHandler;
import com.softwareoverflow.colorfall.game_pieces.GameObject;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnViewInflateListener;

public class Tutorial implements View.OnClickListener{

    private Runnable runnable;
    private Handler handler;

    private GameView gameView;

    private FancyShowCaseView showCaseView;

    private boolean hasBeenShown, isCurrentlyShowing;

    Tutorial (final Activity activity, final GameView gameView, final List<GameObject> gameObjects){
        this.gameView = gameView;

        handler = new Handler();
        final int heightOffset = new AdvertHandler().getGameBannerAd().getMeasuredHeight();

        runnable = new Runnable() {
            @Override
            public void run() {
                for(GameObject object : gameObjects){
                    if( object.getY() >= 0) //view is on screen
                    {
                        gameView.pauseGame();

                        final int bitmapSize = object.getBitmap().getWidth() / 2;
                        final int centreX = object.getX() + bitmapSize;
                        final int centreY = heightOffset + bitmapSize;
                        int radius = (int) (bitmapSize * 1.1);
                        Log.d("debug2", "Showing the view");
                        showCaseView = new FancyShowCaseView.Builder(activity)
                                .closeOnTouch(false)
                                .customView(R.layout.tutorial_pointer_overlay, new OnViewInflateListener() {
                                    @Override
                                    public void onViewInflated(@NonNull View view) {
                                        ImageView pointer = view.findViewById(R.id.tutorial_pointer);
                                        pointer.setLayoutParams(new ConstraintLayout.LayoutParams(
                                                bitmapSize * 3, bitmapSize * 3
                                        ));
                                        pointer.setX(centreX - bitmapSize * 3 / 2);
                                        pointer.setY(centreY);
                                    }
                                })
                                .focusCircleAtPosition(centreX, centreY, radius)
                                .disableFocusAnimation()
                                .build();

                        showCaseView.setOnClickListener(Tutorial.this);
                        showCaseView.show();

                        isCurrentlyShowing = true;
                        return;
                    }
                }
                if(showCaseView == null || !showCaseView.isShown()){
                    handler.post(this);
                }
            }
        };
    }

    public void pause(){
        handler.removeCallbacks(runnable);
        if(showCaseView !=null && showCaseView.isShown()){
            showCaseView.hide();
        }
    }

    public void resume(){
        if(isCurrentlyShowing){
            showCaseView.show();
        }
        if(!hasBeenShown){
            handler.post(runnable);
        }
    }

    @Override
    public void onClick(View v) {
        hasBeenShown = true;
        showCaseView.hide();
        isCurrentlyShowing = false;

        gameView.onResume();
        gameView.startGame();
    }
}

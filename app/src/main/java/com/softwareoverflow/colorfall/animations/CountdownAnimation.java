package com.softwareoverflow.colorfall.animations;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.softwareoverflow.colorfall.game.GameView;
import com.softwareoverflow.colorfall.R;

public class CountdownAnimation {

    private int countdown;
    private static boolean inCountdown = false;

    private final int[] drawableIds =
            { R.drawable.number_1, R.drawable.number_2, R.drawable.number_3 };

    private ImageView imageView;
    private GameView gameView;

    private Animation scaleAnim;

    public CountdownAnimation(final ImageView imageView, float scaleX, float scaleY,
                              final GameView gameView){
        this.imageView = imageView;
        this.gameView = gameView;
        countdown = 3;

        scaleAnim = new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, scaleX, scaleY);
        scaleAnim.setDuration(1000);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("debug", "Started animation: " + countdown);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!inCountdown){
                    imageView.setVisibility(View.GONE);
                    imageView.clearAnimation();
                    return;
                }

                if( countdown > 1) {
                    Log.d("debug", "calling animation recursively");
                    countdown--;
                    start();
                } else {
                    inCountdown = false;
                    gameView.startGame();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void start(){
        inCountdown = true;
        Drawable drawable = gameView.getContext().getDrawable(drawableIds[countdown - 1]);
        imageView.setBackground(drawable);

        imageView.startAnimation(scaleAnim);
    }

    public static void setInCountdown(boolean inCountdown){
        CountdownAnimation.inCountdown = inCountdown;
    }
}

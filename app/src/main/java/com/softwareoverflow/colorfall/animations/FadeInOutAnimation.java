package com.softwareoverflow.colorfall.animations;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class FadeInOutAnimation extends AlphaAnimation{

    public FadeInOutAnimation(int repeatCount) {
        super(1f, 0.1f);
        setInterpolator(new LinearInterpolator());
        setRepeatCount(repeatCount);
        setRepeatMode(Animation.REVERSE);
        setDuration(1000);
    }
}

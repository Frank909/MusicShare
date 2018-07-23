package com.sms.musicshare.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ShowHideAnimation extends Animation {

    private View view;
    private float startAlpha;
    private float deltaAlpha;

    public ShowHideAnimation (View v) {
        this.view = v;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        view.setAlpha(startAlpha + deltaAlpha * interpolatedTime);
        view.requestLayout();
    }

    public void setParams(float start, float end) {

        this.startAlpha = start;
        deltaAlpha = end - startAlpha;
    }

    @Override
    public void setDuration(long durationMillis) {
        super.setDuration(durationMillis);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

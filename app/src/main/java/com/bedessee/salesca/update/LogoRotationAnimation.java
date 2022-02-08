package com.bedessee.salesca.update;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Subclass of {@link android.view.animation.RotateAnimation} used for spinning the Bedessee
 * logo during an update.
 */
public class LogoRotationAnimation extends RotateAnimation {

    public LogoRotationAnimation() {
        super(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        setInterpolator(new AccelerateDecelerateInterpolator());
        setDuration(2000);
        setRepeatCount(-1);
    }

}

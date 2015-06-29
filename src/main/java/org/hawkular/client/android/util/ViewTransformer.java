package org.hawkular.client.android.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;

public final class ViewTransformer implements ValueAnimator.AnimatorUpdateListener {
    private static final class Durations {
        private Durations() {
        }

        public static final int MEDIUM = 220;
    }

    private final View view;

    @NonNull
    public static ViewTransformer of(@NonNull View view) {
        return new ViewTransformer(view);
    }

    private ViewTransformer(View view) {
        this.view = view;
    }

    public void expand() {
        view.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofInt(0, Views.measureHeight(view));
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(Durations.MEDIUM);

        animator.addUpdateListener(this);

        animator.start();
    }

    public void collapse() {
        ValueAnimator animator = ValueAnimator.ofInt(Views.measureHeight(view), 0);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(Durations.MEDIUM);

        animator.addUpdateListener(this);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        view.getLayoutParams().height = (int) animator.getAnimatedValue();

        view.requestLayout();
    }

    public void rotate() {
        Animator animator = ObjectAnimator.ofFloat(view, View.ROTATION, view.getRotation(), view.getRotation() + 180);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(Durations.MEDIUM);

        animator.start();
    }

    @NonNull
    @Override
    public String toString() {
        return "All Hail Megatron".toUpperCase();
    }
}

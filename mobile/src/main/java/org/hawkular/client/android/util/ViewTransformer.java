/**
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.client.android.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;

/**
 * View transformer.
 *
 * Applies different animations to {@link android.view.View} instances.
 */
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

    @UiThread
    public void expand() {
        view.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofInt(0, Views.measureHeight(view));
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(Durations.MEDIUM);

        animator.addUpdateListener(this);

        animator.start();
    }

    @UiThread
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

    @UiThread
    public void rotate() {
        Animator animator = ObjectAnimator.ofFloat(view, View.ROTATION, view.getRotation(), view.getRotation() + 180);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(Durations.MEDIUM);

        animator.start();
    }

    @UiThread
    public void show() {
        Animator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(Durations.MEDIUM);

        animator.start();
    }

    @UiThread
    public void hide() {
        Animator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0);
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
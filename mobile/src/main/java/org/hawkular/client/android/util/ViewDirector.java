/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ViewAnimator;

/**
 * View director.
 *
 * Makes {@link android.widget.ViewAnimator} interaction better, automatically casting all necessary views
 * and changing them only if necessary.
 *
 * It is necessary to call the {@link #using(int)} method before {@link #show(int)}.
 */
public final class ViewDirector {
    private final View sceneView;

    private int animatorId;

    @NonNull
    public static ViewDirector of(@NonNull Activity activity) {
        return new ViewDirector(activity.getWindow().getDecorView());
    }

    @NonNull
    public static ViewDirector of(@NonNull Fragment fragment) {
        return new ViewDirector(fragment.getView());
    }

    private ViewDirector(View sceneView) {
        this.sceneView = sceneView;
    }

    @NonNull
    public ViewDirector using(@IdRes int animatorId) {
        this.animatorId = animatorId;

        return this;
    }

    @UiThread
    public void show(@IdRes int viewId) {
        ViewAnimator animator = (ViewAnimator) sceneView.findViewById(animatorId);
        View view = sceneView.findViewById(viewId);

        if (animator.getDisplayedChild() != animator.indexOfChild(view)) {
            animator.setDisplayedChild(animator.indexOfChild(view));
        }
    }
}
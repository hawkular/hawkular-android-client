/*
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

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;


public class ErrorUtil {
    /**
     * This method is used to set error message to the EditText.
     * @param editText This is EditText.
     * @param errorMessage This is String resource to be shown as error.
     */

    public static void showError(Context context, EditText editText , @StringRes int errorMessage){
        editText.setError(context.getString(errorMessage));
    }

    /**
     * This method is used to show error message in a snackbar.
     * @param view This is the view to find a parent from.
     * @param errorMessage String resource to be shown as error.
     */

    public static void showError(View view, @StringRes int errorMessage){
        Snackbar.make(view,errorMessage,Snackbar.LENGTH_LONG).show();
    }

    /**
     * This method is used to show error message in layout corresponding to a fragment.
     */

    public static void showError(Fragment fragment, int animatorId, int viewId){
        ViewDirector.of(fragment).using(animatorId).show(viewId);
    }

}

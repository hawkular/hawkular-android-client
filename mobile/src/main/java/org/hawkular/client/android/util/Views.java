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

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * View utilities.
 *
 * The most useful ability at this point is measuring {@link android.view.View} instances.
 */
public final class Views {
    private Views() {
    }

    public static boolean isVisible(@NonNull View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    @UiThread
    public static int measureHeight(@NonNull View view) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        return view.getMeasuredHeight();
    }

    @UiThread
    public static int measureHeight(@NonNull ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return 0;
        }

        int listItemsCount = listAdapter.getCount();

        int listItemViewsHeight = 0;

        for (int listItemViewPosition = 0; listItemViewPosition < listItemsCount; listItemViewPosition++) {
            View listItemView = listAdapter.getView(listItemViewPosition, null, listView);

            listItemViewsHeight += Views.measureHeight(listItemView);
        }

        int listDividerViewsHeight = listView.getDividerHeight() * (listItemsCount - 1);

        return listItemViewsHeight + listDividerViewsHeight;
    }
}
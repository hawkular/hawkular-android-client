package org.hawkular.client.android.util;

import android.support.annotation.NonNull;
import android.view.View;

public final class Views {
    private Views() {
    }

    public static int measureHeight(@NonNull View view) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        return view.getMeasuredHeight();
    }
}

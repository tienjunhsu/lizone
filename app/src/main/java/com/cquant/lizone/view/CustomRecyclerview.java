package com.cquant.lizone.view;

/**
 * Created by asus on 2015/10/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class CustomRecyclerview extends RecyclerView {
    public CustomRecyclerview(Context context) {
        super(context);
    }

    public CustomRecyclerview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int newHeightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, newHeightSpec);
    }
}

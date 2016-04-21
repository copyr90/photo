package com.example.tester.new_2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/4/9.
 */
public class AdjustGridView extends GridView {

    public AdjustGridView(Context context) {
        super(context);
    }

    public AdjustGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

package com.project.mapping;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * Created by lin.woo on 2020/2/9.
 */
public class NodeView extends FrameLayout {
    private View v;
    public NodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        v = LayoutInflater.from(getContext()).inflate(R.layout.item_node, this, true);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}

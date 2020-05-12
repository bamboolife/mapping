package com.project.mapping.tree;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

public class MoveScaleView extends ViewGroup implements ScaleGestureDetector.OnScaleGestureListener {
	private static final String TAG = "MoveScaleView";
	public  TreeView tree;
	private int mPointerId;

	public MoveScaleView(Context context) {
		super(context);
		init();
	}

	public MoveScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MoveScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public MoveScaleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int size = getChildCount();
		for (int i = 0; i < size; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		tree.layout(l, t, r, b);
		Log.e(TAG, "treeOnLayout"+tree.getMeasuredWidth() + "  " + tree.getMeasuredHeight());
	}

	void init() {
		ctx						= this.getContext();
		mScaleGestureDetector	= new ScaleGestureDetector(ctx, this);
		TouchSlop				= ViewConfiguration.get(ctx).getScaledTouchSlop();
		tree					= new TreeView(ctx);

		addView(tree);
		setClipChildren(false);
		setClipToPadding(false);
	}
	@Override
	public boolean performClick() {
		return super.performClick();
	}

	private ScaleGestureDetector mScaleGestureDetector = null;
	private float preScale = 1;// 默认前一次缩放比例为1
	private int TouchSlop;
	private Context ctx;
	float lastX, lastY, baseX, baseY;
	int mode = 0;
	private boolean onMove;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mPointerId	= ev.getPointerId(0);
				baseX		= ev.getX(mPointerId);
				baseY		= ev.getY(mPointerId);
				mode = 1;
				Log.e(TAG, "onTouchEvent   ACTION_DOWN  " + mode);
				break;

			case MotionEvent.ACTION_MOVE:
				Log.e(TAG, "onTouchEvent   ACTION_MOVE  " + mode);
				float	ex = ev.getX(ev.findPointerIndex(mPointerId)),
						ey = ev.getY(ev.findPointerIndex(mPointerId));
				if (!onMove) {
					float moveX = ex - baseX;
					float moveY = ey - baseY;
					onMove = TouchSlop < Math.sqrt(moveX * moveX + moveY * moveY);
					if (onMove) {
						baseX = tree.getLeft();
						baseY = tree.getTop();
					}
				}
				if (onMove) {
					tree.setLeft((int) (tree.getLeft() + ex - lastX));
					tree.setTop ((int) (tree.getTop()  + ey - lastY));
				}
				lastX = ex;
				lastY = ey;
				break;
		}
		return onMove;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mode > 1) {
			Log.e(TAG, "onTouchEvent   UNKNOWN  " + mode);
			if (mScaleGestureDetector.onTouchEvent(ev)) {
				Log.e(TAG, "onTouchEvent   UNKNOWN  ------ return");
			} else {
				mode = 1;
			}
		}

		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				mode += 1;
				Log.e(TAG, "onTouchEvent   ACTION_POINTER_DOWN  " + mode);
				break;

			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_POINTER_UP:
				//将模式进行为负数这样，多指下，抬起不会触发移动
				Log.e(TAG, "onTouchEvent ACTION_OUTSIDE | ACTION_POINTER_UP");
				int actIndex	= ev.getActionIndex();
				if (ev.getPointerId(actIndex) == mPointerId) {
					int reIndex = actIndex == 0 ? 1 : 0;
					mPointerId  = ev.getPointerId(reIndex);
					lastX		= ev.getX(mPointerId);
					lastY		= ev.getY(mPointerId);
				}
				mode = -2;
				break;

			case MotionEvent.ACTION_MOVE:
				Log.e(TAG, "onTouchEvent   ACTION_MOVE  " + mode);
				float	ex = ev.getX(ev.findPointerIndex(mPointerId)),
						ey = ev.getY(ev.findPointerIndex(mPointerId));
				if (!onMove) {
					float moveX = ex - baseX;
					float moveY = ey - baseY;
					onMove = TouchSlop < Math.sqrt(moveX * moveX + moveY * moveY);
					if (onMove) {
						baseX = tree.getLeft();
						baseY = tree.getTop();
					}
				}
				if (onMove) {
					tree.setLeft((int) (tree.getLeft() + ex - lastX));
					tree.setTop ((int) (tree.getTop()  + ey - lastY));
				}
				lastX = ex;
				lastY = ey;
				break;

			case MotionEvent.ACTION_UP:
				mode = 0;
				if (!onMove) {
					performClick();
					return false;
				}
				onMove = false;
				Log.e(TAG, "onTouchEvent   ACTION_UP  " + mode);
				break;
		}
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		tree.setScale(tree.scale *= detector.getScaleFactor());
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		Log.e(TAG, "MotionEvent.onScaleBegin  ");
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		Log.e(TAG, "MotionEvent.onScaleEnd  ");
	}
}
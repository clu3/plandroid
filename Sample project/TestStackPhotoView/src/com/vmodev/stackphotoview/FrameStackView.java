package com.vmodev.stackphotoview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

public class FrameStackView extends FrameLayout {
	private static String TAG = "StackView";
	private GestureDetector detector;
	private BaseAdapter adapter;
	private DataSetObserver observer;

	public FrameStackView(Context context) {
		super(context);
		initializeView();
	}

	public FrameStackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public FrameStackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeView();
	}

	private void initializeView() {
		detector = new GestureDetector(getContext(), new GestureListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	private class GestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.i(TAG, "OnScroll: " + distanceX + "   " + distanceY);
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}

	private void refreshView() {
		if (getChildCount() != 0) {
			removeAllViews();
		}
		if (adapter != null) {
			for (int position = 0; position < adapter.getCount(); position++) {
				View newChild = adapter.getView(position, null, this);
				// add to parent view
				FrameLayout.LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				addView(newChild, params);
				// rotate
				newChild.setRotation(position * 2);
				// moving
				newChild.setOnTouchListener(new OnTouchToMovingListener());
			}
		}
	}

	public void setAdapter(BaseAdapter adapter) {
		if (this.adapter != null && observer != null) {
			this.adapter.unregisterDataSetObserver(observer);
		}
		this.adapter = adapter;
		if (this.adapter != null) {
			observer = new MyDataSetObserver();
			this.adapter.registerDataSetObserver(observer);
		}
		refreshView();
	}

	private class MyDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
		}
	}

	private class OnTouchToMovingListener implements OnTouchListener {
		private int _xDelta;
		private int _yDelta;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			View myView = v;// getChildAt(getChildCount() - 1);
			//
			WindowManager wm = (WindowManager) getContext().getSystemService(
					Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			int widthScreen = display.getWidth(); // deprecated

			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) myView
					.getLayoutParams();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				_xDelta = X - layoutParams.leftMargin;
				_yDelta = Y - layoutParams.topMargin;
				break;
			case MotionEvent.ACTION_UP:
				// layoutParams.leftMargin = 0;
				// layoutParams.topMargin = 0;
				// layoutParams.rightMargin = 0;
				// layoutParams.bottomMargin = 0;
				// myView.setLayoutParams(layoutParams);
				 AnimatorSet set = new AnimatorSet();
				 set.playTogether(
				 // ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
				 // ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
				 // ObjectAnimator.ofFloat(myView, "rotation", 0, -10),
				 ObjectAnimator.ofFloat(myView, "translationX", 0, 10)
				 // ObjectAnimator.ofFloat(myView, "translationY", 0, 10)
				 // ObjectAnimator.ofFloat(myView, "scaleX", 1, 1.5f),
				 // ObjectAnimator.ofFloat(myView, "scaleY", 1, 0.5f)
				 // ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
				 );
				 set.setDuration(1000).start();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				layoutParams.leftMargin = X - _xDelta;
				layoutParams.topMargin = Y - _yDelta;
				layoutParams.rightMargin = -300;
				layoutParams.bottomMargin = -300;
				myView.setLayoutParams(layoutParams);
				if (X > widthScreen / 2) {
					myView.setRotation(myView.getRotation() + 0.5f);
				} else {
					myView.setRotation(myView.getRotation() - 0.5f);
				}
				// Log.i("", "X=" + X + "  " + "Y=" + Y + "  " + "Xdelta=" +
				// _xDelta
				// + "  " + "Ydelta" + _yDelta + "  ");
				break;
			}
			return true;
		}
	}
}

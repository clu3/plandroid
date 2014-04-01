package com.vmodev.stackphotoview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

public class WindowStackView extends RelativeLayout {
	private static String TAG = "StackView";
	private GestureDetector detector;
	private BaseAdapter adapter;
	private DataSetObserver observer;

	public WindowStackView(Context context) {
		super(context);
		initializeView();
	}

	public WindowStackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public WindowStackView(Context context, AttributeSet attrs, int defStyle) {
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
				// RelativeLayout.LayoutParams params = new LayoutParams(
				// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				// addView(newChild, params);
				// add to window manager
				WindowManager window = (WindowManager) getContext()
						.getSystemService(Context.WINDOW_SERVICE);
				final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
						WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
								| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
						PixelFormat.TRANSLUCENT);
				window.addView(newChild, params);
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
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			View myView = v;// getChildAt(getChildCount() - 1);
			//
			WindowManager window = (WindowManager) getContext()
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = window.getDefaultDisplay();
			int widthScreen = display.getWidth(); // deprecated

			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) myView
					.getLayoutParams();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				window.updateViewLayout(myView, layoutParams);
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

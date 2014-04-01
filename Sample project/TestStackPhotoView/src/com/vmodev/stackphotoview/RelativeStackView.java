package com.vmodev.stackphotoview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

public class RelativeStackView extends RelativeLayout {
	private static String TAG = "StackView";
	private GestureDetector detector;
	private BaseAdapter adapter;
	private DataSetObserver observer;

	public RelativeStackView(Context context) {
		super(context);
		initializeView();
	}

	public RelativeStackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public RelativeStackView(Context context, AttributeSet attrs, int defStyle) {
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
				RelativeLayout.LayoutParams params = new LayoutParams(
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
		float dx = 0, dy = 0, x = 0, y = 0;

		@Override
		public boolean onTouch(View myView, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				x = event.getRawX();
				y = event.getRawY();
				dx = x - myView.getX();
				dy = y - myView.getY();
			}
				break;
			case MotionEvent.ACTION_MOVE: {
				myView.setX(event.getX() - dx);
				myView.setY(event.getY() - dy);
			}
				break;
			case MotionEvent.ACTION_UP: {
				// your stuff
			}
			}
			return true;
		}
	}
}

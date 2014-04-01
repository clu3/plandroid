package com.vmodev.stackphotoview;

import java.util.Random;

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

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class AnimStackView extends RelativeLayout {
	private static final int FACTOR_ROTATION = 20;
	// condition to remove view
	private static final int THRESHOLD_VELOCITY = 1000;
	private static final int THRESHOLD_DELTA_X = 0;
	// durations
	private static final int TIME_TO_BACK = 500;
	private static final int TIME_TO_REMOVE = 1000;
	// distances
	private static final int DISTANCE_MOVEOUT_EXTRA_X = 0;
	private static final int DISTANCE_MOVEOUT_EXTRA_Y = 1000;
	// condition to rotate view when it's moving
	private static final int THRESHOLD_X_DELTA_TO_ROTATE = 0;
	private static final int THRESHOLD_Y_DELTA_TO_ROTATE = 0;

	private static String MOVED_VIEW = "Moved_view";
	private static String TAG = "StackView";
	private GestureDetector detector;
	private BaseAdapter adapter;
	private DataSetObserver observer;

	public AnimStackView(Context context) {
		super(context);
		initializeView();
	}

	public AnimStackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public AnimStackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeView();
	}

	private void initializeView() {
		detector = new GestureDetector(getContext(), new GestureListener());
		trackMovementView(this);
	}

	private void trackMovementView(View view) {
		view.setOnTouchListener(new OnTouchTrackMovementListener());
	}

	private float randomRotation() {
		return ((new Random().nextFloat() - 0.5f) * FACTOR_ROTATION);
	}

	private View getTopView() {
		for (int position = getChildCount() - 1; position >= 0; position--) {
			View childAt = getChildAt(position);
			Object tag = childAt.getTag();
			boolean isMovedView = tag != null && MOVED_VIEW.equals(tag);
			if (!isMovedView) {
				return childAt;
			}
		}
		return null;
	}

	private class GestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float deltaX = e2.getRawX() - e1.getRawX();
			Log.i(TAG,
					"onFling: velocityX=" + velocityX + "  " + "\tvelocityY="
							+ velocityY + "\tdeltaX=" + String.valueOf(deltaX));
			if (Math.abs(velocityX) > THRESHOLD_VELOCITY
					&& Math.abs(deltaX) > THRESHOLD_DELTA_X) {
				boolean isRight = velocityX > 0;
				moveOutView(getTopView(), isRight, velocityX, velocityY);
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// get the top view in stack
			View topView = getTopView();
			// move view to new position
			ViewPropertyAnimator.animate(topView).setDuration(0)
					.translationXBy(-distanceX);
			ViewPropertyAnimator.animate(topView).setDuration(0)
					.translationYBy(-distanceY);
			// rotate view if the distance of moving is enough far
			if (Math.abs(distanceX) > THRESHOLD_X_DELTA_TO_ROTATE
					|| Math.abs(distanceY) > THRESHOLD_Y_DELTA_TO_ROTATE) {
				float rotationValue = distanceX / 10;
				// rotate if move to left or right (not only up/down)
				if (distanceX != 0) {
					ViewPropertyAnimator.animate(topView).setDuration(0)
							.rotationBy(-rotationValue);
				}
			}
			// Log.i(TAG, "Xdelta=" + distanceX + "  " + "Ydelta" + distanceY);
			return true;
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
				ViewPropertyAnimator.animate(newChild).setDuration(0)
						.rotation(randomRotation());
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

	public class OnTouchTrackMovementListener implements OnTouchListener {
		private float x, y;
		// track the action down is valid or not
		private boolean allowMoving = false;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			View topView = getTopView();
			if (topView == null) {
				return false;
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				allowMoving = isTouchInView(event, topView);
			}
			// ignore all of invalid action (not in child view)
			if (!allowMoving) {
				return false;
			}
			// always allow ACTION_UP
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// store x,y of the topView
				x = topView.getLeft();
				y = topView.getTop();
				break;
			case MotionEvent.ACTION_UP:
				ViewPropertyAnimator.animate(topView).setDuration(TIME_TO_BACK)
						.x(x).y(y).rotation(randomRotation());
				break;
			}
			return detector.onTouchEvent(event);
		}
	}

	private void moveOutView(final View view, boolean toRight, float velocityX,
			float velocityY) {
		float translationXValue = toRight ? Math.abs(velocityX) : -(Math
				.abs(velocityX) + DISTANCE_MOVEOUT_EXTRA_X);
		float translationYValue = toRight ? velocityY : velocityY
				+ DISTANCE_MOVEOUT_EXTRA_Y;
		Log.i(TAG, "Moving view to " + (toRight ? "right" : "left") + " ["
				+ translationXValue + "," + translationYValue + "]");
		view.setTag(MOVED_VIEW);
		ViewPropertyAnimator.animate(view).setDuration(TIME_TO_REMOVE)
				.translationXBy(translationXValue)
				.translationYBy(translationYValue)
				.setListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator animation) {

					}

					@Override
					public void onAnimationRepeat(Animator animation) {

					}

					@Override
					public void onAnimationEnd(Animator animation) {
						removeView(view);
					}

					@Override
					public void onAnimationCancel(Animator animation) {

					}
				});
	}

	/**
	 * Check the event is in view or not
	 * 
	 */
	private boolean isTouchInView(MotionEvent event, View view) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		boolean isIn = location[0] <= event.getRawX()
				&& event.getRawX() <= location[0] + view.getWidth();
		isIn = isIn && location[1] <= event.getRawY()
				&& event.getRawY() <= location[1] + view.getHeight();
		// Log.i(TAG, "X=" + location[0] + "\tY=" + location[1] + "\n Touched ("
		// + event.getRawX() + "," + event.getRawY() + ")");
		return isIn;
	}
}

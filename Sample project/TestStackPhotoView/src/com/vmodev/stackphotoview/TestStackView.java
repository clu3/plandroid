package com.vmodev.stackphotoview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

public class TestStackView extends AdapterView {
	private Adapter mAdapter;

	/**
	 * {@inheritDoc}
	 */
	public TestStackView(Context context) {
		super(context);
		initStackView();
	}

	/**
	 * {@inheritDoc}
	 */
	public TestStackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStackView();
	}

	/**
	 * {@inheritDoc}
	 */
	public TestStackView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initStackView();
	}

	private void initStackView() {
		this.setOnTouchListener(new OnTouchChildViewListener());
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		// if we don't have an adapter, we don't need to do anything
		if (mAdapter == null) {
			return;
		}

		if (getChildCount() == 0) {
			int position = 0;
			int bottomEdge = 0;
			while (bottomEdge < getHeight() && position < mAdapter.getCount()) {
				View newBottomChild = mAdapter.getView(position, null, this);

				// rotate
				newBottomChild.setRotation(position * 2);

				// set touch listener
				addAndMeasureChild(newBottomChild);
				bottomEdge += newBottomChild.getMeasuredHeight();
				position++;
			}
		}

		positionItems();
	}

	private class OnTouchChildViewListener implements OnTouchListener {
		private int _xDelta;
		private int _yDelta;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			View myView = getChildAt(getChildCount() - 1);
			//
			WindowManager wm = (WindowManager) getContext().getSystemService(
					Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			int widthScreen = display.getWidth(); // deprecated

			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) myView
						.getLayoutParams();
				_xDelta = X - lParams.leftMargin;
				_yDelta = Y - lParams.topMargin;
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myView
						.getLayoutParams();
				layoutParams.leftMargin = X - _xDelta;
				layoutParams.topMargin = Y - _yDelta;
				layoutParams.rightMargin = -250;
				layoutParams.bottomMargin = -250;
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

	/**
	 * Adds a view as a child view and takes care of measuring it
	 * 
	 * @param child
	 *            The view to add
	 */
	private void addAndMeasureChild(View child) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(child, -1, params, true);

		int itemWidth = getWidth();
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	/**
	 * Positions the children at the "correct" positions
	 */
	private void positionItems() {
		int top = 0;

		for (int index = 0; index < getChildCount(); index++) {
			View child = getChildAt(index);

			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			int left = (getWidth() - width) / 2;

			child.layout(left, top, left + width, top + height);
			// top += height;
		}
	}
}

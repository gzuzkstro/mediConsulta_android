package com.antonyt.infiniteviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

import hirondelle.date4j.DateTime;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around
 * effect. Should be used with an {@link InfinitePagerAdapter}.
 * 
 */
public class InfiniteViewPager extends ViewPager {

	// ******* Declaration *********
	public static final int OFFSET = 1000;

	/**
	 * datesInMonth is required to calculate the height correctly
	 */
	private ArrayList<DateTime> datesInMonth;

	/**
	 * Enable swipe
	 */
	private boolean enabled = true;

	/**
	 * A calendar height is not fixed, it may have 4, 5 or 6 rows. Set
	 * fitAllMonths to true so that the calendar will always have 6 rows
	 */
	private boolean sixWeeksInCalendar = false;

	/**
	 * Use internally to decide height of the calendar
	 */
	private int rowHeight = 0;

	// ******* Setter and getters *********
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSixWeeksInCalendar() {
		return sixWeeksInCalendar;
	}

	public ArrayList<DateTime> getDatesInMonth() {
		return datesInMonth;
	}

	public void setDatesInMonth(ArrayList<DateTime> datesInMonth) {
		this.datesInMonth = datesInMonth;
	}

	public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
		this.sixWeeksInCalendar = sixWeeksInCalendar;
		rowHeight = 0;
	}

	// ************** Constructors ********************
	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InfiniteViewPager(Context context) {
		super(context);
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		// offset first element so that we can scroll to the left
		setCurrentItem(OFFSET);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

}

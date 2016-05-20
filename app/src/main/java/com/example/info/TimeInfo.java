package com.example.info;

import android.widget.GridView;

public class TimeInfo {

	/**
	 * @return the textView
	 */
	public String getTextView() {
		return textView;
	}

	/**
	 * @param textView
	 *            the textView to set
	 */
	public void setTextView(String textView) {
		this.textView = textView;
	}

	private String textView;

	private GridView gridView;

	/**
	 * @return the gridView
	 */
	public GridView getGridView() {
		return gridView;
	}

	/**
	 * @param gridView the gridView to set
	 */
	public void setGridView(GridView gridView) {
		this.gridView = gridView;
	}

}

package org.pochette.utils_lib.report;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

/**
 * Report is a POJO class to contain information to be transferred from the ReportSystem to the ReportThread
 */
@SuppressWarnings("unused")
public class Report {

	private static final String TAG = "FEHA (Report)";

	//Variables
	String mText;
	String mType; // identify repeated reports by same type
	Date mCreatedDate;
	Date mShownDate;
	boolean mHasBeenShown;

	//Constructor
	public Report(String tText) {
		mText = tText;
		mCreatedDate = Calendar.getInstance().getTime();
		mShownDate = null;
		mHasBeenShown = false;
		mType ="";
	}

	public Report(String tText, String tType) {
		mText = tText;
		mCreatedDate = Calendar.getInstance().getTime();
		mShownDate = null;
		mHasBeenShown = false;
		mType = tType;
	}

	//Setter and Getter
	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

	public Date getCreatedDate() {
		return mCreatedDate;
	}

	public void setCreatedDate(Date mCreatedDate) {
		this.mCreatedDate = mCreatedDate;
	}

	public Date getShownDate() {
		return mShownDate;
	}

	public void setShownDate(Date mShownDate) {
		this.mShownDate = mShownDate;
	}

	public boolean isHasBeenShown() {
		return mHasBeenShown;
	}

	public void setHasBeenShown(boolean mHasBeenShown) {
		this.mHasBeenShown = mHasBeenShown;
	}


	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface
	
	@NonNull
	public String toString() {
		return mText;
	}
}
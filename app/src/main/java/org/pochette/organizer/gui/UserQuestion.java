package org.pochette.organizer.gui;

/**
 * POJO UserQuestion stores the question for the UserChoice
 */
public class UserQuestion {

	//Variables
	@SuppressWarnings("unused")
	private final String TAG = "FEHA (UserQuestion)";
	public String mCode;
	public String mText;
	public boolean mIsDefault;

	//Constructor
	public UserQuestion(String tCode, String tText) {
		mCode = tCode;
		mText = tText;
		mIsDefault = false;
	}
	public UserQuestion(String tCode, String tText, boolean tIsDefault) {
		mCode = tCode;
		mText = tText;
		mIsDefault = tIsDefault;
	}
	//Setter and Getter
	public String getCode() {
		return mCode;
	}
	@SuppressWarnings("unused")
	public void setCode(String tCode) {
		mCode = tCode;
	}
	public String getText() {
		return mText;
	}
	public void setText(String tText) {
		mText = tText;
	}

	@Override
	public String toString() {
		return mCode + ": " + mText;
	}

	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface

}

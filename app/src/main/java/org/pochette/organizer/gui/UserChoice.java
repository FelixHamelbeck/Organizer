package org.pochette.organizer.gui;

//import android.app.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import org.pochette.utils_lib.logg.Logg;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * UserChoice retrieves choices from the user through AlertDialog
 *
 * Each available choice is created through addQuestion(UserQuestion)
 * The AlertDialog is created and displayed in poseQuestion(context)
 */
@SuppressWarnings("unused")
public class UserChoice {
	//Variables
	@SuppressWarnings("FieldCanBeLocal")
	private final String TAG = "FEHA (UserChoice)";
	UserChoice mUserChoice;
	ArrayList<UserQuestion> mAR ;
	private UserQuestion mAnswer;
	private final String mTitle;

	//Constructor
	public UserChoice(ArrayList<UserQuestion> tAR, String tTitle) {
		mAR = tAR;
		mTitle = tTitle;
		mUserChoice = this;
	}
	public UserChoice( String tTitle) {
		mAR = new ArrayList<>(0);
		mTitle = tTitle;
		mUserChoice = this;
	}

	//Setter and Getter
	@NonNull
	@Override
	public String toString() {
		String result = "";
		for (UserQuestion t : mAR) {
			result = String.format(Locale.ENGLISH,"%s\n%s",result, t.toString());
		}
		if (mAnswer == null) {
			result = String.format(Locale.ENGLISH,"%s\n=> Answer=%s", result, "???");
		} else {
			result = String.format(Locale.ENGLISH,"%s\n=> Answer=%s", result, mAnswer.mCode);
		}
		return result;
	}

	//Livecycle

	//Static Methods

	//Internal Organs
	private UserQuestion processAnswer(int tAnswer) {
		int i = 0;
		for (UserQuestion t : mAR) {
			if (i == tAnswer) {
				return t;
			}
			i++;
		}
		return null;
	}
	//Interface

	/**
	 * Add Question to the list of choices
	 * @param tUserQuestion to be added
	 */
	public void addQuestion(UserQuestion tUserQuestion) {
		mAR.add(tUserQuestion);
	}

	/**
	 * Add question to the list of choices. The question is created from tCode and tText
	 * @param tCode Code of the question
	 * @param tText Text of the question
	 */
	public void addQuestion(String tCode, String tText) {
		mAR.add(new UserQuestion(tCode,tText));
	}

	/**
	 * Add question to the list of choices. The question is created from tCode and tText
	 * @param tCode Code of the question
	 * @param tText Text of the question
	 * @param tIsDefault boolean to define default
	 */
	public void addQuestion(String tCode, String tText, boolean tIsDefault) {
		mAR.add(new UserQuestion(tCode,tText, tIsDefault));
	}

	/**
	 * This method poses the question in a AlertDialog to the user.
	 * It loops on the UI thread, till a RunTime Exception is raised.
	 *
	 * If a choice was selected, it is returned.
	 * If not choice was selected by touching outside of the AlertBuilder null is returned.
	 * @param iContext Context for the question
	 * @return answer to the question
	 */
	@SuppressLint("HandlerLeak")
	public  String poseQuestion(Context iContext) {
		final Handler fHandler;
		fHandler = new Handler()	{
			@Override
			public void handleMessage(@NonNull Message mesg)
			{
				throw new RuntimeException();
			}
		};
		//MaintenanceSystem.rememberThread( handler.getLooper().getThread());
		List<String> tListString = new ArrayList<>();
		for (UserQuestion t : mAR) {
			tListString.add(t.getText());
		}
		String[] tArrayString = new String[tListString.size()];
		tArrayString = tListString.toArray(tArrayString);
		AlertDialog.Builder builder = new AlertDialog.Builder(iContext);
		if (mTitle != null && mTitle.length()>0) {
			builder.setTitle(mTitle);
		}
		builder.setItems(tArrayString, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAnswer = mUserChoice.processAnswer(which);
				fHandler.sendMessage(fHandler.obtainMessage());
			}
		});
		try {
			builder.show();
			Looper.loop();
		} catch (Exception e) {
			// this exception is no error, just the user action of selecting or tapping outside
		}
		if (mAnswer == null) {
			return null;
		}
		return mAnswer.mCode;
	}
}

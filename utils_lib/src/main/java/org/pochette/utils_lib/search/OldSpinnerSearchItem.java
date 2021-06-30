package org.pochette.utils_lib.search;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * this class combines the data for spinner: code, text, sql and image resource
 * so much about MCV
 */
public class OldSpinnerSearchItem {

	private final String TAG = "FEHA (OldSpinnerSearchItem)";

	//Variables
	public Class mClass;
	public String mCode;
	public String mDisplaytext;
	public boolean mInclude ; // true if enums in mAR are allowed, false when excluded
	public ArrayList<Enum> mAR_Enum;
	public int mSort;
	public int mImageResource;

	//Constructor
	public OldSpinnerSearchItem(String iCode, String iDisplaytext, boolean iInclude, Enum iEnum, int iSort, int iImageResource) {
		mCode = iCode;
		mDisplaytext = iDisplaytext;
		mInclude = iInclude;
		mAR_Enum = new ArrayList<>(0);
		mAR_Enum.add(iEnum);
		mSort = iSort;
		mImageResource = iImageResource;
	}
//
//	public OldSpinnerSearchItem(MyEnum iMyEnum, int iImageResource) {
//		mCode = iEnum.;
//		mDisplaytext = iDisplaytext;
//		mInclude = iInclude;
//		mAR_Enum = new ArrayList<>(0);
//		mAR_Enum.add(iEnum);
//		mSort = iSort;
//		mImageResource = iImageResource;
//	}

	//Setter and Getter
	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface

	@NonNull
	public String toString() {
		int tSize;
		if (mAR_Enum == null) {
			tSize = 0;
		} else {
			tSize = mAR_Enum.size();
		}

		return String.format(Locale.ENGLISH,
				"%s [%s, %3d]\n%d", mCode, mDisplaytext, mSort, tSize);
	}

}

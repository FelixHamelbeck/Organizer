package org.pochette.organizer.gui_assist;

import org.pochette.utils_lib.search.SearchCriteria;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * this class combines the data for spinner: code, text, sql and image resource
 * so much about MCV
 *
 * method and value are translated into a search criteria
 * key is used to identify a CSI, if value is empty or blank it is method, else value
 * Displaytext and imageresource are for GUI representation
 * Sort is a sort key
 *
 * The only place where a CustomSpinnerItem should be created is the SpinnerItemFactory
 */
public class CustomSpinnerItem {

	@SuppressWarnings("unused")
	private final String TAG = "FEHA (SpinnerSearchItem)";
	//Variables
	public String mMethod;
	public String mValue;
	public String mDisplaytext;
	public String mKey;
	public int mSort;
	public int mImageResource;

	//Constructor
	public CustomSpinnerItem(String iDisplaytext, String iMethod, String iValue, int iSort, int iImageResource) {
		mMethod = iMethod;
		mValue = iValue;
		mDisplaytext = iDisplaytext;
		mSort = iSort;
		mImageResource = iImageResource;
		if (mValue == null || mValue.isEmpty()) {
			mKey = mMethod;
		} else {
			mKey = mValue;
		}
	}
	//Setter and Getter
	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface

	@SuppressWarnings("unused")
	public SearchCriteria getSearchCriteria() {
		return new SearchCriteria(mMethod,mValue);
	}

	@NonNull
	public String toString() {
		return String.format(Locale.ENGLISH,
				"%s [%s, %s]", mDisplaytext, mMethod,  mValue);
	}

}

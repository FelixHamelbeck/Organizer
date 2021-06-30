package org.pochette.organizer.gui_assist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.pochette.organizer.R;

import java.util.ArrayList;

/**
 * The available Modes are
 * "IT" -> Icon and Text = row_spinner_icontext.xml
 * "I0" -> Icon and No Text = row_spinner_icononly.xml
 * "0T" -> No Icon but Text = row_spinner_textonly.xml
 */
@SuppressWarnings("unused")
public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

	@SuppressWarnings("FieldCanBeLocal")
	private final String TAG = "FEHA (CustomSpinnerAdapter)";

	public static final String MODE_ICON_TEXT = "IT";
	public static final String MODE_TEXT_ONLY = "0T";
	public static final String MODE_ICON_ONLY = "I0";
	//Variables
	ArrayList<CustomSpinnerItem> mAR_Custom_SpinnerItem;
	private String mTitleMode = "IT";
	private String mDropdownMode = "IT";

	//Constructor
	public CustomSpinnerAdapter(@SuppressWarnings("unused") Context context, ArrayList<CustomSpinnerItem> tAR_Custom_SpinnerItem) {
		mAR_Custom_SpinnerItem = tAR_Custom_SpinnerItem;
	}

	//Setter and Getter
	public void setTitleMode(String iTitleMode) {
		mTitleMode = iTitleMode;
	}

	public void setDropdownMode(String iDropdownMode) {
		mDropdownMode = iDropdownMode;
	}
	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface
	@Override
	public int getCount() {
		if (mAR_Custom_SpinnerItem == null || mAR_Custom_SpinnerItem.size() == 0) {
			return 0;
		}
		return mAR_Custom_SpinnerItem.size();
	}

	@Override
	public Object getItem(int position) {
		return mAR_Custom_SpinnerItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Create the view, after the dropdown is closed
	 *
	 * @param position    of item selected
	 * @param convertView .
	 * @param parent      as parent
	 * @return the new view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View tView;
		boolean tFlagText = true;
		boolean tFlagIcon = true;

		Context tContext = parent.getContext();
		switch (mTitleMode) {
			default:
			case "IT":
				tView = View.inflate(tContext, R.layout.row_spinner_icontext, null);
				break;
			case "I0":
				if (mAR_Custom_SpinnerItem.get(position).mImageResource != 0) {
					tView = View.inflate(tContext, R.layout.row_spinner_icononly, null);
					tFlagText = false;
				} else {
					tView = View.inflate(tContext, R.layout.row_spinner_icontext, null);
				}
				break;
			case "0T":
				tView = View.inflate(tContext, R.layout.row_spinner_textonly, null);
				tFlagIcon = false;
				break;
		}
		ImageView tImageView = tView.findViewById(R.id.IV_SP_Icon);
		TextView tTextview = tView.findViewById(R.id.TV_SP_Text);
		if (tImageView != null) {
			if (tFlagIcon) {
				tImageView.setImageResource(mAR_Custom_SpinnerItem.get(position).mImageResource);
				tImageView.setVisibility(View.VISIBLE);
			} else {
				tImageView.setVisibility(View.GONE);
			}
		}
		if (tTextview != null) {
			if (tFlagText) {
				tTextview.setVisibility(View.VISIBLE);
				tTextview.setText(mAR_Custom_SpinnerItem.get(position).mDisplaytext);
			} else {
				tTextview.setVisibility(View.GONE);
			}
		}
		return tView;
	}

	/**
	 * Create the view in the dropdown
	 *
	 * @param position    of item
	 * @param convertView .
	 * @param parent      as parent
	 * @return the new view
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View tView;
		boolean tFlagText = true;
		boolean tFlagIcon = true;
		Context tContext = parent.getContext();
		switch (mDropdownMode) {
			default:
			case "IT":
				tView = View.inflate(tContext, R.layout.row_spinner_icontext, null);
				break;
			case "I0":

				tView = View.inflate(tContext, R.layout.row_spinner_icononly, null);
				tFlagText = false;
				break;
			case "0T":
				tView = View.inflate(tContext, R.layout.row_spinner_textonly, null);
				tFlagIcon = false;
				break;
		}

		ImageView tImageView = tView.findViewById(R.id.IV_SP_Icon);
		TextView tTextview = tView.findViewById(R.id.TV_SP_Text);
		if (tImageView != null) {
			if (tFlagIcon) {
				tImageView.setVisibility(View.VISIBLE);
				tImageView.setImageResource(mAR_Custom_SpinnerItem.get(position).mImageResource);
			} else {
				tImageView.setVisibility(View.GONE);
			}
		}
		if (tTextview != null) {
			if (tFlagText) {
				tTextview.setText(mAR_Custom_SpinnerItem.get(position).mDisplaytext);
				tTextview.setVisibility(View.VISIBLE);
			} else {
				tTextview.setVisibility(View.GONE);
			}
		}

		return tView;
	}

	public int getPosition(CustomSpinnerItem tCustomSpinnerItem) {
		return getPosition(tCustomSpinnerItem.mKey);
	}

	public int getPosition(String iValue) {
		int i = 0;
		for (CustomSpinnerItem sCustomSpinnerItem : mAR_Custom_SpinnerItem) {
			if (sCustomSpinnerItem.mValue.equals(iValue) ) {
				return i;
			}
			i++;
		}
		return 0;
	}

	public ArrayList<CustomSpinnerItem> getDefaultSsi() {
		return mAR_Custom_SpinnerItem;
	}

}
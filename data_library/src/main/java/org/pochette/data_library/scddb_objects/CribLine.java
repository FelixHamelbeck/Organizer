package org.pochette.data_library.scddb_objects;

import androidx.annotation.NonNull;

/**
 * CribLine is a POJO class to store one cribline for displaying
 *
 * Crib is the database object, which corresponds to the full crib, i.e. all the lines
 * In ScddbCrib_Adapter this is split by Crib.convert into an Arraylist of CribLine
 */
public class CribLine {

	//Variables
	@SuppressWarnings("unused")
	private static final String TAG = "FEHA (ScddbCribline)";
	public int mLineNUmber;
	public String mBarColumn;
	public String mDescColumn;

	//Constructor

	public CribLine(int mLineNUmber, String mBarColumn, String mDescColumn) {
		this.mLineNUmber = mLineNUmber;
		this.mBarColumn = mBarColumn;
		this.mDescColumn = mDescColumn;
	}
	//Setter and Getter

	//Livecycle

	//Static Methods

	//Internal Organs

	//Interface
	@NonNull
	@Override
	public String toString() {
		return "CribLine{" +
				"mLineNUmber=" + mLineNUmber +
				", mBarColumn='" + mBarColumn + '\'' +
				", mDescColumn='" + mDescColumn + '\'' +
				'}';
	}

}
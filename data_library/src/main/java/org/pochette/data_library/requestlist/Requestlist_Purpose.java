package org.pochette.data_library.requestlist;


/**
 * Requestlist_Purpose describes the purpose of a Requestlist<br>
 * String for database query included
 * <p>
 * 0   EVENT   Event related<br>
 * 1   PURPOSE Purpose based ???<br>
 * 2   TMP Temporard<br>
 * 3   UNDEFINED   Undefined<br>
 * </p>
 */
public enum Requestlist_Purpose {

	EVENT("Event", "EVENT", 3),
	//TMP("Temp.", "TMP", 1,R.drawable.ic_listpurpose_temp),
	THEME("Theme", "THEME",2),
	UNDEFINED("Unknown", "UNDEFINED", 0);

	private final String Text;
	private final int Priority;
	private final String Code;

	Requestlist_Purpose(String iText, String iCode, int iPriority) {
		this.Text = iText;
		this.Code = iCode;
		this.Priority = iPriority;
	}

	public String getText() {
		return Text;
	}
	@SuppressWarnings("unused")
	public String getShortText() {
		return Text;
	}

	public String getCode() {
		return Code;
	}

	public int getPriority() {
		return Priority;
	}

	public static Requestlist_Purpose fromString(String text) {
		for (Requestlist_Purpose b : Requestlist_Purpose.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static Requestlist_Purpose fromCode(String tCode) {
		for (Requestlist_Purpose b : Requestlist_Purpose.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}

}

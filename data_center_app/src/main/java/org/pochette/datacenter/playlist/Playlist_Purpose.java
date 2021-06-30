package org.pochette.data_library.playlist;



/**
 * Playlist_Purpose describes the purpose of a Playlist<br>
 * String for database query included
 * <p>
 * 0   EVENT   Event related<br>
 * 1   PURPOSE Purpose based ???<br>
 * 2   TMP Temporard<br>
 * 3   UNDEFINED   Undefined<br>
 * </p>
 */
@SuppressWarnings({"FieldMayBeFinal", "unused"})
public enum Playlist_Purpose {

	EVENT("Event", "EVENT", 3),
	//TMP("Temp.", "TMP", 1,R.drawable.ic_listpurpose_temp),
	PURPOSE("Theme", "PURPOSE",2),
	UNDEFINED("Unknown", "UNDEFINED", 0);
	
	private String Text;
	private int Priority;
	private String Code;

	Playlist_Purpose(String iText, String iCode, int iPriority) {
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

	public static Playlist_Purpose fromString(String text) {
		for (Playlist_Purpose b : Playlist_Purpose.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static Playlist_Purpose fromCode(String tCode) {
		for (Playlist_Purpose b : Playlist_Purpose.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}

}

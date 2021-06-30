package org.pochette.data_library.scddb_objects;


/**
 * Enum for Favourite, a descriptive attribute to MusicFile
 * later perhaps also to dance
 * <p>
 * <p>
 * <p>
 * 20   HORR    Horrible <br>
 * 30   RANO    Rather not <br>
 * 45   UNKN    Unknown <br>
 * 50   NEUT    Neutral <br>
 * 60   OKAY    Okay <br>
 * 70   GOOD    Good <br>
 * 80   VYGO    Very Good <br>
 * </p>
 */
@SuppressWarnings({"FieldMayBeFinal", "unused"})
public enum DanceFavourite {
	HORRIBLE("Horrible", "HORR", 20),
	RATHER_NOT("Rather Not", "RANO", 30),
	NEUTRAL("Neutral", "NEUT", 50),
	UNKNOWN("Unkown", "UNKN", 45),
	OKAY("Okay", "OKAY", 60),
	GOOD("Good", "GOOD", 70),
	VERY_GOOD("Very Good", "VYGO", 80),;
	private String Text;
	private int Priority;
	private String Code;

	DanceFavourite(String iText, String iCode, int iPriority) {
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

	public static DanceFavourite fromString(String text) {
		for (DanceFavourite b : DanceFavourite.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static DanceFavourite fromCode(String tCode) {
		for (DanceFavourite b : DanceFavourite.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}
}

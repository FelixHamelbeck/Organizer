package org.pochette.data_library.music;


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
public enum MusicPreferenceFavourite {
	NOT_OK("Not Ok", "NOOK", 30),
	UNKNOWN("Unkown", "UNKN", 40),
	REOD("Odd Repeat", "REOD", 45),
	NEUTRAL("Neutral", "NEUT", 50),
	ANYG("Any Good", "ANYG", 55),
	REGO("Good Repeat", "REGO",65),
	OKAY("Okay", "OKAY", 60),
	GOOD("Good", "GOOD", 70),
	VERY_GOOD("Very Good", "VYGO", 80),
	TODAY("Today", "TODA", 100);
	private String Text;
	private int Priority;
	private String Code;

	MusicPreferenceFavourite(String iText, String iCode, int iPriority) {
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

	public static MusicPreferenceFavourite fromString(String text) {
		for (MusicPreferenceFavourite b : MusicPreferenceFavourite.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static MusicPreferenceFavourite fromCode(String tCode) {
		for (MusicPreferenceFavourite b : MusicPreferenceFavourite.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}
}

package org.pochette.data_library.music;


/**
 * MusicFilePurpose is a property of a MusicFile
 * <p>
 * <p>
 * 11    SCD     SCD <br>
 * 13    WUCD    Warmup & Cooldown <br>
 * 16    STEP    Step Practise <br>
 * 26    SCLI    Scottish_Listening <br>
 * 49    LISG    Listening <br>
 * 90   UNKN    Unkown <br>
 * <p>
 * </p>
 */
public enum MusicFilePurpose {
	SCD("SCD", "SCD", 11),
	WARMUP_COOLDOWN("Warmup & Cooldown", "WUCD", 13),
	STEP_PRACTISE("Step Practise", "STEP", 16),
	CELTIC_LISTENING("Celtic_Listening", "CELC", 26),
	LISTENING("Listening", "LISG", 49),
	UNKNOWN("Unkown", "UNKN", 90);
	private final String Text;
	private final int Priority;
	private final String Code;

	MusicFilePurpose(String iText, String iCode, int iPriority) {
		this.Text = iText;
		this.Code = iCode;
		this.Priority = iPriority;
	}

	public String getText() {
		return Text;
	}

	public String getCode() {
		return Code;
	}

	public int getPriority() {
		return Priority;
	}

	public static MusicFilePurpose fromString(String text) {
		for (MusicFilePurpose b : MusicFilePurpose.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static MusicFilePurpose fromCode(String tCode) {
		for (MusicFilePurpose b :MusicFilePurpose.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}
}

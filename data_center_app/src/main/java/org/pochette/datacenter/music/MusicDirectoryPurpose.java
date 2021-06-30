package org.pochette.data_library.music;


/**
 * MusicDirectoryPurpose is a property of a MusicDirectory
 * <p>
 * <p>
 * 11    SCD     SCD <br>
 * 13    WUCD    Warmup & Cooldown <br>
 * 16    STEP    Step Practise <br>
 * 19    CLMX    Class Mixed <br>
 * 26    SCLI    Scottish_Listening <br>
 * 49    LISG    Listening <br>
 * 90   UNKN    Unkown <br>
 * <p>
 * </p>
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public enum MusicDirectoryPurpose {
	SCD("SCD", "SCD", 11),
	WARMUP_COOLDOWN("Warmup & Cooldown", "WUCD", 13),
	STEP_PRACTISE("Step Practise", "STEP", 16),
	CLASS_MIXED("Mixed Class Purpose", "CLMX", 19),
	CELTIC_LISTENING("Celtic_Listening", "CELC", 26),
	LISTENING("Listening", "LISG", 49),

	UNKNOWN("Unkown", "UNKN", 90);
	private String Text;
	private int Priority;
	private String Code;

	MusicDirectoryPurpose(String iText, String iCode, int iPriority) {
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

	public static MusicDirectoryPurpose fromString(String text) {
		for (MusicDirectoryPurpose b : MusicDirectoryPurpose.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static MusicDirectoryPurpose fromCode(String tCode) {
		for (MusicDirectoryPurpose b : MusicDirectoryPurpose.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}
}

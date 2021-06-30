package org.pochette.data_library.pairing;


/**
 * Source of a Match object
 *
 *
 */

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public enum PairingSource {
	DIRECTORY_SIGNATURE("Directory Signature Match","DIRECTORY_SIGNATURE",90),
	DIRECTORY_SCORE("Directory Match by Score","DIRECTORY_SCORE", 80),
	BLANK("Blank","BLANK", 0),
	MANUAL("Manual Match","MANUAL", 50);

	private String Text;
	private int Priority;
	private String Code;

	PairingSource(String iText, String iCode, int iPriority) {
		this.Text = iText;
		this.Code = iCode;
		this.Priority = iPriority;
	}
	
	public String getText() {
		return Text;
	}
	public String getShortText() {
		return Text;
	}

	public String getCode() {
		return Code;
	}

	public int getPriority() {
		return Priority;
	}

	public static PairingSource fromString(String text) {
		for (PairingSource b : PairingSource.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static PairingSource fromCode(String tCode) {
		for (PairingSource b : PairingSource.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}

}

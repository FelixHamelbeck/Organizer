package org.pochette.data_library.pairing;

/**
 * Status of a Match object
 *
 *
 */

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public enum PairingStatus {
	CANDIDATE("Candidate","CANDIDATE", 50),
	CONFIRMED("Confirmed","CONFIRMED", 100),
	//BLANK("Blank","BLANK", 40, R.drawable.ic_matchstatus_none),
	//NON_SCDDB("Non Scddb","NON_SCDDB", 80,R.drawable.ic_blank), does not make sense
	REJECTED("Rejected","REJECTED", 30);

	private String Text;
	private int Priority;
	private String Code;

	PairingStatus(String iText, String iCode, int iPriority) {
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

	public static PairingStatus fromString(String text) {
		for (PairingStatus b : PairingStatus.values()) {
			if (b.Text.equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static PairingStatus fromCode(String tCode) {
		for (PairingStatus b : PairingStatus.values()) {
			if (b.Code.equals(tCode)) {
				return b;
			}
		}
		return null;
	}
}

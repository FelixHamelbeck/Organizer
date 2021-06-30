package org.pochette.data_library.pairing;


import org.pochette.utils_lib.logg.Logg;

/**
 * The Signature class represents the signature information. With the scoring method the closeness of albums and directories can be detected fairly well.
 *
 * The assumption is that the filename contains strings like R8x32, for a complete directory the signature is a concat of the single file's signatures
 * For recordings the signature is calculated from the database data, for an alum the signature is a concat of the the single recording's signatures
 * A count is stored at the beginning and the parts are separated by a comma
 *
 * A scoring mechanism is available to detect the closeness of two signatures
 *
 */
public class Signature {

	//Variables

	private static final String TAG = "FEHA (Signature)";
	@SuppressWarnings("FieldCanBeLocal")
//	private static String smPattern = "(.*)([hHjJmMrRsSwW]\\d+x\\d+)(.*)";
	private static String mPattern = "(.*)([hHjJmMrRsSwW]\\d+x\\d+)(.*)(.mp3)*+"; // "mp3" not required
	private static String mEmpty = "X0x00";
	public String mSignature;

	//Constructor
	public Signature(String mSignature) {
		this.mSignature = mSignature;
	}

	//Setter and Getter
	//Livecycle
	//Static Methods
	//Internal Organs

	private float calculateScore(String mSignature, String nSignature) {
		if (mSignature.equals(nSignature)) {
			return 1.f;
		}
		if (! mSignature.substring(0, 3).equals(nSignature.substring(0, 3))) {
			return 0.f;
		}
		String[] mParts = getParts(mSignature);
		String[] nParts = getParts(nSignature);
		return calculateScore(mParts, nParts);
	}


	//Interface
	public static float calculateScore(String[] mParts, String[] nParts) {
		float tScore = 0.f;
		if (!mParts[0].equals(nParts[0])) {
			return tScore;
		}
		int tCountAll;
		try {
			tCountAll = Integer.parseInt(mParts[0].trim());
		} catch (NumberFormatException e) {

			Logg.e(TAG, e.toString());
			Logg.i(TAG, mParts[0]);
			return -1.f;
		}
		int tCountFit = 0;
		int tCountMisfit = 0;
		int tCountNullfit = 0;
		for (int i = 1; i <= tCountAll; i++) {
			String mSig = mParts[i];
			String nSig = nParts[i];
			if (mSig.equals(nSig)) {
				tCountFit++;
			} else if (mSig.equals(mEmpty) || nSig.equals(mEmpty)) {
				tCountNullfit++;
			} else {
				tCountMisfit++;
			}
		}

		tScore = 0.5f
				+ (0.3f * tCountFit) / tCountAll
				- (0.2f * tCountNullfit) / tCountAll
				- (0.4f * tCountMisfit) / tCountAll;
		return tScore;
	}

	public static String[] getParts(String iSignature) {
		return iSignature.split(",");
	}

	/**
	 * This method compare the Signature to an other
	 * A score is calculated: A perfect fit corresponds to 1.0f
	 * Usually called with the signature of a directory to be compared to a signature of an album
	 *
	 * @param tSignature to be compared to self
	 * @return the score where 1.0f is perfect fit
	 */
	public float compare(Signature tSignature) {
		return calculateScore(mSignature, tSignature.mSignature);
	}




	/**
	 * Provide the regex Pattern applied
	 * @return  provide the regex pattern for signature recognition
	 */
	public static String getPattern() {
		return mPattern;
	}

	/**
	 * Provide the default signature
	 * @return the default signature, when the filename does not yield anything
	 */
	public static String getEmpty() {
		return mEmpty;
	}

}

package org.pochette.data_library.pairing;


import org.pochette.utils_lib.logg.Logg;

import java.util.regex.Pattern;

/**
 * The Signature class represents the signature information. With the scoring method the closeness of albums and directories can be detected fairly well.
 * <p>
 * The assumption is that the filename contains strings like R8x32, for a complete directory the signature is a concat of the single file's signatures
 * For recordings the signature is calculated from the database data, for an alum the signature is a concat of the the single recording's signatures
 * A count is stored at the beginning and the parts are separated by a comma
 * <p>
 * A scoring mechanism is available to detect the closeness of two signatures
 */
public class Signature {

    private static final String TAG = "FEHA (Signature)";
    //Variables
    private static final String mPattern = "(.*)([hHjJmMrRsSwW]\\d+x\\d+)(.*)(.mp3)*+"; // "mp3" not required
    private static final String mEmpty = "X0x00";
    private static final String mNonTrivial = "([hHjJmMrRsSwW])";
    private static final Pattern mNonTrivialPattern = Pattern.compile(mNonTrivial);
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
//		if (! mSignature.substring(0, 3).equals(nSignature.substring(0, 3))) {
//			return 0.f;
//		}
        try {

            String[] mParts = getParts(mSignature);
            String[] nParts = getParts(nSignature);
            return calculateScore(mParts, nParts);

        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            Logg.i(TAG, mSignature);
            Logg.i(TAG, nSignature);
            return 0.f;
        }
    }


    //Interface
    public static float calculateScore(String[] mParts, String[] nParts) {
        int tCountFit = 0;
        int tCountMisfit = 0;
        int tCountNullfit = 0;
        int tCountSkip = 0;
        float tScore = 0.f;


        //	if (!mParts[0].equals(nParts[0])) {
        // unequal numbers
        String[] bParts;
        String[] aParts;
        if (mParts.length > nParts.length) {
            bParts = mParts;
            aParts = nParts;
        } else {
            aParts = mParts;
            bParts = nParts;
        }
        int tCountAll = aParts.length;
        int tCounterDiff = bParts.length - aParts.length;
        if (tCounterDiff > 5) {
            return 0.f;
        }

        int iB = 1;
        String aSig;
        String bSig;

        for (int iA = 1; iA < aParts.length; iA++) {
            bSig = bParts[iB];
            aSig = aParts[iA];
            while (tCounterDiff > 0 && !bSig.equals(aSig)) {
                tCounterDiff--;
                tCountSkip++;
                iB++;
                bSig = bParts[iB];
            }


            if (bSig.equals(aSig)) {
                tCountFit++;
            } else if (aSig.equals(mEmpty) || bSig.equals(mEmpty)) {
                tCountNullfit++;
            } else {
                tCountMisfit++;
            }
            iB++;
        }


        //	}
//		int tCountAll;
//		try {
//			tCountAll = Integer.parseInt(mParts[0].trim());
//		} catch (NumberFormatException e) {
//			Logg.w(TAG, e.toString());
//			return -1.f;
//		}
//		int tCountFit = 0;
//		int tCountMisfit = 0;
//		int tCountNullfit = 0;
//		for (int i = 1; i <= tCountAll; i++) {
//			String mSig = mParts[i];
//			String nSig = nParts[i];
//			if (mSig.equals(nSig)) {
//				tCountFit++;
//			} else if (mSig.equals(mEmpty) || nSig.equals(mEmpty)) {
//				tCountNullfit++;
//			} else {
//				tCountMisfit++;
//			}
//		}

        // zou make call this parameters empirical
        tScore = 0.5f
                + (0.5f * tCountFit) / tCountAll
                - (0.2f * tCountNullfit) / tCountAll
                - (0.2f * tCountSkip) / tCountAll
                - (0.5f * tCountMisfit) / tCountAll;
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
     *
     * @return provide the regex pattern for signature recognition
     */
    public static String getPattern() {
        return mPattern;
    }

    /**
     * Provide the default signature
     *
     * @return the default signature, when the filename does not yield anything
     */
    public static String getEmpty() {
        return mEmpty;
    }


    public static boolean isTrivial(String iSignatureString) {
        return !mNonTrivialPattern.matcher(iSignatureString).find();
    }
}

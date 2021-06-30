package org.pochette.data_library.pairing;

/**
 * Status of a Task object
 * <p>
 * OPEN Open<br>
 * LOCKED   Locked<br>
 * SUCCESS Successfully Executed<br>
 * EXPIRED  Expired<br>
 * UNKNOWN  Unknown<br>
 * RETRY    Retry waiting<br>
 * </p>
 */

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public enum PairingObject {
    MUSIC_DIRECTORY("Album=Music_Directory", "MUSIC_DIRECTORY", "ALBUM", "LDB.MUSIC_DIRECTORY"),
    MUSIC_FILE("Recording=Music_File", "MUSIC_FILE", "RECORDING", "LDB.MUSIC_FILE");

    private final String mText;
    private final String mCode;
    private final String mScddb_Table;
    private final String mLdb_Table;

    PairingObject(String text, String code, String scddb_Table, String ldb_Table) {
        mText = text;
        mCode = code;
        mScddb_Table = scddb_Table;
        mLdb_Table = ldb_Table;
    }

    public String getText() {
        return mText;
    }

    public String getCode() {
        return mCode;
    }

    public static PairingObject getByCode(String tCode) {
        for (PairingObject t : PairingObject.values()) {
            if (t.mCode.equals(tCode)) {
                return t;
            }
        }
        return null;
    }

}

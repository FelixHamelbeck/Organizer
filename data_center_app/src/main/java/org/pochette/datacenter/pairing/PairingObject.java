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

@SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
public enum PairingObject {
    MUSIC_DIRECTORY("Album=Music_Directory", "MUSIC_DIRECTORY", "ALBUM", "LDB.MUSIC_DIRECTORY"),
    MUSIC_FILE("Recording=Music_File", "MUSIC_FILE", "RECORDING", "LDB.MUSIC_FILE");

    private String Text;
    private String Code;
    private String Scddb_Table;
    private String Ldb_Table;

    PairingObject(String text, String code, String scddb_Table, String ldb_Table) {
        Text = text;
        Code = code;
        Scddb_Table = scddb_Table;
        Ldb_Table = ldb_Table;
    }

    public String getText() {
        return Text;
    }

    public String getCode() {
        return Code;
    }

    public static PairingObject getByCode(String tCode) {
        for (PairingObject t : PairingObject.values()) {
            if (t.Code.equals(tCode)) {
                return t;
            }
        }
        return null;
    }

}

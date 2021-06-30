package org.pochette.data_library.database_management;

import android.database.sqlite.SQLiteDatabase;


import org.pochette.utils_lib.search.SearchPattern;

import java.util.Date;

public interface ManagementInterface {

    Ldb_Helper createLdbHelper();

    SQLiteDatabase getDatabase();

    // ScddbFile
    void downloadScddb();

    Date getLastWebdate();

    Date getLocalScddbDate();

    boolean isDbFileAvailable();

    // attach ...

    void attachLdbToScddb();

    // CRUD

    public <T> T readFirst(SearchPattern iSearchPattern);


}

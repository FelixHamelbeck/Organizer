package org.pochette.organizer.formation;


import org.pochette.data_library.scddb_objects.Formation;
import org.pochette.data_library.scddb_objects.FormationModifier;
import org.pochette.data_library.scddb_objects.FormationRoot;
import org.pochette.data_library.database_management.DataService;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * ScddbAlbum correspond to an album as available on scddb
 */
public class FormationData {

    //Variables
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static final String TAG = "FEHA (FormationData)";

    private static ArrayList<FormationRoot> mAL_FormationRoot;
    private static ArrayList<FormationModifier> mAL_FormationModifier;
    private static ArrayList<Formation> mAL_Formation;

    //Constructor
    public FormationData() {
        getAllFormatianRoot();
        getAllFormationModifier();
        getAllFormation();
    }

    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs

    ArrayList<FormationRoot> getAllFormatianRoot() {
        if (mAL_FormationRoot == null) {
            SearchPattern tSearchPattern = new SearchPattern(FormationRoot.class);
            DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
            DataService tDataService = tDataServiceSingleton.getDataService();
            mAL_FormationRoot = tDataService.readArrayList(tSearchPattern);
            Collections.sort(mAL_FormationRoot, new Comparator<FormationRoot>() {
                @Override
                public int compare(FormationRoot o1, FormationRoot o2) {
                    if (o1.mCountDances == o2.mCountDances) {
                        return o2.mName.compareToIgnoreCase(o1.mName);
                    } else {
                        return o2.mCountDances - o1.mCountDances;
                    }
                }
            });

        }
        return mAL_FormationRoot;
    }

    @SuppressWarnings("UnusedReturnValue")
    ArrayList<FormationModifier> getAllFormationModifier() {
        if (mAL_FormationModifier == null) {
            SearchPattern tSearchPattern = new SearchPattern(FormationModifier.class);
            DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
            DataService tDataService = tDataServiceSingleton.getDataService();
            mAL_FormationModifier = tDataService.readArrayList(tSearchPattern);
        }
        return mAL_FormationModifier;
    }

    ArrayList<Formation> getAllFormation() {
        if (mAL_Formation == null) {
            SearchPattern tSearchPattern = new SearchPattern(Formation.class);
            DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
            DataService tDataService = tDataServiceSingleton.getDataService();
            mAL_Formation = tDataService.readArrayList(tSearchPattern);
        }
        return mAL_Formation;
    }

    //Interface
    public Formation getFormationByKey(String iKey) {
        for (Formation lFormation : getAllFormation()) {
            if (lFormation.mKey.equals(iKey)) {
                return lFormation;
            }
        }
        return null;
    }


    public FormationRoot getFormationRootByKey(String iKey) {
        for (FormationRoot lFormationRoot : getAllFormatianRoot()) {
            if (lFormationRoot.mKey.equals(iKey)) {
                return lFormationRoot;
            }
        }
        return null;
    }


}
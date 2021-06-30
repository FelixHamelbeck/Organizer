package org.pochette.organizer.diagram;

import android.util.AndroidRuntimeException;

import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * The class GetDiagramTask organizes the Task of GetDiagram
 * <p>
 * Retrieval for authors  {"kr", "heiko", "scddb"} coded
 * <p>
 * Method execute performs the retrieval. It is usually called by TaskThread
 * <p>
 * Method create_Task (dance_id) stores the proper request into the Task queue
 */
public class DiagramTask {
    private static String TAG = "FEHA (DiagramTask)";

//    /**
//     * The method by which the DiagramManager is retrieved from the server
//     *
//     * @param jsonString request details
//     * @throws AndroidRuntimeException if anything is wrong with the input jsonString
//     */
//    public static void execute(String jsonString) throws AndroidRuntimeException {
//        Logg.d(TAG, "run" + jsonString);
//        if (jsonString == null || jsonString.equals("")) {
//            throw new AndroidRuntimeException("No parameter");
//        }
//        int DANCE_id = SearchCallDiagramTask.getScddbDanceId(jsonString);
//        try {
//            DiagramManager.download(DANCE_id);
//        } catch (FileNotFoundException e) {
//            Logg.e(TAG, e.toString());
//        }
//    }
//
//    /**
//     * Create task for the task thread to retrieve a diagramm from strathspey
//     * <p>
//     * If a reference to a diagram is already stored in the local database, do nothing, except when tForce = true
//     * <p>
//     * If a task is already pending in the queue, do nothing
//     * <p>
//     *
//     * @param iScddbDance_id ID of the Dance, for which diagrams are to be retrieved
//     * @param tForce         if true, a new diagramm is retrieved anyway, if available
//     */
//    public static void createTask(int iScddbDance_id, boolean tForce) {
//        Logg.i(TAG, "createTask: " + iScddbDance_id);
//        if (!tForce) {
//            // DiagramManager already stored?
//            SearchCriteria tSearchCriteria;
//            SearchPattern tSearchPattern;
//            SearchCall tSearchCall;
//            tSearchPattern = new SearchPattern(DiagramManager.class);
//            tSearchCriteria = new SearchCriteria("DANCE_ID", "" + iScddbDance_id);
//            tSearchPattern.addSearch_Criteria(tSearchCriteria);
//            tSearchCall = new SearchCall(DiagramManager.class, tSearchPattern, null);
//            DiagramManager tScddbDiagram = tSearchCall.produceFirst();
//            if (tScddbDiagram != null) {
//                Logg.i(TAG, "Already stored: " + tScddbDiagram.toString());
//                return;
//            }
//
//			// open task already available?
//            //does not work as SearchCriteria JSON is not precise
//
//            tSearchPattern = new SearchPattern(Task.class);
//            tSearchCriteria = new SearchCriteria("TASK_NAME", "GetDiagramTask");
//            tSearchPattern.addSearch_Criteria(tSearchCriteria);
//            tSearchCriteria = new SearchCriteria("JSON", "" + iScddbDance_id);
//            tSearchPattern.addSearch_Criteria(tSearchCriteria);
//            tSearchCriteria = new SearchCriteria("OPEN", "");
//            tSearchPattern.addSearch_Criteria(tSearchCriteria);
//            tSearchCall = new SearchCall(Task.class, tSearchPattern, null);
//            ArrayList<Task> tAR_Task = tSearchCall.produceArrayList();
//            for (Task lTask : tAR_Task) {
//                if (lTask != null && getScddbDanceId(lTask.mJsonString) == iScddbDance_id) {
//                    //Logg.i(TAG, "Task already present");
//                    //Logg.i(TAG, lTask.toString());
//                    Logg.i(TAG, "Task already present with " + lTask.mJsonString);
//                    return;
//                }
//            }
//        }
//        Logg.i(TAG, "new DiagramTask" + iScddbDance_id);
//        new Task(
//                0,
//                "DiagramTask",
//                SearchCallDiagramTask.class.getName(),
//                "execute",
//                getJsonString(iScddbDance_id),
//                90,
//                5);
//    }
//
//    static String getJsonString(int iScddbDance_Id) {
//        JSONObject tParamter = new JSONObject();
//        try {
//            tParamter.put("DANCE_id", iScddbDance_Id);
//            return tParamter.toString();
//        } catch (JSONException e) {
//            Logg.e(TAG, e.toString());
//            throw new IllegalArgumentException("Cannot convert ScddbDance_Id to json");
//        }
//    }
//
//
//
//
//
//    static int getScddbDanceId(String iJsonString) {
//        int tDANCE_id;
//        try {
//            JSONObject tParameter = new JSONObject(iJsonString);
//            tDANCE_id = tParameter.getInt("DANCE_id");
//        } catch (JSONException e) {
//            Logg.e(TAG, e.toString());
//            throw new IllegalArgumentException("No dance_id in JsonString");
//        }
//        return tDANCE_id;
//    }
//
//    /**
//     * This method created getDiagramTasks for all dances identified to be part of MyCollection
//     * Do not delete, it is call from Setting as defined in SettingControl
//     */
////    public static void createAllTasks() {
////        ArrayList<Integer> tAR_Id = Dance_DB.getConfirmedDanceIds();
////        if (tAR_Id != null) {
////            for (Integer lId : tAR_Id) {
////                SearchCallDiagramTask.createTask(lId, false);
////            }
////        }
////    }
//

}

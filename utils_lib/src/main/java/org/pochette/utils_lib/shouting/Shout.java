package org.pochette.utils_lib.shouting;


/**
 * Shout is a POJO Class to communicate between floors
 *
 * One Shout is a set of data to be sent from downstairs, where it is usually referered to as ShoutToCeiling
 * When received upstairs, it is usually refered to as ShoutFromFloor
 *
 * <p>
 *     mActor represents the sender of the shout, i.e. the classname of the sending object<br>
 *     mLastObject provides the name of object of the last actions, e.g. item<br>
 *     mLastAction provides the name of last action, e.g. deleted<br>
 *     mJsonString allows for flexible data storage, to be used sparingly<br>
 * </p>
 */
@SuppressWarnings("unused")
public class Shout {

	@SuppressWarnings({"FieldMayBeFinal", "unused"})
	private static String TAG = "FEHA (Shout)";

	//Variables
	public String mActor;
	public String mLastAction ;
	public String mLastObject ;
	public String mJsonString;

	@SuppressWarnings("rawtypes")
	private Class mStoredObjectClass;
	private Object mStoredObject;

	//Constructor
	public Shout(String tActor) {
		mActor = tActor;
		mLastAction = "";
		mLastObject = "";
		mJsonString = "";
	}

	public Shout(Shout iShout) {
		mActor = iShout.mActor;
		mLastAction = iShout.mLastAction;
		mLastObject = iShout.mLastObject;
		mJsonString = iShout.mJsonString;
		if (this.carriesObject()) {
			mStoredObjectClass = iShout.mStoredObjectClass;
			mStoredObject = iShout.mStoredObject;
		}
	}

	public Shout(String tActor, String tLastAction) {
		mActor = tActor;
		mLastAction = tLastAction;
		mLastObject = "";
		mJsonString = "";
	}


	public Shout(String tActor, String tLastAction, String tLastObject) {
		mActor = tActor;
		mLastAction = tLastAction;
		mLastObject = tLastObject;
		mJsonString = "";
	}

	//Setter and Getter
	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface

	/**
	 * Store any object in the shout. Both class and object reference is stored
	 * @param iObject to be stored
	 */
	public void storeObject(Object iObject) {
		mStoredObjectClass = iObject.getClass();
		mStoredObject = iObject;
	}

	/**
	 * Remove object in the shout.
	 */
	public void removeObject() {
		mStoredObjectClass = null;
		mStoredObject = null;
	}

	/**
	 * Return the stored object. This involves an un checked cast
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	public <T>  T returnObject() {
		if (mStoredObject == null) {
			return null;
		}
		return (T) mStoredObject;
	}

	/**
	 *
	 * @return true, if an object is stored
	 */
	public boolean carriesObject() {
		return mStoredObject != null && mStoredObjectClass != null;
	}

	public String toString(){
		return mActor +
				( (mLastAction == null || mLastAction.equals("") ? "" : " :"+mLastAction))+
				( (mLastObject == null || mLastObject.equals("") ? "" : " ["+mLastObject+"]"))+
				( (mJsonString == null || mJsonString.equals("") ? "" : "\n"+mJsonString));

	}

}
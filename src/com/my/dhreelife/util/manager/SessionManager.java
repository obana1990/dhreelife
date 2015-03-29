package com.my.dhreelife.util.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.ActivityCompat;

import com.my.dhreelife.root.LoginActivity;
import com.my.dhreelife.root.WelcomeActivity;
import com.my.dhreelife.util.ImageLoader;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Obana on 10/30/13.
 */
public class SessionManager{
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private Context _context;

	// Shared pref mode
	public static int PRIVATE_MODE = 0;
	// Sharedpref file name
	public static final String PREF_NAME = "session";
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String KEY_NAME = "name";
	public static final String KEY_PROFILE_URL = "profileurl";
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_EMAIL = "email";
	// Constructor
	@SuppressLint("CommitPrefEdits")
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String name, String userName,String userId,String profileURL){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_NAME, name);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.putString(KEY_PROFILE_URL, profileURL);
		// commit changes
		editor.commit();
	}


	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();

		user.put(KEY_NAME, pref.getString(KEY_NAME, null));
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
		user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
		// return user
		return user;
	}

	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything 
	 * */
	public boolean checkLogin(){
		// Check login status
		if(this.isLoggedIn()){
			//redirect to WelcomeActivity
			GeneralManager.authManager.userId = pref.getString(KEY_USER_ID,null);
			GeneralManager.authManager.profileURL = pref.getString(KEY_PROFILE_URL,null);
			Intent i = new Intent(_context, WelcomeActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			_context.startActivity(i);
			Activity activity = (Activity) _context;
			ActivityCompat.finishAffinity(activity);
			return true;
		}
		return false;
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		if(pref.getBoolean(IS_LOGIN, false))
			return true;
		return false;
	}
	/**
	 * Clear session details
	 * */
	public void logoutUser(String fbId){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		//clear device ID
		final SharedPreferences prefs = _context.getSharedPreferences(WelcomeActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.remove(String.valueOf(GeneralManager.authManager.userId)+WelcomeActivity.PROPERTY_REG_ID);
		editor.commit();
		
		//clear profile photo
		File imageFile = new File(_context.getFilesDir(),"tmp.jpg");
		imageFile.delete();
		
		// After logout redirect user to Login Activity
		Intent i = new Intent(_context, LoginActivity.class);

		// Add new Flag to start new Activity
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

		// Staring Login Activity
		_context.startActivity(i);
		Activity activity = (Activity) _context;
		ActivityCompat.finishAffinity(activity);
		ImageLoader imageLoader = new ImageLoader(_context);
		imageLoader.clearCache();
		
	}
}

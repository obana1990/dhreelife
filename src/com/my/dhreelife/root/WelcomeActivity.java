package com.my.dhreelife.root;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.my.dhreelife.KeepAliveService;
import com.my.dhreelife.R;
import com.my.dhreelife.fragment.CalendarActivity;
import com.my.dhreelife.fragment.ContactActivity;
import com.my.dhreelife.fragment.EventActivity;
import com.my.dhreelife.fragment.PublicActivity;
import com.my.dhreelife.fragment.SettingActivity;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.FriendManager;
import com.my.dhreelife.util.manager.GeneralManager;
import com.my.dhreelife.util.manager.SessionManager;

public class WelcomeActivity extends FragmentActivity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TAG = "GCMDemo";
	private GoogleCloudMessaging gcm;
	private Context context;

	private static String regid;
	private RequestQueue requestQueue;
 
	@SuppressLint({ "NewApi", "SdCardPath" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_welcome);
		context = this;
		startService(new Intent(getBaseContext(), KeepAliveService.class));
		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(this, R.raw.click, 1); 

		// read friend from shared preferences
		ConstantManager.friendManager = new FriendManager(this);
		ConstantManager.friendManager.readFriendFromSharedPreferences();

		//create the bundle to pass to each fragment
		Bundle bundle = new Bundle();
		bundle.putString("user_id",GeneralManager.authManager.userId);

		//initialize if no event reminder configuration found
		File f = new File(
				"/data/data/"+getApplicationContext().getPackageName()+"/shared_prefs/"+ConstantManager.EVENT_REMINDER);
		if(!f.exists())
		{
			SharedPreferences pref = getSharedPreferences(ConstantManager.EVENT_REMINDER, ConstantManager.PRIVATE_MODE);
			Editor editor = pref.edit();
			editor.putBoolean("ENABLED", true);
			editor.putLong("DURATION", 900000);
			editor.commit();
		}
		ConstantManager.CURRENT_TASK = ConstantManager.WELCOME_ACTIVITY_ID;

		if(GeneralManager.authManager.userId==null)
		{
			SharedPreferences pref = context.getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
			GeneralManager.authManager.userId = pref.getString(SessionManager.KEY_USER_ID,null);
			getIntent().putExtra("user_id", GeneralManager.authManager.userId);
		}



		if (checkPlayServices()) {
			//Toast.makeText(this, "Google Play Services supported.", Toast.LENGTH_LONG).show();
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			}
			else
			{
				//always update server for device id
				storeRegistrationId(context, regid);
			}
		}

		//		GCMRegistrar.checkDevice(this);
		//
		//		// Make sure the manifest was properly set - comment out this line
		//		// while developing the app, then uncomment it when it's ready.
		//		GCMRegistrar.checkManifest(this);



		//this.getWindow().setSoftInputMode(WindowManager.LayoutParams.);


		ActionBar.Tab tab;
		ActionBar actionBar = getActionBar();

		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(false);

		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(false);

		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create Contact Tab
		tab = actionBar.newTab().setTabListener(new ContactActivity());
		// Create your own custom icon
		tab.setIcon(R.drawable.contact_tab);
		//tab.setText("Contact");
		actionBar.addTab(tab);

		// Create Event Tab
		tab = actionBar.newTab().setTabListener(new EventActivity());
		// Create your own custom icon
		tab.setIcon(R.drawable.event_tab);
		//tab.setText("Event");
		actionBar.addTab(tab);
		actionBar.selectTab(tab);

		// Create Calendar Tab
		tab = actionBar.newTab().setTabListener(new CalendarActivity());
		// Create your own custom icon
		tab.setIcon(R.drawable.calendar_tab);
		//tab.setText("Calendar");
		actionBar.addTab(tab);

		// Create Public Tab
		tab = actionBar.newTab().setTabListener(new PublicActivity());
		// Create your own custom icon
		tab.setIcon(R.drawable.public_tab);
		//tab.setText("Public");
		actionBar.addTab(tab);

		// Create Setting Tab
		tab = actionBar.newTab().setTabListener(new SettingActivity());
		// Create your own custom icon
		tab.setIcon(R.drawable.setting_tab);
		//tab.setText("Setting");
		actionBar.addTab(tab);
		if(getIntent().getStringExtra("notification")!=null)
		{
			actionBar.setSelectedNavigationItem(3);
		}

		actionBar.setSelectedNavigationItem(1);
	}


	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	private void storeRegistrationId(final Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(String.valueOf(GeneralManager.authManager.userId)+PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();

		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();

		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("id",GeneralManager.authManager.userId);
		mParams.put("deviceid", regId);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_DEVICE_ID);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/updateDeviceId",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Public");

		//add the request into request queue
		requestQueue.add(volleyRequest);

	}

	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(WelcomeActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(this, "This device is not supporting Google Play Services.", Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(String.valueOf(GeneralManager.authManager.userId)+PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			//Toast.makeText(context, "Registration not found.",Toast.LENGTH_LONG).show();
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			//Toast.makeText(context, "App version changed.",Toast.LENGTH_LONG).show();
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(WelcomeActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/*
	 * register device ID
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {

			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					regid = gcm.register(ConstantManager.SENDER_ID);

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			protected void onPostExecute(Void result) {

				storeRegistrationId(context, regid);
			};
		}.execute(null, null, null);

	}


	public StringVolleyResponseListener createNewRequest(int taskId)
	{
		StringVolleyResponseListener stringVolleyResponseListener = new StringVolleyResponseListener()
		{
			@Override
			public void onStringResponse(String response)
			{
				int taskId = getTaskId();
				switch(taskId)
				{
				case ConstantManager.UPDATE_DEVICE_ID:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) {

							if(result.has("error"))
							{
								Toast.makeText(getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
							}


						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		};
		stringVolleyResponseListener.setTaskId(taskId); 
		return stringVolleyResponseListener;
	}

}

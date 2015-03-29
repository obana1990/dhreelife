package com.my.dhreelife.fragment;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.my.dhreelife.R;
import com.my.dhreelife.activity.AddNewEventActivity;
import com.my.dhreelife.activity.TagFriendActivity;
import com.my.dhreelife.root.PendingEventDetailsActivity;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.DatePickerDialogFragment;
import com.my.dhreelife.util.IncomingCall;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.TimePickerDialogFragment;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;


@SuppressLint("NewApi")
public class EventExtraInformationActivity extends Fragment implements ActionBar.TabListener {
	private Fragment mFragment;
	private String eventId;
	private boolean editing =false;

	private ProgressBar progressBar1 ;
	private TextView loadingText1 ;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");


	private Button btnLaunchEvent;
	private Button btnInviteFriends;
	private Button btnEditEvent;
	private Button btnSetAlarm;


	private EditText eventTitle;
	private EditText eventDescription;
	private EditText eventLocation;
	private Spinner eventDate;
	private Spinner eventTime;
	private EditText eventCreatedDate;

	//Time variable
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	private String oldTitle;
	private String oldDescription;
	private String oldTime;
	private String oldDate;
	private String oldLocation;

	private LinearLayout eventInfoContainer;

	private String invitationList;
	private RequestQueue requestQueue;

	private boolean alarmPresent = false;
	private int alarmId = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.EVENT_EXTRA_INFORMATION_ACTIVITY_ID;
		View v = inflater.inflate(R.layout.activity_extra_event_information, container, false);
		alarmPresent = false;
		//check if alarm present
		alarmId = getActivity().getIntent().getIntExtra("alarmId",-1);
		if(alarmId!=-1)
		{
			alarmPresent = true;
		}

		eventId = getActivity().getIntent().getStringExtra("eventId");
		progressBar1 = (ProgressBar) v.findViewById(R.id.eventBasicInfoPb);
		loadingText1 = (TextView) v.findViewById(R.id.eventBasicInfoLoadingTxt);


		eventTitle = (EditText) v.findViewById(R.id.eventTitle);
		eventDescription = (EditText) v.findViewById(R.id.eventDescription);
		eventLocation = (EditText) v.findViewById(R.id.eventLocation);
		eventDate = (Spinner) v.findViewById(R.id.eventDate);
		eventTime = (Spinner) v.findViewById(R.id.eventTime);
		eventCreatedDate = (EditText) v.findViewById(R.id.eventCreatedDate);

		btnLaunchEvent = (Button) v.findViewById(R.id.btnLaunchEvent);
		btnInviteFriends = (Button)v.findViewById(R.id.btnInviteFriends);
		btnEditEvent = (Button) v.findViewById(R.id.btnEditEvent);
		btnSetAlarm = (Button) v.findViewById(R.id.btnSetAlarm);
		eventInfoContainer = (LinearLayout)v.findViewById(R.id.lytEventInfoContainer);
		eventTime.setBackgroundResource(R.drawable.spinner_background);
		eventDate.setBackgroundResource(R.drawable.spinner_background);

		if(getActivity()!=null)
		{
			requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();
			//http call
			showProgress(true);

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id", eventId);

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.VIEW_EVENT);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"events/view",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("EventExtraInformation");

			//add the request into request queue
			requestQueue.add(volleyRequest);

		}


		btnLaunchEvent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);	
				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.activity_launch_event_dialog);

				//layout
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				dialog.getWindow().setAttributes(lp);

				dialog.show();


				Button dialogButtonuYes = (Button) dialog.findViewById(R.id.btnYes);
				Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnNo);
				// if button is clicked, close the custom dialog
				dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(requestQueue!=null)
						{
							showProgress(true);
							btnLaunchEvent.setEnabled(false);

							PendingEventDetailsActivity.refreshListOnFinish = true;

							//create params
							Map<String, String> mParams = new HashMap<String,String>();
							mParams.put("id",GeneralManager.authManager.userId);
							mParams.put("eid", eventId);

							//create error listener
							Response.ErrorListener errorListner = new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									if(getActivity()!=null)
										Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
								}
							};
							//create the listener on call back
							StringVolleyResponseListener listener = createNewRequest(ConstantManager.LAUNCH_EVENT);
							CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/launchEvent",mParams,listener,errorListner);

							//set tag
							volleyRequest.setTag("EventExtraInformation");

							//add the request into request queue
							requestQueue.add(volleyRequest);
						}
						dialog.dismiss();
					}
				});
				dialogButtonNo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
		if(alarmPresent)
		{
			btnSetAlarm.setBackgroundResource(R.drawable.alarm_off);
		}
		else
		{
			btnSetAlarm.setBackgroundResource(R.drawable.alarm);
		}
		btnSetAlarm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!alarmPresent)
				{
					//create reminder

					final Dialog dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.activity_create_new_reminder);

					//layout
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					dialog.getWindow().setAttributes(lp);

					dialog.show();


					Button dialogButtonYes = (Button) dialog.findViewById(R.id.btnNewReminderYes);
					Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnNewReminderNo);

					dialogButtonNo.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();

						}
					});

					dialogButtonYes.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							try
							{
								if(requestQueue!=null)
								{

									SharedPreferences pref = getActivity().getSharedPreferences(ConstantManager.EVENT_REMINDER, ConstantManager.PRIVATE_MODE);

									Calendar cal = Calendar.getInstance();
									cal.set(year, month, day, hour, minute);


									ContentResolver cr = getActivity().getContentResolver();
									ContentValues values = new ContentValues();
									//get the reminder time configuration

									values.put(Events.DTSTART, cal.getTimeInMillis()-pref.getLong("DURATION", 0));
									values.put(Events.DTEND, cal.getTimeInMillis()-pref.getLong("DURATION", 0)+10000);
									values.put(Events.TITLE, eventTitle.getText().toString()+" 's reminder");
									values.put(Events.DESCRIPTION, eventDescription.getText().toString());
									values.put(Events.CALENDAR_ID, ConstantManager.DEFAULT_CALENDAR_ID);


									values.put(Events.HAS_ALARM, true);

									//Get current timezone
									values.put(Events.EVENT_TIMEZONE,TimeZone.getDefault().getID());
									Uri uri = cr.insert(Events.CONTENT_URI, values);
									// get the event ID that is the last element in the Uri
									alarmId = Integer.parseInt(uri.getLastPathSegment());

									ContentValues reminders = new ContentValues();
									reminders.put(Reminders.EVENT_ID, alarmId);
									reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
									reminders.put(Reminders.MINUTES, 0);

									cr.insert(Reminders.CONTENT_URI, reminders);


									//create params
									Map<String, String> mParams = new HashMap<String,String>();
									mParams.put("id",GeneralManager.authManager.userId);
									mParams.put("eid", eventId);
									mParams.put("alarmId", String.valueOf(alarmId));

									//create error listener
									Response.ErrorListener errorListner = new Response.ErrorListener() {

										@Override
										public void onErrorResponse(VolleyError error) {
											if(getActivity()!=null)
												Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
										}
									};
									//create the listener on call back
									StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_ALARM_ID);
									CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/updateAlarmId",mParams,listener,errorListner);

									//set tag
									volleyRequest.setTag("EventExtraInformation");

									//add the request into request queue
									requestQueue.add(volleyRequest);

									//make changes to alarm = present
									alarmPresent = true;
									btnSetAlarm.setBackgroundResource(R.drawable.alarm_off);
									getActivity().getIntent().putExtra("alarmId",alarmId);
								}
								dialog.dismiss();
							}
							catch (Exception e)
							{
								dialog.dismiss();
								Toast.makeText(getActivity().getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
							}

						}
					});
				}
				else
				{
					final Dialog dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.activity_remove_calendar_entry);

					//layout
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					dialog.getWindow().setAttributes(lp);

					dialog.show();

					Button dialogButtonuYes = (Button) dialog.findViewById(R.id.btnRemoveCalendarEntryYes);
					Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnRemoveCalendarEntryNo);
					// if button is clicked, close the custom dialog
					dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							//remove calendar reminder 
							int rowDeleted = -1;
							try
							{
								rowDeleted = DeleteCalendarEntry(alarmId);
							}
							catch (IllegalArgumentException e)
							{
								dialog.dismiss();
								Toast.makeText(getActivity().getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
							}
							if(rowDeleted>0)
							{
								if(requestQueue!=null)
								{
									//set the alarm id to -1, no alarm present
									alarmId = -1;
									getActivity().getIntent().putExtra("alarmId",alarmId);

									alarmPresent = false;
									btnSetAlarm.setBackgroundResource(R.drawable.alarm);

									//create params
									Map<String, String> mParams = new HashMap<String,String>();
									mParams.put("id",GeneralManager.authManager.userId);
									mParams.put("eid", eventId);
									mParams.put("alarmId", String.valueOf(alarmId));

									//create error listener
									Response.ErrorListener errorListner = new Response.ErrorListener() {

										@Override
										public void onErrorResponse(VolleyError error) {
											if(getActivity()!=null)
												Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
										}
									};
									//create the listener on call back
									StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_ALARM_ID);
									CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/updateAlarmId",mParams,listener,errorListner);

									//set tag
									volleyRequest.setTag("EventExtraInformation");

									//add the request into request queue
									requestQueue.add(volleyRequest);


								}
								dialog.dismiss();
							}


						}
					});
					dialogButtonNo.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

				}

			}
		});

		btnEditEvent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!editing)
				{
					editing = true;
					btnEditEvent.setBackgroundResource(R.drawable.save);
					eventTitle.setEnabled(true);
					eventDescription.setEnabled(true);
					eventLocation.setEnabled(true);
					eventTime.setClickable(true);
					eventDate.setClickable(true);
					addListenerOnSpinner();
					eventLocation.setEnabled(true);

				}
				else
				{

					boolean wrongDateTimeFormat = false;
					try
					{
						//sdf.parse(eventNewDateAndTime);
					}
					catch(Exception e)
					{
						wrongDateTimeFormat = true;
					}

					if(!wrongDateTimeFormat)
					{
						editing = false;
						btnEditEvent.setBackgroundResource(R.drawable.edit);
						eventTitle.setEnabled(false);
						eventDescription.setEnabled(false);
						eventLocation.setEnabled(false);
						eventTime.setClickable(false);
						eventDate.setClickable(false);
						eventDate.setOnTouchListener(null);
						eventTime.setOnTouchListener(null);
						eventLocation.setEnabled(false);

						if(changesMade()&&requestQueue!=null)
						{
							//submit http
							showProgress(true);

							//create params
							String time = eventDate.getAdapter().getItem(0).toString()+" "+eventTime.getAdapter().getItem(0).toString()+":00";
							Map<String, String> mParams = new HashMap<String,String>();
							mParams.put("id", GeneralManager.authManager.userId);
							mParams.put("eid", eventId);
							mParams.put("title", eventTitle.getText().toString());
							mParams.put("description", eventDescription.getText().toString());
							mParams.put("description", eventDescription.getText().toString());
							mParams.put("location", eventLocation.getText().toString());
							mParams.put("time", time);

							//create error listener
							Response.ErrorListener errorListner = new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									if(getActivity()!=null)
										Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
								}
							};
							//create the listener on call back
							StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_EVENT);
							CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"events/edit",mParams,listener,errorListner);

							//set tag
							volleyRequest.setTag("EventExtraInformation");

							//add the request into request queue
							requestQueue.add(volleyRequest);

							btnEditEvent.setEnabled(false);
						}

					}
					else
					{
						Toast.makeText(getActivity().getApplicationContext(), "Wrong Date and Time format", Toast.LENGTH_LONG).show();
					}

				}

			}
		});

		btnInviteFriends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				Intent intent = new Intent(getActivity().getApplicationContext(),TagFriendActivity.class);
				intent.putExtra("userId", GeneralManager.authManager.userId);
				startActivityForResult(intent,GeneralManager.constantManager.INVITE_FRIENDS_TO_EVENT_ACTIVITY);
			}
		});


		return v;
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new EventExtraInformationActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);
		//if request queue is not null then cancel all
		if(requestQueue!=null)
			requestQueue.cancelAll("EventExtraInformation");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	public void showProgress(boolean show)
	{
		if(show)
		{
			progressBar1.setVisibility(View.VISIBLE);
			loadingText1.setVisibility(View.VISIBLE);
			eventInfoContainer.setVisibility(View.INVISIBLE);
		}
		else
		{
			progressBar1.setVisibility(View.GONE);
			loadingText1.setVisibility(View.GONE);
			eventInfoContainer.setVisibility(View.VISIBLE);
		}
	}



	public void getEventInfo()
	{		
		if(requestQueue!=null)
		{
			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("eid",eventId);

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_EVENT_INFO_FOR_RELAUNCH);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"events/getEventInfoForRelaunch",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("EventExtraInformation");

			//add the request into request queue
			requestQueue.add(volleyRequest);
		}

	}

	public void relaunchEvent(String eid,String eventName, String eventDescription, String eventLocation, String eventMaxAttendees,String eventAttendeesId,String eventAttendeesName)
	{
		Intent myIntent = new Intent(getActivity(), AddNewEventActivity.class);
		//pass user id to next activity
		myIntent.putExtra("relaunch", "1");
		myIntent.putExtra("eid", eid);
		myIntent.putExtra("eventName", eventName);
		myIntent.putExtra("eventDescription", eventDescription);
		myIntent.putExtra("eventLocation", eventLocation);
		myIntent.putExtra("eventMaxAttendees", eventMaxAttendees);
		myIntent.putExtra("eventAttendeesId", eventAttendeesId);
		myIntent.putExtra("eventAttendeesName", eventAttendeesName);
		startActivityForResult(myIntent,  GeneralManager.constantManager.CREATE_EVENT_ACTIVITY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == ConstantManager.OK && (requestCode ==  GeneralManager.constantManager.INVITE_FRIENDS_TO_EVENT_ACTIVITY)&&requestQueue!=null){
			invitationList = data.getExtras().getString("tagList");
			String [] temp = invitationList.split(",");

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("Eventuser[id]",GeneralManager.authManager.userId);
			mParams.put("Eventuser[eid]",eventId);

			for(int i=0;i<temp.length;i++)
			{
				mParams.put("Eventuser[invitation_list]["+String.valueOf(i)+"]",temp[i].trim());
			}

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.INVITE_FRIENDS_TO_EVENT);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/invite",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("EventExtraInformation");

			//add the request into request queue
			requestQueue.add(volleyRequest);

		}
		else if (resultCode == ConstantManager.OK && (requestCode == GeneralManager.constantManager.CREATE_EVENT_ACTIVITY)){
			Toast.makeText(getActivity().getApplicationContext(), "Event relaunched.", Toast.LENGTH_SHORT).show();
			getActivity().finish();
		}
	}



	public void addListenerOnSpinner() {

		eventTime.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				if(action==MotionEvent.ACTION_DOWN)
				{
					DialogFragment newFragment = new TimePickerDialogFragment(eventTime);
					newFragment.show(getActivity().getFragmentManager(), "timePicker");
					return true;
				}
				return true;
			}
		});

		eventDate.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				if(action==MotionEvent.ACTION_DOWN)
				{
					DialogFragment newFragment = new DatePickerDialogFragment(eventDate);
					newFragment.show(getActivity().getFragmentManager(), "timePicker");
					return true;
				}
				return true;
			}
		});
	}

	public boolean changesMade()
	{
		if(!oldTitle.equals(eventTitle.getText().toString()))
			return true;
		if(!oldDescription.equals(eventDescription.getText().toString()))
			return true;
		if(!oldLocation.equals(eventLocation.getText().toString()))
			return true;
		if(!oldTime.equals(eventTime.getAdapter().getItem(0).toString()))
			return true;
		if(!oldDate.equals(eventDate.getAdapter().getItem(0).toString()))
			return true;
		return false;
	}


	/*
	 * Delete an entry of calendar
	 */
	private int DeleteCalendarEntry(int entryID) throws IllegalArgumentException{
		int iNumRowsDeleted = 0;

		if(getActivity()!=null)
		{
			Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
			Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
			iNumRowsDeleted = getActivity().getContentResolver().delete(eventUri, null, null);
		}


		return iNumRowsDeleted;
	}

	/*
	 * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
	 */
	@SuppressWarnings("deprecation")
	private String getCalendarUriBase() {
		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor = null;
		if(getActivity()!=null)
		{
			try {
				managedCursor = getActivity().managedQuery(calendars, null, null, null, null);
			} catch (Exception e) {
				// eat
			}

			if (managedCursor != null) {
				calendarUriBase = "content://calendar/";
			} else {
				calendars = Uri.parse("content://com.android.calendar/calendars");
				try {
					managedCursor = getActivity().managedQuery(calendars, null, null, null, null);
				} catch (Exception e) {
					// eat
				}

				if (managedCursor != null) {
					calendarUriBase = "content://com.android.calendar/";
				}

			}
		}

		return calendarUriBase;
	}


	public StringVolleyResponseListener createNewRequest(int taskId)
	{
		StringVolleyResponseListener stringVolleyResponseListener = new StringVolleyResponseListener()
		{
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onStringResponse(String response)
			{
				if(getActivity()!=null)
				{
					int taskId = getTaskId();
					showProgress(false);
					switch(taskId)
					{
					case ConstantManager.VIEW_EVENT:

						try {
							JSONObject result = new JSONObject(response);
							if(result!=null)
							{
								if(!result.has("error"))
								{
									btnEditEvent.setEnabled(true);
									btnSetAlarm.setEnabled(true);
									btnSetAlarm.setVisibility(View.VISIBLE);
									eventTitle.setText(result.getString("name"));
									eventDescription.setText(result.getString("description"));
									eventLocation.setText(result.getString("location"));


									try {
										SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										Date date = stringToDate.parse(result.getString("time"));

										eventCreatedDate.setText("Created "+dateFormat.format(date));

										Calendar cal = Calendar.getInstance();
										cal.setTime(date);

										//initialize the date and time
										year = cal.get(Calendar.YEAR);
										month = cal.get(Calendar.MONTH);
										day = cal.get(Calendar.DAY_OF_MONTH);

										hour = cal.get(Calendar.HOUR);
										minute = cal.get(Calendar.MINUTE);

										SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd"); 
										SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm"); 

										List<String> list = new ArrayList<String>();
										list.add(dateFormat2.format(cal.getTime()));
										ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
												android.R.layout.simple_spinner_item, list);
										eventTime.setAdapter(dataAdapter);

										List<String> list2 = new ArrayList<String>();
										list2.add(dateFormat1.format(cal.getTime()));
										ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity().getApplicationContext(),
												android.R.layout.simple_spinner_item, list2);
										eventDate.setAdapter(dataAdapter2);

										//save the old record for checking later
										oldTitle = result.getString("name");
										oldDescription = result.getString("description");
										oldLocation = result.getString("location");
										oldTime = dateFormat2.format(cal.getTime());
										oldDate = dateFormat1.format(cal.getTime());
									} catch (ParseException e1) {
										e1.printStackTrace();
									}

									int privacy = 0;
									try
									{
										privacy = Integer.parseInt(result.getString("privacy"));
									}
									catch (Exception e)
									{

									}
									eventCreatedDate.setText(eventCreatedDate.getText().toString()+"-By "+result.getString("owner"));
									if(result.getString("status").equals("0"))
									{
										eventCreatedDate.setText(eventCreatedDate.getText().toString()+"-On going");
									}
									else
									{
										eventCreatedDate.setText(eventCreatedDate.getText().toString()+" Expired");
										//change the launch event button to relaunch event
										btnLaunchEvent.setBackgroundResource(R.drawable.relaunch_event_button);
										btnLaunchEvent.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
												getEventInfo();

											}
										});
									}


									// only owner can launch event and invite friends.
									if(!result.getString("owner_id").equals(GeneralManager.authManager.userId))
									{
										btnLaunchEvent.setEnabled(false);
										btnLaunchEvent.setVisibility(View.INVISIBLE);
										btnEditEvent.setEnabled(false);
										btnEditEvent.setVisibility(View.INVISIBLE);

										// if open event only allow user to invite friends
										if(privacy ==1)
										{
											btnInviteFriends.setEnabled(true);
											btnInviteFriends.setVisibility(View.VISIBLE);
										}
										else
										{
											btnInviteFriends.setEnabled(false);
											btnInviteFriends.setVisibility(View.GONE);
										}
									}
									else
									{
										btnLaunchEvent.setVisibility(View.VISIBLE);
										btnLaunchEvent.setEnabled(true);
										btnEditEvent.setVisibility(View.VISIBLE);
										btnEditEvent.setEnabled(true);

										btnInviteFriends.setVisibility(View.VISIBLE);
										btnInviteFriends.setEnabled(true);
									}
								}
								else
								{
									if(getActivity()!=null)
										Toast.makeText(getActivity().getApplicationContext(),result.getString("error") , Toast.LENGTH_LONG).show();
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;
					case ConstantManager.LAUNCH_EVENT:

						try {
							JSONObject result = new JSONObject(response);
							if (result != null) {
								try {
									if(!result.has("error")) 
									{
										btnLaunchEvent.setVisibility(View.VISIBLE);
										btnLaunchEvent.setEnabled(true);
										//call event to refresh the event list
										IncomingCall incomingCall = (IncomingCall) GeneralManager.constantManager.CURRENT_FRAGMENT_PARENT;
										incomingCall.onCall();
										getActivity().finish();
									}
									else
									{
										if(getActivity()!=null)
											Toast.makeText(getActivity().getApplicationContext(),result.getString("error") , Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;
					case ConstantManager.INVITE_FRIENDS_TO_EVENT:

						try {
							JSONObject result = new JSONObject(response);
							if (result != null) {
								try {
									if(!result.has("error")) 
									{
										Toast.makeText(getActivity().getApplicationContext(),result.getString("message") , Toast.LENGTH_LONG).show();
									}
									else
									{

										Toast.makeText(getActivity().getApplicationContext(),result.getString("error") , Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;
					case ConstantManager.GET_EVENT_INFO_FOR_RELAUNCH:

						try {
							JSONObject result = new JSONObject (response);
							if(result!=null)
							{
								if(!result.has("error")) 
								{
									String eventName = result.getString("name");
									String eventLocation = result.getString("location");
									String eventDescription = result.getString("description");
									String eventMaxAttendees = result.getString("maxattendees");
									String attendeesId = result.getString("attendeesid");
									String attendeesName = result.getString("attendeesname");
									String eid = result.getString("id");
									relaunchEvent(eid,eventName,eventDescription,eventLocation,eventMaxAttendees,attendeesId,attendeesName);
								}
								else
								{
									if(getActivity()!=null)
										Toast.makeText(getActivity().getApplicationContext(),result.getString("error") , Toast.LENGTH_LONG).show();
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;
					case ConstantManager.UPDATE_EVENT:
						btnEditEvent.setEnabled(true);
						try {
							JSONObject result = new JSONObject(response);
							if(result!=null)
							{
								if(!result.has("error")) 
								{
									if(getActivity()!=null)
										Toast.makeText(getActivity().getApplicationContext(),"Event updated" , Toast.LENGTH_LONG).show();
									//call event to refresh the event list
									IncomingCall incomingCall = (IncomingCall) GeneralManager.constantManager.CURRENT_FRAGMENT_PARENT;
									incomingCall.onCall();
								}
								else
								{
									if(getActivity()!=null)
										Toast.makeText(getActivity().getApplicationContext(),result.getString("error") , Toast.LENGTH_LONG).show();
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;
					case ConstantManager.UPDATE_ALARM_ID:
						try {
							JSONObject result = new JSONObject(response);
							if(result!=null)
							{
								if(result.has("error"))
								{
									if(getActivity()!=null)
										Toast.makeText(getActivity().getApplicationContext(),result.getString("error") , Toast.LENGTH_LONG).show();
								}
								else
								{
									if(getActivity()!=null)
									{
										Toast.makeText(getActivity().getApplicationContext(),result.getString("message") , Toast.LENGTH_LONG).show();
										getActivity().getIntent().putExtra("alarmId",alarmId);
									}
								}
								//call event to refresh the event list
								IncomingCall incomingCall = (IncomingCall) GeneralManager.constantManager.CURRENT_FRAGMENT_PARENT;
								incomingCall.onCall();
							}
						} catch (JSONException e) {
							if(getActivity()!=null)
								Toast.makeText(getActivity().getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
						break;	
					}


				}
			}
		};
		stringVolleyResponseListener.setTaskId(taskId); 
		return stringVolleyResponseListener;
	}

}

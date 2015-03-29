package com.my.dhreelife.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class AddNewEventActivity extends Activity{


	//UI
	private Spinner txtChangeDate;
	private Button createEvent;
	private Spinner tagFriend;
	private LinearLayout loadingForm;
	private LinearLayout createEventForm;
	private EditText eventTitle;
	private EditText eventLocation;
	private EditText eventDescription;
	private EditText eventMaxAttendees;
	private Spinner txtChangeTime;
	private boolean firedUpActivityToTagFriend = false;
	private boolean firedUpActivityToTagGroup = false;
	private boolean refreshNeeded = false;
	private Button backButton;
	private CheckBox privacyCheckBox;
 
	//Time variable
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private String mediterian;

	//Constant
	private static final int TIME_DIALOG_ID = 998;
	private static final int DATE_DIALOG_ID = 999;
	private final int TAG_FRIEND_ACTIVITY = 950;
	private final int TAG_GROUP_ACTIVITY = 951;
	private final int OK = 1;
	private final int GET_LOCATION = 800;

	//operation specific variable
	private String tagFriendList="";
	private String tagGroupList="";
	private Context context ;
	private String latitude;
	private String longtitude;
	private Spinner tagGroup;
	private boolean timeSet = false;
	private boolean dateSet = false;
	private int eventPrivacy = 0;
	private RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_add_new_event2);

		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();
		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);

		ConstantManager.CURRENT_TASK = ConstantManager.ADD_NEW_EVENT_ACTIVITY_ID;
		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(this, R.raw.click, 1); 
		context = this;

		//setting up the listener for time + date spinner
		addListenerOnButton();
		//set the current time and date
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR);
		minute = c.get(Calendar.MINUTE);

		//set the current time and date as default event time and date
		List<String> list = new ArrayList<String>();
		list.add(String.valueOf(hour)+":"+String.valueOf(minute));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, list);
		txtChangeTime.setAdapter(dataAdapter);
		timeSet = true;

		List<String> list2 = new ArrayList<String>();
		list2.add(String.valueOf(month+1)+"-"+String.valueOf(day)+"-"+String.valueOf(year));
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, list2);
		txtChangeDate.setAdapter(dataAdapter2);
		dateSet = true;

		//initialize the form for loading progress
		loadingForm = (LinearLayout) findViewById(R.id.progressForm);
		createEventForm = (LinearLayout) findViewById(R.id.createEventForm);
		privacyCheckBox = (CheckBox) findViewById(R.id.chkBoxEventPrivacy);

		backButton = (Button) findViewById(R.id.createEventBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});

		privacyCheckBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(privacyCheckBox.isChecked())
					eventPrivacy = 1;
			}
		});

		eventTitle = (EditText)findViewById(R.id.event_title);
		eventLocation= (EditText)findViewById(R.id.event_location);
		eventDescription= (EditText)findViewById(R.id.event_description);
		eventMaxAttendees= (EditText)findViewById(R.id.event_max_attendees);


		tagGroup = (Spinner) findViewById(R.id.tagGroup);
		tagGroup.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!firedUpActivityToTagGroup)
				{
					firedUpActivityToTagGroup = true;
					Intent intent = new Intent(context,TagGroupActivity.class);
					intent.putExtra("userId", GeneralManager.authManager.userId);
					startActivityForResult(intent,TAG_GROUP_ACTIVITY);
					return true;
				}

				return false;
			}
		});

		tagFriend = (Spinner) findViewById(R.id.spnrTagFriend);
		tagFriend.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!firedUpActivityToTagFriend)
				{
					firedUpActivityToTagFriend = true;
					Intent intent = new Intent(context,TagFriendActivity.class);
					intent.putExtra("userId", GeneralManager.authManager.userId);
					startActivityForResult(intent,TAG_FRIEND_ACTIVITY);
					return true;
				}

				return false;
			}
		});

		//check if it's relaunch
		final String relaunch = getIntent().getStringExtra("relaunch");
		if(relaunch!=null&&relaunch.equals("1"))
		{
			eventTitle.setText(getIntent().getStringExtra("eventName"));
			eventLocation.setText(getIntent().getStringExtra("eventLocation"));
			eventDescription.setText(getIntent().getStringExtra("eventDescription"));
			eventMaxAttendees.setText(getIntent().getStringExtra("eventMaxAttendees"));
			List<String> list3 = new ArrayList<String>();
			list3.add(getIntent().getStringExtra("eventAttendeesName"));
			ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(context,
					android.R.layout.simple_spinner_item, list3);
			tagFriend.setAdapter(dataAdapter3);
			tagFriendList =getIntent().getStringExtra("eventAttendeesId");
		}

		String sponsorEvent = getIntent().getStringExtra("sponsorEvent");
		if(sponsorEvent!=null&&sponsorEvent.equals("1"))
		{

			eventTitle.setText(getIntent().getStringExtra("eventName"));
			eventLocation.setText(getIntent().getStringExtra("eventLocation"));
			eventDescription.setText(getIntent().getStringExtra("eventDescription"));

			eventTitle.setEnabled(false);
			eventDescription.setEnabled(false);
			eventLocation.setEnabled(false);
		}

		//set on click for create event
		createEvent = (Button)findViewById(R.id.create_event);
		createEvent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				if(timeSet&&dateSet&&!eventTitle.getText().toString().equals("")&&!eventTitle.getText().toString().equals("")&&!eventDescription.getText().toString().equals("")&&
						!eventMaxAttendees.getText().toString().equals("")&&!eventLocation.getText().toString().equals(""))
				{
					if(relaunch!=null&&relaunch.equals("1"))
						sendRelaunchEventRequest();
					else
						sendCreateEventRequest();
				}
				else 
				{
					Toast.makeText(context, "Please fill in all the information.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	//Time and Date part
	private DatePickerDialog.OnDateSetListener datePickerListener 
	= new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			Date tmpDate = cal.getTime();

			String temp = dateFormat.format(tmpDate);

			List<String> list = new ArrayList<String>();
			list.add(temp);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_spinner_item, list);
			txtChangeDate.setAdapter(dataAdapter);
			dateSet = true;
		}
	};

	public void addListenerOnButton() {
		txtChangeTime = (Spinner) findViewById(R.id.spnrChangeTime);
		txtChangeDate = (Spinner) findViewById(R.id.spnrChangeDate);
		txtChangeTime.setOnTouchListener(new View.OnTouchListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(TIME_DIALOG_ID);
				return true;
			}
		});


		txtChangeDate.setOnTouchListener(new View.OnTouchListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(DATE_DIALOG_ID);
				return true;
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, 
					timePickerListener, hour, minute,false);
		case DATE_DIALOG_ID:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, 
					year, month,day);
		}
		return null;
	}



	private TimePickerDialog.OnTimeSetListener timePickerListener = 
			new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;

			if(hour>=12)
			{
				mediterian = "pm";
			}
			else
				mediterian = "am";

			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); 
			Date tmpDate = new Date();
			tmpDate.setHours(hour);
			tmpDate.setMinutes(minute);
			tmpDate.setSeconds(0);
			String temp = dateFormat.format(tmpDate.getTime());

			List<String> list = new ArrayList<String>();
			list.add(temp);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_spinner_item, list);
			txtChangeTime.setAdapter(dataAdapter);
			timeSet = true;

		}
	};

	public void showProgress(boolean show)
	{
		if(show)
		{
			createEvent.setEnabled(false);
			loadingForm.setVisibility(View.VISIBLE);
			createEventForm.setVisibility(View.GONE);
		}
		else
		{
			loadingForm.setVisibility(View.GONE);
			createEventForm.setVisibility(View.VISIBLE);
			createEvent.setEnabled(true);
		}
	}



	@Override
	public void finish() {
		if(refreshNeeded)
		{
			// Prepare data intent 
			Intent data = new Intent();
			data.putExtra("REFRESH", "TRUE");
			// Activity finished ok, return the data
			setResult(OK, data);
		}
		super.finish();
	}

	public void sendCreateEventRequest()
	{
		showProgress(true);
		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("id",GeneralManager.authManager.userId); 
		mParams.put("Event[owner_id]",GeneralManager.authManager.userId); 
		mParams.put("Event[time][month]",String.valueOf(month+1));
		mParams.put("Event[time][day]",String.valueOf(day)); 
		mParams.put("Event[time][year]",String.valueOf(year)); 
		mParams.put("Event[time][hour]",String.valueOf(hour)); 
		mParams.put("Event[time][min]",String.valueOf(minute)); 

		mParams.put("Event[name]",eventTitle.getText().toString()); 
		mParams.put("Event[location]",eventLocation.getText().toString());
		mParams.put("Event[description]",eventDescription.getText().toString());
		mParams.put("Event[maxattendees]",eventMaxAttendees.getText().toString());
		mParams.put("Event[privacy]",String.valueOf(eventPrivacy));


		Set<String> set = new HashSet<String>();
		if(!tagFriendList.equals(""))
		{
			String [] temp = tagFriendList.split(",");
			for(int i=0;i<temp.length;i++)
			{
				set.add(temp[i].trim());
			}
		}
		if(!tagGroupList.equals(""))
		{
			String [] temp = tagGroupList.split(",");
			for(int i=0;i<temp.length;i++)
			{
				set.add(temp[i].trim());
			}
		} 
		int counter = 0;
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext()){
			String id = (String) iterator.next();
			mParams.put("Event[tag_friends]["+String.valueOf(counter)+"]",id);
			counter++;
		}




		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.ADD_NEW_EVENT_ACTIVITY);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"events/createEvent",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("AddNewEvent");

		//add the request into request queue
		requestQueue.add(volleyRequest);


	}


	public void sendRelaunchEventRequest()
	{
		showProgress(true);
		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("Event[id]",getIntent().getStringExtra("eid")); 
		mParams.put("Event[owner_id]",GeneralManager.authManager.userId); 
		mParams.put("Event[time][month]",String.valueOf(month));
		mParams.put("Event[time][day]",String.valueOf(day)); 
		mParams.put("Event[time][year]",String.valueOf(year)); 
		mParams.put("Event[time][hour]",String.valueOf(hour)); 
		mParams.put("Event[time][min]",String.valueOf(minute)); 
		mParams.put("Event[name]",eventTitle.getText().toString()); 
		mParams.put("Event[location]",eventLocation.getText().toString());
		mParams.put("Event[description]",eventDescription.getText().toString());
		mParams.put("Event[maxattendees]",eventMaxAttendees.getText().toString());
		mParams.put("Event[privacy]",String.valueOf(eventPrivacy));
		int counter = 0;

		// combine all tag list into one
		@SuppressWarnings("rawtypes")
		HashSet taggedFriend = new HashSet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = taggedFriend.iterator();
		while(iterator.hasNext())
		{
			mParams.put("Event[tag_friends]["+String.valueOf(counter)+"]",String.valueOf(iterator.next()));
			counter++;
		}
		if(!tagFriendList.equals(""))
		{
			String [] temp = tagFriendList.split(",");
			for(int i=0;i<temp.length;i++)
			{
				mParams.put("Event[tag_friends]["+String.valueOf(counter)+"]",temp[i].trim());
				counter++;
			}
		}


		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.RELAUNCH_EVENT_ACTIVITY);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"events/relaunchEvent",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Public");

		//add the request into request queue
		requestQueue.add(volleyRequest);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == OK && requestCode == GET_LOCATION) {
			if (data.hasExtra("longtitude")&&data.hasExtra("latitude")) {
				//Toast.makeText(this,"longtitude = "+data.getExtras().getString("longtitude")+" , latitude = "+data.getExtras().getString("latitude"),Toast.LENGTH_LONG).show();
				latitude = data.getExtras().getString("latitude");
				longtitude =data.getExtras().getString("longtitude");
			}
		}
		else if(resultCode == OK && requestCode == TAG_FRIEND_ACTIVITY)
		{
			List<String> list = new ArrayList<String>();
			list.add(data.getExtras().getString("tagListNameOnly"));
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_spinner_item, list);
			tagFriend.setAdapter(dataAdapter);
			firedUpActivityToTagFriend = false;
			tagFriendList = data.getExtras().getString("tagList");
		}
		else if(resultCode == OK && requestCode == TAG_GROUP_ACTIVITY)
		{
			List<String> list = new ArrayList<String>();
			list.add(data.getExtras().getString("tagListNameOnly"));
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_spinner_item, list);
			tagGroup.setAdapter(dataAdapter);
			firedUpActivityToTagGroup = false;
			tagGroupList = data.getExtras().getString("tagList");
		}
	}


	public StringVolleyResponseListener createNewRequest(int taskId)
	{
		StringVolleyResponseListener stringVolleyResponseListener = new StringVolleyResponseListener()
		{
			@Override
			public void onStringResponse(String response)
			{
				int taskId = getTaskId();
				showProgress(false);
				switch(taskId)
				{
				case ConstantManager.ADD_NEW_EVENT_ACTIVITY:
				case ConstantManager.RELAUNCH_EVENT_ACTIVITY:


					try {
						JSONObject result = new JSONObject(response);
						if (result != null) {
							try {
								if(result.has("error"))
								{
									Toast.makeText(getBaseContext(), "New group added.", Toast.LENGTH_LONG).show();
								}
								else
								{
									Toast.makeText(context, result.getString("message"), Toast.LENGTH_LONG).show();
									refreshNeeded = true;
								}
								//refresh calendar entries
								finish();
							} catch (JSONException e) {
								e.printStackTrace();
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

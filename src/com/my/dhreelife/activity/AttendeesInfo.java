package com.my.dhreelife.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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


public class AttendeesInfo extends Activity {

	private LinearLayout linearLayout1;
	private LinearLayout linearLayout2;
	private LinearLayout progressLayout;
	private String eventId;
	private Button backButton;
	private RequestQueue requestQueue;
	private Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//remove title bar 
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_attendees_info);
		
		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);


		linearLayout1 = (LinearLayout) findViewById(R.id.attendees_layout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.attendees_layout2);
		progressLayout = (LinearLayout) findViewById(R.id.attendeesLoadingProgressLayout);

		Bundle extras = getIntent().getExtras();
		if(extras!=null)
		{
			eventId = extras.getString("eventId");
			showProgress(true);
			
			//initialize the request queue object
			requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();
			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",eventId);

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.ATTENDEES_INFO);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/getAttendeesCount",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Public");

			//add the request into request queue
			requestQueue.add(volleyRequest);




		}

		backButton = (Button) findViewById(R.id.attendeesInfoBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}


	public void showProgress(boolean show)
	{
		if(show)
		{
			progressLayout.setVisibility(View.VISIBLE);
			linearLayout1.setVisibility(View.GONE);
			linearLayout2.setVisibility(View.GONE);
		}
		else
		{
			progressLayout.setVisibility(View.GONE);
			linearLayout1.setVisibility(View.VISIBLE);
			linearLayout2.setVisibility(View.VISIBLE);
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
						case ConstantManager.ATTENDEES_INFO:
							
							try {
								JSONObject result = new JSONObject(response);
								if(result!=null)
								{
									if(!result.has("error"))
									{
										JSONArray goingAttendees = result.getJSONArray("going");
										JSONArray maybeAttendees = result.getJSONArray("maybe");
										JSONArray notGoingAttendees = result.getJSONArray("notgoing");
										JSONArray pendingAttendees = result.getJSONArray("pending");

										//set the max attendees
										String maxattendeesCount = result.getString("maxattendees");
										TextView maxAttendees = (TextView)findViewById(R.id.txtMaxAttendee);
										maxAttendees.setText("Max Attendees : "+maxattendeesCount);
										maxAttendees.setTextColor(Color.BLACK);

										LinearLayout ln1 = (LinearLayout) findViewById(R.id.attendeesGoingLayout);
										//set the number of people in this group
										int temp = goingAttendees.length();
										TextView goingAttendeesCount = (TextView) findViewById(R.id.txtAttendeeGoing);
										goingAttendeesCount.setText("Going ("+ String.valueOf(temp)+")");
										goingAttendeesCount.setTextColor(Color.BLACK);

										for(int i=0;i<goingAttendees.length();i++)
										{
											TextView attendeesName = new TextView(context);
											attendeesName.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
											attendeesName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
											JSONObject object =  (goingAttendees.getJSONObject(i)).getJSONObject("Eventuser");
											attendeesName.setText(object.getString("name"));
											attendeesName.setTextColor(Color.BLACK);
											ln1.addView(attendeesName);
										}

										LinearLayout ln3 = (LinearLayout) findViewById(R.id.attendeesNotGoingLayout);
										//set the number of people in this group
										temp = notGoingAttendees.length();
										TextView notGoingAttendeesCount = (TextView) findViewById(R.id.txtAttendeeNotGoing);
										notGoingAttendeesCount.setText("Not Going ("+ String.valueOf(temp)+")");
										notGoingAttendeesCount.setTextColor(Color.BLACK);

										for(int i=0;i<notGoingAttendees.length();i++)
										{
											TextView attendeesName = new TextView(context);
											attendeesName.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
											attendeesName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
											attendeesName.setTextColor(Color.BLACK);
											JSONObject object =  (notGoingAttendees.getJSONObject(i)).getJSONObject("Eventuser");
											attendeesName.setText(object.getString("name"));
											ln3.addView(attendeesName);
										}
										LinearLayout ln4 = (LinearLayout) findViewById(R.id.attendeesPendingLayout);
										//set the number of people in this group
										temp = pendingAttendees.length();
										TextView pendingAttendeessCount = (TextView) findViewById(R.id.txtAttendeePending);
										pendingAttendeessCount.setText("Pending ("+ String.valueOf(temp)+")");
										pendingAttendeessCount.setTextColor(Color.BLACK);

										for(int i=0;i<pendingAttendees.length();i++)
										{
											TextView attendeesName = new TextView(context);
											attendeesName.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
											attendeesName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
											attendeesName.setTextColor(Color.BLACK);
											JSONObject object =  (pendingAttendees.getJSONObject(i)).getJSONObject("Eventuser");
											attendeesName.setText(object.getString("name"));
											ln4.addView(attendeesName);
										}
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
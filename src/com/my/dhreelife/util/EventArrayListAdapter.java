package com.my.dhreelife.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.model.Event;
import com.my.dhreelife.root.PendingEventDetailsActivity;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

/**
 * Created by Obana on 7/19/13.
 */
public class EventArrayListAdapter extends ArrayAdapter<Event> {
	private List <Event> eventList;
	private Context context;
	private boolean mEventPressed = false;
	private boolean mSwiping = false;
	private Activity activity;
	private RequestQueue requestQueue;
	public EventArrayListAdapter(Context context,Activity activity, List<Event> eventList) {
		super(context, R.layout.event_list_view, eventList);
		this.context = context;
		this.eventList = eventList;
		this.activity = activity;
	}
 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.event_list_view, parent, false);
		if(eventList!=null)
		{
			TextView eventTitle = (TextView) rowView.findViewById(R.id.event_title);

			TextView eventCreatorName = (TextView) rowView.findViewById(R.id.event_creator);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.event_creator_profile_picture);
			eventTitle.setText(eventList.get(position).getEventTitle());

			eventCreatorName.setText(eventList.get(position).getEventCreatorName());
			eventTitle.setTextSize(14.0f);
			eventCreatorName.setTextSize(14.0f);

			// Change icon based on status
			if(eventList.get(position).getEventStatus()==0)
				imageView.setImageResource(R.drawable.event_on_going);
			else if(eventList.get(position).getEventStatus()==1)
				imageView.setImageResource(R.drawable.event_finished);
		}
		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				Intent intent;
				intent  = new Intent(context, PendingEventDetailsActivity.class);
				intent.putExtra("eventId",eventList.get(position).getEventId());
				
				//check if alarm present
				if(eventList.get(position).getAlarmId()!=-1)
					intent.putExtra("alarmId",eventList.get(position).getAlarmId());
				activity.startActivityForResult(intent, GeneralManager.constantManager.VIEW_EVENT_ACTIVITY);
			}
		});

		rowView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.activity_remove_event);

				//layout
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				dialog.getWindow().setAttributes(lp);

				dialog.show();


				Button dialogButtonuYes = (Button) dialog.findViewById(R.id.btnRemoveEventYes);
				Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnRemoveEventNo);
				// if button is clicked, close the custom dialog
				dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ListView listView = (ListView)rowView.getParent();
						//remove from view
						ArrayAdapter<Event> adapter = (ArrayAdapter) listView.getAdapter();

						//initialize the request queue object
						requestQueue = new VolleyRequestQueue(context).getRequestQueueInstance();

						//create params
						Map<String, String> mParams = new HashMap<String,String>();
						mParams.put("id",GeneralManager.authManager.userId);
						mParams.put("eid",eventList.get(position).getEventId());

						//create error listener
						Response.ErrorListener errorListner = new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
							}
						};

						//craete the listener on call back
						StringVolleyResponseListener listener = createNewRequest(ConstantManager.REMOVE_EVENT);
						CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/remove",mParams,listener,errorListner);

						//set tag
						volleyRequest.setTag("Public");

						//add the request into request queue
						requestQueue.add(volleyRequest);


						//remove from database
						Event eventTemp = (Event) adapter.getItem(position);
						adapter.remove(eventTemp);
						adapter.notifyDataSetChanged();
						
						//remove reminder
						int alarmId = eventTemp.getAlarmId();
						try
						{
							DeleteCalendarEntry(alarmId);
						}
						catch (IllegalArgumentException e)
						{
							Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
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
				return false;
			}

		});
		return rowView;
	}


	public StringVolleyResponseListener createNewRequest(int taskId)
	{
		StringVolleyResponseListener stringVolleyResponseListener = new StringVolleyResponseListener()
		{
			@Override
			public void onStringResponse(String response)
			{
				if(context!=null)
				{
					int taskId = getTaskId();
					switch(taskId)
					{
					case ConstantManager.REMOVE_EVENT:

						try {
							JSONObject result = new JSONObject(response);
							if(result!=null)
							{
								if(result.has("error"))
								{
									Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		};
		stringVolleyResponseListener.setTaskId(taskId); 
		return stringVolleyResponseListener;
	}
	

	/*
	 * Delete an entry of calendar
	 */
	private int DeleteCalendarEntry(int entryID) throws IllegalArgumentException{
		int iNumRowsDeleted = 0;

		Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
		iNumRowsDeleted = activity.getContentResolver().delete(eventUri, null, null);


		return iNumRowsDeleted;
	}

	/*
	 * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
	 */
	private String getCalendarUriBase() {
		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor = null;
		try {
			managedCursor = activity.managedQuery(calendars, null, null, null, null);
		} catch (Exception e) {
			// eat
		}

		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor = activity.managedQuery(calendars, null, null, null, null);
			} catch (Exception e) {
				// eat
			}

			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}

		}

		return calendarUriBase;
	}

}

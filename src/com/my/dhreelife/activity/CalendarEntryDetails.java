package com.my.dhreelife.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.my.dhreelife.R;
import com.my.dhreelife.util.manager.GeneralManager;

public class CalendarEntryDetails extends Activity {

	private Button btnCalendarEntryBackButton;
	private Button btnCalendarEntryDeleteButton;

	private TextView txtCalendarEntryTitle;
	private TextView txtCalendarEntryTime;
	private TextView txtCalendarEntryLocation;
	private TextView txtCalendarEntryDescription;

	private int calendarEventId;
	private int eventId = -1;
	private Context context = this;
	private boolean refreshNeeded = false;
	private String position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_calendar_entry);



		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);

		btnCalendarEntryBackButton = (Button) findViewById(R.id.calendarEntryBackButton);
		btnCalendarEntryDeleteButton = (Button) findViewById(R.id.calendarEntryDeleteButton);

		txtCalendarEntryTitle = (TextView) findViewById(R.id.txtCalendarTitle);
		txtCalendarEntryTime = (TextView) findViewById(R.id.txtCalendarTime);
		txtCalendarEntryLocation = (TextView) findViewById(R.id.txtCalendarLocation);
		txtCalendarEntryDescription = (TextView) findViewById(R.id.txtCalendarDescription);

		try
		{
			eventId = Integer.parseInt(getIntent().getStringExtra("eventId"));
			String eventTitle = getIntent().getStringExtra("title");
			String eventTime = getIntent().getStringExtra("time");
			String eventDescription = getIntent().getStringExtra("description");
			String eventLocation = getIntent().getStringExtra("location");
			position= getIntent().getStringExtra("position");

			txtCalendarEntryTitle.setText(eventTitle);
			txtCalendarEntryTime.setText(eventTime);
			txtCalendarEntryTime.setTextColor(Color.GRAY);
			txtCalendarEntryLocation.setText(eventLocation);
			txtCalendarEntryLocation.setTextColor(Color.GRAY);
			txtCalendarEntryDescription.setText(eventDescription);
			txtCalendarEntryDescription.setTextColor(Color.GRAY);
		}
		catch(Exception e)
		{

		}

		btnCalendarEntryBackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		btnCalendarEntryDeleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final Dialog dialog = new Dialog(context);
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

						if(eventId!=-1)
						{
							int rowDeleted = DeleteCalendarEntry(eventId);
							if(rowDeleted>0)
							{
								refreshNeeded = true;
								Toast.makeText(context, "Calendar entry deleted.",Toast.LENGTH_LONG).show();
								dialog.dismiss();
								finish();
							}
						}
						else
						{
							Toast.makeText(context, "Can't delete calendar entry, ID missing.",Toast.LENGTH_LONG).show();
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
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}

	/*
	 * Delete an entry of calendar
	 */
	private int DeleteCalendarEntry(int entryID) {
		int iNumRowsDeleted = 0;

		Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
		iNumRowsDeleted = getContentResolver().delete(eventUri, null, null);


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
			managedCursor = managedQuery(calendars, null, null, null, null);
		} catch (Exception e) {
			// eat
		}

		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor = managedQuery(calendars, null, null, null, null);
			} catch (Exception e) {
				// eat
			}

			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}

		}

		return calendarUriBase;
	}


	@Override
	public void finish() {
		if(refreshNeeded)
		{
			// Prepare data intent 
			Intent data = new Intent();
			data.putExtra("REFRESH", "TRUE");
			data.putExtra("position", position);
			// Activity finished ok, return the data
			setResult(GeneralManager.constantManager.OK, data);
		}
		super.finish();
	}

}
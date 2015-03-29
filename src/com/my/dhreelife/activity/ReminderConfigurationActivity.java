package com.my.dhreelife.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.my.dhreelife.R;
import com.my.dhreelife.util.manager.ConstantManager;



public class ReminderConfigurationActivity extends Activity{

	private Spinner reminderDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reminder_configuration); 

		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);
		
		reminderDuration = (Spinner) findViewById(R.id.eventReminderDurationSpnr);
		reminderDuration.setBackgroundResource(R.drawable.spinner_background);
		reminderDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View currentView,
					int position, long id) {
				SharedPreferences pref = getSharedPreferences(ConstantManager.EVENT_REMINDER, ConstantManager.PRIVATE_MODE);
				Editor editor = pref.edit();
				switch(position)
				{
				case 0:
					//15 mins
					editor.putLong("DURATION", 900000);
					break;
				case 1:
					//30 mins
					editor.putLong("DURATION", 1800000);
					break;
				case 2:
					//45 mins
					editor.putLong("DURATION", 2700000);
					break;
				case 3:
					//1 hour
					editor.putLong("DURATION", 3600000);
					break;
				case 4:
					//2 hours
					editor.putLong("DURATION", 7200000);
					break;
				}
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});
		
		//set the value of reminder spinner
		SharedPreferences pref = getSharedPreferences(ConstantManager.EVENT_REMINDER, ConstantManager.PRIVATE_MODE);
		long duration = pref.getLong("DURATION", 900000);
		if(duration ==900000)
		{
			reminderDuration.setSelection(0);
		}
		else if(duration == 1800000)
		{
			reminderDuration.setSelection(1);
		}
		else if(duration == 2700000)
		{
			reminderDuration.setSelection(2);
		}
		else if(duration == 3600000)
		{
			reminderDuration.setSelection(3);
		}
		else if(duration == 7200000)
		{
			reminderDuration.setSelection(4);
		}
	}
}

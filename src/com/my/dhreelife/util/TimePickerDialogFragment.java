package com.my.dhreelife.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;


@SuppressLint("ValidFragment")
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	private String timeSet = "";
	private Spinner time ;
	
	public TimePickerDialogFragment(Spinner time)
	{
		this.time = time;
	}
	 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); 
			Date tmpDate = new Date();
			tmpDate.setHours(hourOfDay);
			tmpDate.setMinutes(minute);
			tmpDate.setSeconds(0);
			String temp = dateFormat.format(tmpDate.getTime());
			List<String> list = new ArrayList<String>();
			list.add(temp);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
					android.R.layout.simple_spinner_item, list);
			time.setAdapter(dataAdapter);
		}
		catch(Exception e)
		{}
	}
}
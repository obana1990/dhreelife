package com.my.dhreelife.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

@SuppressLint("ValidFragment")
public class DatePickerDialogFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

	private String dateSet = "";
	private Spinner date;
	
	public DatePickerDialogFragment(Spinner date)
	{
		this.date = date;
	}
	 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		try
		{
			//dateSet = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DATE, day);
			Date tmpDate = cal.getTime();

			String temp = dateFormat.format(tmpDate);
			List<String> list = new ArrayList<String>();
			list.add(temp);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
					android.R.layout.simple_spinner_item, list);
			date.setAdapter(dataAdapter);
		}
		catch(Exception e)
		{
			
		}
	}
}
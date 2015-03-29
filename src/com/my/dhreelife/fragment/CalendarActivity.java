package com.my.dhreelife.fragment;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.my.dhreelife.R;
import com.my.dhreelife.activity.CalendarEntryDetails;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class CalendarActivity  extends Fragment implements ActionBar.TabListener {

	private Fragment mFragment;
	private static final String tag = "MyCalendarActivity";

	private TextView currentMonth;
	private ListView calendarEntryListView;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	@SuppressLint("NewApi")
	private int month, year;
	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
	private Map<String, ArrayList<String[]>> eventList = new HashMap<String,ArrayList<String[]>>();
	private View currentClickedView;
	private ArrayAdapter<String> listViewAdapter;
	private List<String> eventEntry;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.CALENDAR_ACTIVITY_ID;

		View v = inflater.inflate(R.layout.activity_calendar, container, false);

		if(v!=null)
		{
			_calendar = Calendar.getInstance(Locale.getDefault());
			month = _calendar.get(Calendar.MONTH) + 1;
			year = _calendar.get(Calendar.YEAR);
			Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
					+ year);

			currentClickedView = new View(getActivity().getBaseContext());

			prevMonth = (ImageView) v.findViewById(R.id.prevMonth);
			calendarEntryListView = (ListView) v.findViewById(R.id.lstViewCalendarEntry);

			if(prevMonth!=null)
				prevMonth.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
						if (view == prevMonth) {
							if (month <= 1) {
								month = 12;
								year--;
							} else {
								month--;
							}
							Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
									+ month + " Year: " + year);
							setGridCellAdapterToDate(month, year);
						}
						if (view == nextMonth) {
							if (month > 11) {
								month = 1;
								year++;
							} else {
								month++;
							}
							Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
									+ month + " Year: " + year);
							setGridCellAdapterToDate(month, year);
						}
					}
				});

			currentMonth = (TextView) v.findViewById(R.id.currentMonth);
			currentMonth.setText(DateFormat.format(dateTemplate,
					_calendar.getTime()));

			nextMonth = (ImageView) v.findViewById(R.id.nextMonth);
			nextMonth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
					if (view == prevMonth) {
						if (month <= 1) {
							month = 12;
							year--;
						} else {
							month--;
						}
						Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
								+ month + " Year: " + year);
						setGridCellAdapterToDate(month, year);
					}
					if (view == nextMonth) {
						if (month > 11) {
							month = 1;
							year++;
						} else {
							month++;
						}
						Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
								+ month + " Year: " + year);
						setGridCellAdapterToDate(month, year);
					}
				}
			});

			calendarView = (GridView) v.findViewById(R.id.calendar);

			// Initialised
			adapter = new GridCellAdapter(getActivity().getApplicationContext(),
					R.id.calendar_day_gridcell, month, year);
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
		}

		return v;
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new CalendarActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}


	/**
	 *
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getActivity().getApplicationContext(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View ...");
		super.onDestroy();
	}

	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap<String, Integer> eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-MMM-yyyy");
		private final SimpleDateFormat dateFormatter2 = new SimpleDateFormat(
				"HH:mm");

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 *
		 * @param mm
		 * @param yy
		 */
		 private void printMonth(int mm, int yy) {
			 Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			 int trailingSpaces = 0;
			 int daysInPrevMonth = 0;
			 int prevMonth = 0;
			 int prevYear = 0;
			 int nextMonth = 0;
			 int nextYear = 0;

			 int currentMonth = mm - 1;
			 String currentMonthName = getMonthAsString(currentMonth);
			 daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			 Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
					 + daysInMonth + " days.");

			 GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			 Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			 if (currentMonth == 11) {
				 prevMonth = currentMonth - 1;
				 daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				 nextMonth = 0;
				 prevYear = yy;
				 nextYear = yy + 1;
				 Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
						 + prevMonth + " NextMonth: " + nextMonth
						 + " NextYear: " + nextYear);
			 } else if (currentMonth == 0) {
				 prevMonth = 11;
				 prevYear = yy - 1;
				 nextYear = yy;
				 daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				 nextMonth = 1;
				 Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
						 + prevMonth + " NextMonth: " + nextMonth
						 + " NextYear: " + nextYear);
			 } else {
				 prevMonth = currentMonth - 1;
				 nextMonth = currentMonth + 1;
				 nextYear = yy;
				 prevYear = yy;
				 daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				 Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
						 + prevMonth + " NextMonth: " + nextMonth
						 + " NextYear: " + nextYear);
			 }

			 int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			 trailingSpaces = currentWeekDay;

			 Log.d(tag, "Week Day:" + currentWeekDay + " is "
					 + getWeekDayAsString(currentWeekDay));
			 Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			 Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			 if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				 if (mm == 2)
					 ++daysInMonth;
				 else if (mm == 3)
					 ++daysInPrevMonth;

			 // Trailing Month days
			 for (int i = 0; i < trailingSpaces; i++) {
				 Log.d(tag,
						 "PREV MONTH:= "
								 + prevMonth
								 + " => "
								 + getMonthAsString(prevMonth)
								 + " "
								 + String.valueOf((daysInPrevMonth
										 - trailingSpaces + DAY_OFFSET)
										 + i));
				 list.add(String
						 .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								 + i)
								 + "-GREY"
								 + "-"
								 + getMonthAsString(prevMonth)
								 + "-"
								 + prevYear);
			 }

			 // Current Month Days
			 for (int i = 1; i <= daysInMonth; i++) {
				 Log.d(currentMonthName, String.valueOf(i) + " "
						 + getMonthAsString(currentMonth) + " " + yy);
				 if (i == getCurrentDayOfMonth()) {
					 list.add(String.valueOf(i) + "-BLUE" + "-"
							 + getMonthAsString(currentMonth) + "-" + yy);
				 } else {
					 list.add(String.valueOf(i) + "-WHITE" + "-"
							 + getMonthAsString(currentMonth) + "-" + yy);
				 }
			 }

			 // Leading Month days
			 for (int i = 0; i < list.size() % 7; i++) {
				 Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				 list.add(String.valueOf(i + 1) + "-GREY" + "-"
						 + getMonthAsString(nextMonth) + "-" + nextYear);
			 }
		 }

		 /**
		  * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		  * ALL entries from a SQLite database for that month. Iterate over the
		  * List of All entries, and get the dateCreated, which is converted into
		  * day.
		  *
		  * @param year
		  * @param month
		  * @return
		  */
		 private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,int month) {
			 ContentResolver contentResolver = getActivity().getBaseContext().getContentResolver();
			 String selection = "deleted!=1";
			 Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"), new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" ,"_id"}, selection, null, null);
			 HashMap<String, Integer> map = new HashMap<String, Integer>();
			 eventList = new HashMap<String,ArrayList<String[]>>();
			 if(cursor!=null)
			 {
				 while(cursor.moveToNext())
				 {
					 String[] CalNames = new String[cursor.getCount()];
					 for (int i = 0; i < CalNames.length; i++) {
						 Calendar cal = Calendar.getInstance();
						 Date  eventStartDate = new Date(cursor.getLong(3));
						 String[] eventInfo = new String[5];
						 String title = cursor.getString(1);
						 String location = cursor.getString(5);
						 String id = cursor.getString(6);
						 String description = cursor.getString(2);
						 eventInfo[0] = title;
						 eventInfo[1] = dateFormatter2.format(eventStartDate);
						 eventInfo[2] = id;
						 eventInfo[3] = location;
						 eventInfo[4] = description;
						 map.put(String.valueOf(cal.get(Calendar.YEAR)), cal.get(Calendar.MONTH + 1));
						 addValue(eventList,dateFormatter.format(eventStartDate),eventInfo);
						 cursor.moveToNext();
					 }
				 }

				 //Toast.makeText(getActivity().getBaseContext(),add,Toast.LENGTH_SHORT).show();
			 }



			 return map;
		 }

		 @Override
		 public long getItemId(int position) {
			 return position;
		 }

		 @SuppressLint("SimpleDateFormat")
		 @SuppressWarnings("deprecation")
		 @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			 View row = convertView;
			 if (row == null) {
				 LayoutInflater inflater = (LayoutInflater) _context
						 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			 }

			 // Get a reference to the Day gridcell
			 gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			 gridcell.setOnClickListener(this);

			 // ACCOUNT FOR SPACING

			 Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			 String[] day_color = list.get(position).split("-");
			 String theday = day_color[0];
			 String themonth = day_color[2];
			 String theyear = day_color[3];



			 if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				 if (eventsPerMonthMap.containsKey(theday)) {
					 num_events_per_day = (TextView) row
							 .findViewById(R.id.num_events_per_day);
					 Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					 num_events_per_day.setText(numEvents.toString());
				 }
			 }



			 // Set the Day GridCell
			 gridcell.setText(theday);
			 gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			 Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
					 + theyear);

//			 if (day_color[1].equals("GREY")) {
//				 gridcell.setTextColor(getResources()
//						 .getColor(R.color.lightgray02));
//			 }
//			 if (day_color[1].equals("WHITE")) {
//				 gridcell.setTextColor(getResources().getColor(
//						 R.color.lightgray02));
//			 }
			 gridcell.setTextColor(getResources().getColor(
					 R.color.lightgray02));
			 
			 String date = theday+"-"+themonth+"-"+theyear;
			 Date temp = new Date(date);
			 SimpleDateFormat df = new SimpleDateFormat("d-MMMM-yyyy");
			 SimpleDateFormat df2 = new SimpleDateFormat("d-MMM-yyyy");
			 date = df2.format(temp);
			 
			 
			 Calendar cal = Calendar.getInstance();
			 Date tempDate =cal.getTime();
			 String tempDateInString = df.format(tempDate);

			 if (day_color[1].equals("BLUE")&&day_color[2].equals(tempDateInString.split("-")[1])&&day_color[3].equals(tempDateInString.split("-")[2])) {
				 //gridcell.setBackgroundColor(getResources().getColor(R.color.gray));
			 }

			 
			 if(eventList.get(date)!=null)
			 {
				 gridcell.setTextColor(getResources().getColor(R.color.black));
			 }
			 return row;
		 }

		 @SuppressWarnings("deprecation")
		 @Override
		 public void onClick(View v) {
			 GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
			 String tag = (String) v.getTag();
			 Date temp = new Date(tag);
			 final String date_month_year = dateFormatter.format(temp);

			 
			 //set the background as user click it
			 currentClickedView.setBackgroundResource(0);
			 currentClickedView = v;
			 v.setBackgroundResource(R.drawable.calendar_bg_orange);
			 
			 
			 if(calendarEntryListView!=null&&getActivity()!=null)
			 {
				 if(eventList.get(date_month_year)!=null)
				 {
					 //calendarEventTitle.setText("Event : "+eventList.get(date_month_year)[0]+" at "+eventList.get(date_month_year)[1]);
					 String entry = "";
					 eventEntry = new ArrayList<String>();
					 for(int i=0;i<eventList.get(date_month_year).size();i++)
					 {
						 entry = entry+ eventList.get(date_month_year).get(i)[0]+" at "+eventList.get(date_month_year).get(i)[1]+" "+eventList.get(date_month_year).get(i)[2]+" \n";
						 eventEntry.add(eventList.get(date_month_year).get(i)[0]+" at "+eventList.get(date_month_year).get(i)[1]);
					 }
					 listViewAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, eventEntry);
					 calendarEntryListView.setAdapter(listViewAdapter);
					 calendarEntryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							
							GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
							Intent myIntent = new Intent(getActivity(), CalendarEntryDetails.class);
							Bundle bundle = new Bundle();
							bundle.putString("position",String.valueOf(position));

							
							myIntent.putExtra("position",String.valueOf(position));
							myIntent.putExtra("eventId",eventList.get(date_month_year).get(position)[2]);
							myIntent.putExtra("title",eventList.get(date_month_year).get(position)[0]);
							myIntent.putExtra("time",eventList.get(date_month_year).get(position)[1]);
							myIntent.putExtra("description",eventList.get(date_month_year).get(position)[4]);
							myIntent.putExtra("location",eventList.get(date_month_year).get(position)[3]);
							startActivityForResult(myIntent,  GeneralManager.constantManager.CALENDAR_ENTRY_DETAILS_ACTIVITY);
						}
					});
					 
				 }
				 
				 
				 
			 }
			 Log.e("Selected date", date_month_year);
			 try {
				 Date parsedDate = dateFormatter.parse(date_month_year);
				 Log.d(tag, "Parsed Date: " + parsedDate.toString());

			 } catch (ParseException e) {
				 e.printStackTrace();
			 }
		 }

		 public int getCurrentDayOfMonth() {
			 return currentDayOfMonth;
		 }

		 private void setCurrentDayOfMonth(int currentDayOfMonth) {
			 this.currentDayOfMonth = currentDayOfMonth;
		 }

		 public void setCurrentWeekDay(int currentWeekDay) {
			 this.currentWeekDay = currentWeekDay;
		 }

		 public int getCurrentWeekDay() {
			 return currentWeekDay;
		 }
	}

	private void addValue(Map map, Object key, Object value) {
	    Object obj = map.get(key);
	    List list;
	    if (obj == null) {  
	        list = new ArrayList<Object>();  
	    } else {
	        list = ((ArrayList) obj);
	    }
	    list.add(value);
	    map.put(key, list);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == ConstantManager.OK && requestCode ==  GeneralManager.constantManager.CALENDAR_ENTRY_DETAILS_ACTIVITY) {
			if (data.hasExtra("REFRESH")) {
				int position = -1;
				try
				{
					position = Integer.parseInt(data.getStringExtra("position"));
					eventEntry.remove(position);
					listViewAdapter.notifyDataSetChanged();
					//refresh the data
					adapter = new GridCellAdapter(getActivity().getApplicationContext(),
							R.id.calendar_day_gridcell, month, year);
					adapter.notifyDataSetChanged();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}
	}

}

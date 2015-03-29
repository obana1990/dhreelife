package com.my.dhreelife.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.my.dhreelife.R;
import com.my.dhreelife.activity.AddNewEventActivity;
import com.my.dhreelife.model.Event;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.EventArrayListAdapter;
import com.my.dhreelife.util.IncomingCall;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressLint("NewApi")
public class EventActivity extends Fragment implements ActionBar.TabListener,SearchView.OnQueryTextListener,IncomingCall{

	private Fragment mFragment;
	private ImageButton addEvent;
	private boolean search = false;
	private ListView eventList;
	private EditText searchContactList;
	private List<Event> events ;
	private List<Event> backUpEvent;
	private ProgressBar loadingPb;
	private ProgressBar loadingMoreEventPb;
	private TextView loadingTxt;
	private int offset = 0;
	private boolean onLoading = false;
	private boolean reloadEventList = false;
	private RequestQueue requestQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.EVENT_ACTIVITY_ID;
		GeneralManager.constantManager.CURRENT_FRAGMENT = EventActivity.this;
		GeneralManager.constantManager.CURRENT_FRAGMENT_PARENT = EventActivity.this;

		//initialize the request queue object
		if(getActivity()!=null)
			requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();

		offset = 0;
		View v = inflater.inflate(R.layout.activity_event, container, false);
		events = new ArrayList<Event>();
		refreshEventList();


		if(v!=null)
		{
			loadingPb = (ProgressBar) v.findViewById(R.id.eventLoadingPb);
			loadingMoreEventPb = (ProgressBar) v.findViewById(R.id.eventWindowPb);
			loadingTxt = (TextView) v.findViewById(R.id.eventLoadingTxt); 
			eventList = (ListView) v.findViewById(R.id.event_list);
			addEvent = (ImageButton) v.findViewById(R.id.add_event);
			searchContactList = (EditText) v.findViewById(R.id.search_event_list);


			eventList.setOnScrollListener(new AbsListView.OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {


				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					//already scroll to the bottom
					final int lastItem = firstVisibleItem + visibleItemCount;
					if(lastItem==totalItemCount&&!onLoading&&totalItemCount!=0)
					{
						loadingMoreEventPb.setVisibility(View.VISIBLE);
						offset+=ConstantManager.NUMBER_OF_NEW_LOAD_EVENT;
						refreshEventList();
					}
				}
			});


			if(searchContactList!=null)
			{
				searchContactList.setHint("Search");
				searchContactList.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

					}

					@Override
					public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
						int textLength = searchContactList.getText().length();
						if(textLength==0)
						{
							searchContactList.setHint("Search");
							search = false;
							events = backUpEvent;
						}
						else
						{
							searchContactList.setHint("");
							search = true;
						}
						populateEventList(0,0);
					}

					@Override
					public void afterTextChanged(Editable editable) {

					}
				});
			}

			if(addEvent!=null)
				addEvent.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
						Intent myIntent = new Intent(getActivity(), AddNewEventActivity.class);
						startActivityForResult(myIntent,  GeneralManager.constantManager.CREATE_EVENT_ACTIVITY);
					}
				});

		}

		return v;
	}


	public void populateEventList(int lastViewedPosition,int topOffset)
	{
		if(events!=null)
		{
			if(search)
			{
				events = backUpEvent;
				List<Event> searchResult = new ArrayList<Event>();
				if(searchContactList!=null)
				{
					for(int i=0;i<events.size();i++)
					{
						String searchKeyword = searchContactList.getText().toString();
						String eventTitle = events.get(i).getEventTitle();
						if(eventTitle.contains(searchKeyword))
						{
							searchResult.add(events.get(i));
						}
					}
					events = new ArrayList<Event>();
					for(int i=0;i<searchResult.size();i++)
					{
						events.add(searchResult.get(i));
					}

				}
			}
			if(offset>events.size()&&!onLoading)
			{
				//disable load more event as no more event could be loaded
				onLoading = true;
				Toast.makeText(getActivity().getApplicationContext(), "No more event.", Toast.LENGTH_LONG).show();
			}
			if(eventList!=null&&getActivity()!=null)
			{
				EventArrayListAdapter myAdapter = new EventArrayListAdapter(getActivity().getBaseContext(),getActivity(),events);
				eventList.setAdapter(myAdapter);
				eventList.setSelectionFromTop(lastViewedPosition, topOffset);
			}
			else
			{
				if(getActivity()!=null)
					Toast.makeText(getActivity().getBaseContext(), "Contact list was empty.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new EventActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);
	}

	@SuppressLint("NewApi")
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);
		//if request queue is not null then cancel all
		if(requestQueue!=null)
			requestQueue.cancelAll("Event");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}


	@Override
	public boolean onQueryTextSubmit(String s) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String s) {
		return false;
	}




	public void refreshEventList()
	{
		if(requestQueue!=null)
		{
			onLoading = true;


			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",GeneralManager.authManager.userId);
			mParams.put("offset",String.valueOf(offset));

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_ALL_EVENT);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/getAllEvent",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Event");

			//add the request into request queue
			requestQueue.add(volleyRequest);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == ConstantManager.OK && (requestCode ==  GeneralManager.constantManager.CREATE_EVENT_ACTIVITY||requestCode ==  GeneralManager.constantManager.VIEW_EVENT_ACTIVITY)) {
			if (data.hasExtra("REFRESH")) {
				reloadEventList = true;
				refreshEventList();
			}
		}
	}

	public void showProgress(boolean show)
	{
		if(show)
		{
			loadingPb.setVisibility(View.VISIBLE);
			loadingTxt.setVisibility(View.VISIBLE);
		}
		else
		{
			loadingPb.setVisibility(View.GONE);
			loadingTxt.setVisibility(View.GONE);
		}
	}

	public StringVolleyResponseListener createNewRequest(int taskId)
	{
		StringVolleyResponseListener stringVolleyResponseListener = new StringVolleyResponseListener()
		{
			@Override
			public void onStringResponse(String response)
			{
				if(getActivity()!=null)
				{
					int taskId = getTaskId();
					showProgress(false);
					switch(taskId)
					{
					case ConstantManager.GET_ALL_EVENT:

						try {
							JSONArray result = new JSONArray(response);
							if (result != null) {
								//set view to visible
								eventList.setVisibility(View.VISIBLE);
								int lastViewedPosition = eventList.getFirstVisiblePosition();

								//get offset of first visible view
								View v = eventList.getChildAt(0);
								int topOffset = (v == null) ? 0 : v.getTop();
								if(reloadEventList)
								{
									events = new ArrayList<Event>();
									reloadEventList = false;
								}
								for(int i=0;i<result.length();i++)
								{
									try {
										final JSONObject jsonObject = (JSONObject) result.get(i);
										Event temp = new Event();
										temp.setEventId(new JSONObject(jsonObject.getString("Eventuser")).getString("event_id"));
										temp.setEventTitle(new JSONObject(jsonObject.getString("Eventuser")).getString("event_name"));
										temp.setEventStatus(Integer.parseInt(new JSONObject(jsonObject.getString("Eventuser")).getString("joined_status")));
										String alarmId = new JSONObject(jsonObject.getString("Eventuser")).getString("alarm_id");
										if(!alarmId.equals(""))
											temp.setAlarmId(Integer.parseInt(alarmId));
										events.add(temp);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									catch (Exception e) {
										e.printStackTrace();
									}
								}
								backUpEvent = events;
								populateEventList(lastViewedPosition,topOffset);
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


	@Override
	public void onCall() {
		//refresh event
		events = new ArrayList<Event>();
		offset = 0;
		refreshEventList();
	}
}

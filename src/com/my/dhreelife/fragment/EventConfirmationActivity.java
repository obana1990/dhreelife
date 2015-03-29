package com.my.dhreelife.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.my.dhreelife.R;
import com.my.dhreelife.activity.AttendeesInfo;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.ChatArrayListAdapter;
import com.my.dhreelife.util.IncomingCall;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class EventConfirmationActivity extends Fragment implements ActionBar.TabListener,IncomingCall{
	private Fragment mFragment;
	private TextView eventTitle;
	private Button chatSendButton;
	//private Button attendeesInfoButton;
	private EditText chatContentToSend;
	private Button goingToEvent;
	private Button attendeeInfo;
	private Button notGoingToEvent;

	private ProgressBar loadingPb;
	private String eventId;
	private List<String[]> comments;
	private ListView chatList ;
	public static int commentsCount = ConstantManager.NUMBER_OF_NEW_LOAD_MESSAGE;
	private boolean loadMoreMessageRequest = false;
	private boolean onLoading = false;
	private RequestQueue requestQueue;
	private int goingIntention = 0;
	private int previousGoingIntention = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.EVENT_CONFIRMATION_ACTIVITY_ID;
		ConstantManager.CURRENT_FRAGMENT = EventConfirmationActivity.this;
		View v = inflater.inflate(R.layout.activity_event_confirmation, container, false);
		commentsCount = ConstantManager.NUMBER_OF_NEW_LOAD_MESSAGE;
		eventId = getActivity().getIntent().getStringExtra("eventId");
		try
		{
			ConstantManager.CURRENT_CHAT_WINDOW_EVENT_ID = Integer.parseInt(eventId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//initialize the request queue object
		if(getActivity()!=null)
		{
			requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",GeneralManager.authManager.userId);
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
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_ATTENDEES_STATUS);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/getAttendStatus",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("EventConfirmation");

			//add the request into request queue
			requestQueue.add(volleyRequest);

		}


		//set the event id user current at to Notification manager for checking of incoming notification.
		//if the user receive notification of currently view event chat, then don't generate notification but update instead
		//		if(eventId!=null)
		//			GeneralManager.notificationManager.currentChatWindowEventId = Integer.parseInt(eventId);

		loadingPb = (ProgressBar) v.findViewById(R.id.chatWindowPb);
		eventTitle = (TextView) v.findViewById(R.id.event_confirmation_title);
		chatSendButton = (Button) v.findViewById(R.id.chat_send);
		//disable it until event loaded
		chatSendButton.setEnabled(false);
		goingToEvent = (Button) v.findViewById(R.id.going_to_event);
		notGoingToEvent = (Button) v.findViewById(R.id.not_going_to_event);	
		attendeeInfo = (Button)v.findViewById(R.id.btnAttendeeInfo);


		attendeeInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),AttendeesInfo.class);
				intent.putExtra("eventId", eventId);
				getActivity().startActivity(intent);

			}
		});


		goingToEvent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(requestQueue!=null)
				{

					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("id", GeneralManager.authManager.userId);
					mParams.put("eid", eventId);

					if(goingIntention==1)
					{
						goingIntention = 0;
						mParams.put("going_intention", "0");
					}
					else
					{
						goingIntention = 1;
						mParams.put("going_intention", "1");
					}
					setupEventConfirmationWindow(goingIntention);
					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							if(getActivity()!=null)
								Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_ATTEND_STATUS);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/updateAttendStatus",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("EventConfirmation");

					//add the request into request queue
					requestQueue.add(volleyRequest);
				}
			}
		});


		notGoingToEvent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(requestQueue!=null)
				{

					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("id", GeneralManager.authManager.userId);
					mParams.put("eid", eventId);

					if(goingIntention==3)
					{
						goingIntention = 0;
						mParams.put("going_intention", "0");
					}
					else
					{
						goingIntention = 3;
						mParams.put("going_intention", "3");
					}
					setupEventConfirmationWindow(goingIntention);

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							if(getActivity()!=null)
								Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_ATTEND_STATUS);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventusers/updateAttendStatus",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("EventConfirmation");

					//add the request into request queue
					requestQueue.add(volleyRequest);

				}
			}
		});

		chatContentToSend = (EditText) v.findViewById(R.id.chat_content_to_send);


		//disable the button before server send back respond
		goingToEvent.setVisibility(View.INVISIBLE);
		attendeeInfo.setVisibility(View.INVISIBLE);
		notGoingToEvent.setVisibility(View.INVISIBLE);

		goingToEvent.setEnabled(false);
		attendeeInfo.setEnabled(false);
		notGoingToEvent.setEnabled(false);



		if (eventTitle != null) {
			//eventTitle.setText(eventTitleInString);
		}


		chatList = (ListView) v.findViewById(R.id.chat_list_view);
		if (chatList != null) {
			refreshChatWindow();
		}
		chatList.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {


			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//already scroll to the top
				if(firstVisibleItem==0&&!onLoading)
				{
					commentsCount +=ConstantManager.NUMBER_OF_NEW_LOAD_MESSAGE;
					refreshChatWindow();
					loadMoreMessageRequest = true;
					loadingPb.setVisibility(View.VISIBLE);
				}
			}
		});

		//send new message when the "SEND" button was clicked
		if (chatSendButton != null) {
			chatSendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
						if(chatContentToSend.getText().toString().length()>0&&requestQueue!=null)
						{
							String temp = chatContentToSend.getText().toString();
							chatContentToSend.setText("");
							//get the number of conservation
							ChatArrayListAdapter currentAdapter = (ChatArrayListAdapter) chatList.getAdapter();
							if(currentAdapter==null)
							{
								//no comments, adapter = null initialize a new one
								String temp2[] = new String[4];
								temp2[0] = "Me";
								temp2[1] = temp;
								temp2[2] = GeneralManager.authManager.userId;
								temp2[3] = "";
								List<String[]> newCommentList = new ArrayList<String[]>();
								newCommentList.add(temp2);
								currentAdapter = new ChatArrayListAdapter(getActivity().getBaseContext(), newCommentList,GeneralManager.authManager.userId);
								chatList.setAdapter(currentAdapter);
							}
							else
							{
								//add the new message that user just sent
								String temp2[] = new String[4];
								temp2[0] = "Me";
								temp2[1] = temp;
								temp2[2] = GeneralManager.authManager.userId;
								temp2[3] = "";
								ChatArrayListAdapter adapter = (ChatArrayListAdapter)chatList.getAdapter();
								adapter.add(temp2);
								adapter.notifyDataSetChanged();
								chatList.setSelection(chatList.getCount() - 1);
							}

							//set HTTP params
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
							nameValuePairs.add(new BasicNameValuePair("id", GeneralManager.authManager.userId));
							nameValuePairs.add(new BasicNameValuePair("eid", eventId));
							nameValuePairs.add(new BasicNameValuePair("message", temp));  

							//create params
							Map<String, String> mParams = new HashMap<String,String>();
							mParams.put("id",GeneralManager.authManager.userId);
							mParams.put("eid",eventId);
							mParams.put("message",temp);

							//create error listener
							Response.ErrorListener errorListner = new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									if(getActivity()!=null)
										Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
								}
							};

							//craete the listener on call back
							StringVolleyResponseListener listener = createNewRequest(ConstantManager.POST_COMMENT);
							CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventcomments/postComment",mParams,listener,errorListner);

							//set tag
							volleyRequest.setTag("EventConfirmation");

							//add the request into request queue
							requestQueue.add(volleyRequest);


						}

					} catch (Exception e) {

					}
				}
			});
		}


		return v;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new EventConfirmationActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);
		//if request queue is not null then cancel all
		if(requestQueue!=null)
			requestQueue.cancelAll("EventConfirmation");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	public void populateChatWindow()
	{
		if(getActivity()!=null)
		{
			//create new adapter
			ChatArrayListAdapter newChatContent = new ChatArrayListAdapter(getActivity().getBaseContext(), comments,GeneralManager.authManager.userId);

			chatList.setAdapter(newChatContent);
			if(loadMoreMessageRequest&&chatList.getCount()>=ConstantManager.NUMBER_OF_NEW_LOAD_MESSAGE)
				chatList.setSelection(ConstantManager.NUMBER_OF_NEW_LOAD_MESSAGE-1);
			else
				chatList.setSelection(chatList.getCount() - 1);

			//if all messages was loaded, disable the button
			if(chatList.getCount()<commentsCount)
			{
				//disable load more message
				onLoading = true;
			}	
		}
	}

	public void refreshChatWindow()
	{
		if(requestQueue!=null)
		{
			onLoading = true;

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",eventId);
			mParams.put("count",String.valueOf(commentsCount));

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_COMMENT);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"eventcomments/getComments",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("EventConfirmation");

			//add the request into request queue
			requestQueue.add(volleyRequest);
		}
	}

	public void setupEventConfirmationWindow(int goingIntention)
	{
		goingToEvent.setVisibility(View.VISIBLE);
		attendeeInfo.setVisibility(View.VISIBLE);
		notGoingToEvent.setVisibility(View.VISIBLE);
		goingToEvent.setEnabled(true);
		notGoingToEvent.setEnabled(true);
		switch (goingIntention)
		{
		case 0:
			goingToEvent.setBackgroundResource(R.drawable.going_part2);
			notGoingToEvent.setBackgroundResource(R.drawable.not_going_part2);
			break;

			//going
		case 1:
			goingToEvent.setBackgroundResource(R.drawable.going_part22);
			notGoingToEvent.setBackgroundResource(R.drawable.not_going_part2);
			break;
			//not going
		case 3:
			notGoingToEvent.setBackgroundResource(R.drawable.not_going_part22);
			goingToEvent.setBackgroundResource(R.drawable.going_part2);

			break;
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

					switch(taskId)
					{
					case ConstantManager.POST_COMMENT:

						try {
							JSONObject result = new JSONObject(response);
							if (result != null) {
								if(!result.has("error"))
								{
									refreshChatWindow();
								}
								else
								{
									try {
										if(getActivity()!=null)
											Toast.makeText(getActivity().getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}

						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;

					case ConstantManager.GET_COMMENT:

						try {
							onLoading = false;
							loadingPb.setVisibility(View.INVISIBLE);
							JSONArray result = new JSONArray(response);
							if (result != null) {
								comments = new ArrayList<String[]>();
								for(int i=0;i<result.length();i++)
								{
									try {
										final JSONObject jsonObject = (JSONObject) result.get(i);
										String temp[] = new String[4];
										temp[0] = new JSONObject(jsonObject.getString("Eventcomment")).getString("name");
										temp[1] = new JSONObject(jsonObject.getString("Eventcomment")).getString("message");
										temp[2] = new JSONObject(jsonObject.getString("Eventcomment")).getString("user_id");
										temp[3] = new JSONObject(jsonObject.getString("Eventcomment")).getString("created");
										comments.add(temp);

									} catch (JSONException e) { 
										e.printStackTrace();
									}
								}
								populateChatWindow();

							}

						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;

					case ConstantManager.UPDATE_ATTEND_STATUS:

						try {
							JSONObject result = new JSONObject(response);
							if (result != null) {
								if(result.has("error"))
								{
									//if update attending status fail, restore to previous status
									goingIntention = previousGoingIntention;
									setupEventConfirmationWindow(goingIntention);
									try {
										if(getActivity()!=null)
											Toast.makeText(getActivity().getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								else
								{
									try
									{
										previousGoingIntention = result.getInt("updated_going_intention");
										goingIntention = result.getInt("updated_going_intention");
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}

									GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
								}
							}

						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;

					case ConstantManager.GET_ATTENDEES_STATUS:

						try {
							attendeeInfo.setEnabled(true);
							JSONObject result = new JSONObject(response);
							if (result != null) {
								if(!result.has("error"))
								{
									try {
										//event loaded, enable the button
										previousGoingIntention = result.getInt("going_intention");
										goingIntention = result.getInt("going_intention");
										chatSendButton.setEnabled(true);
										attendeeInfo.setVisibility(View.VISIBLE);
										setupEventConfirmationWindow(Integer.parseInt(result.getString("going_intention")));

									} catch (NumberFormatException e) {
										e.printStackTrace();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								else
								{
									try {
										if(getActivity()!=null)
										{
											Toast.makeText(getActivity().getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
											getActivity().finish();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
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

	@Override
	public void onCall() {
		//call from broadcast, refresh chat window
		refreshChatWindow();

	}
}

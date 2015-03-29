package com.my.dhreelife.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.my.dhreelife.root.PendingEventDetailsActivity;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

/**
 * Created by Obana on 7/19/13.
 */
public class NotificationArrayListAdapter extends ArrayAdapter<String[]> {
	List<String[]> notification;
	private Context context;

	private final int NOTIFICATION_MESSAGE = 0;
	private final int TYPE = 1;
	private final int STATE = 3;
	private final String VIEWED = "1";
	private final String FRIEND_REQUEST_TYPE = "2";
	private final String EVENT_LAUNCH_NOTIFY_TYPE = "3";
	private final String EVENT_TYPE = "1";
	private boolean mNotificationPressed = false;
	private boolean mSwiping = false;
	private List<String> notificationIds = new ArrayList<String>();
	private RequestQueue requestQueue;
 
	public NotificationArrayListAdapter(Context context, List<String[]> notification) {
		super(context.getApplicationContext(), R.layout.notification_list_view, notification);
		this.context = context;
		this.notification = notification;
		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(context).getRequestQueueInstance();
		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(context, R.raw.click, 1); 
	}
	public List<String> getRemovedNotificationId()
	{
		return notificationIds;
	}

	@SuppressWarnings("static-access")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getApplicationContext()
				.getSystemService(context.getApplicationContext().LAYOUT_INFLATER_SERVICE);

		final View rowView = inflater.inflate(R.layout.notification_list_view, parent, false);
		TextView notificationMessage = (TextView) rowView.findViewById(R.id.notification_content);
		TextView notificationType= (TextView) rowView.findViewById(R.id.notification_type);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.notification_user_profile);
		notificationMessage.setText(notification.get(position)[NOTIFICATION_MESSAGE]);
		if(!notification.get(position)[STATE].equals(VIEWED))
		{
			notificationType.setTextColor(Color.parseColor("#000000"));
			notificationMessage.setTextColor(Color.parseColor("#000000"));
		}
		else
		{
			notificationType.setTextColor(Color.GRAY);
			notificationMessage.setTextColor(Color.GRAY);
		}
		if(notification.get(position)[TYPE].equals(FRIEND_REQUEST_TYPE))
		{
			imageView.setImageResource(R.drawable.friend_request_notify_icon);
			notificationType.setText("Friend request");
		}
		else if(notification.get(position)[TYPE].equals(EVENT_TYPE))
		{
			imageView.setImageResource(R.drawable.event_notify_icon);
			notificationType.setText("Event");
		}
		else if(notification.get(position)[TYPE].equals(EVENT_LAUNCH_NOTIFY_TYPE))
		{
			imageView.setImageResource(R.drawable.event_launch_notify_icon);
			notificationType.setText("Event");
		}
		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(notification.get(position)[TYPE].equals(FRIEND_REQUEST_TYPE))
				{
					if(!notification.get(position)[ConstantManager.STATE].equals(ConstantManager.NOTIFICATION_VIEWED))
					{
						final Dialog dialog = new Dialog(context);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.activity_approve_friend_request);

						//layout
						WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
						lp.copyFrom(dialog.getWindow().getAttributes());
						lp.width = WindowManager.LayoutParams.MATCH_PARENT;
						dialog.getWindow().setAttributes(lp);

						dialog.show();


						Button dialogButtonuYes = (Button) dialog.findViewById(R.id.approve_friend_request_yes);
						Button dialogButtonNo = (Button) dialog.findViewById(R.id.approve_friend_request_no);
						// if button is clicked, close the custom dialog
						dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
								approveFriendRequest(notification.get(position)[ConstantManager.REFER_ID],notification.get(position)[ConstantManager.NOTIFICATION_ID]);
								notification.get(position)[ConstantManager.STATE] = ConstantManager.NOTIFICATION_VIEWED;
								notifyDataSetChanged();
								dialog.dismiss();
							}
						});
						dialogButtonNo.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
					}
				}
				else if(notification.get(position)[TYPE].equals(EVENT_LAUNCH_NOTIFY_TYPE))
				{
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);


					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("id", notification.get(position)[ConstantManager.NOTIFICATION_ID]);

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.SET_NOTIFICATION_VIEWED);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"notifications/setNotificationViewed",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("Public");

					//add the request into request queue
					requestQueue.add(volleyRequest);


					//set the state as viewed
					notification.get(position)[ConstantManager.STATE] = ConstantManager.NOTIFICATION_VIEWED;
					notifyDataSetChanged();
					Intent intent;
					intent  = new Intent(context, PendingEventDetailsActivity.class);
					intent.putExtra("eventId",notification.get(position)[ConstantManager.REFER_ID]);
					Activity activity = (Activity) context;
					activity.startActivityForResult(intent, GeneralManager.constantManager.VIEW_EVENT_ACTIVITY);
					//Toast.makeText(context.getApplicationContext(), notification.get(position)[ConstantManager.NOTIFICATION_MESSAGE], Toast.LENGTH_LONG).show();

				}
				else
				{
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);


					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("id", notification.get(position)[ConstantManager.NOTIFICATION_ID]);

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.SET_NOTIFICATION_VIEWED);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"notifications/setNotificationViewed",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("Public");

					//add the request into request queue
					requestQueue.add(volleyRequest);

					//set the state as viewed
					notification.get(position)[ConstantManager.STATE] = ConstantManager.NOTIFICATION_VIEWED;
					notifyDataSetChanged();
				}

			}
		});

		rowView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				ListView listView = (ListView)rowView.getParent();
				//remove from view

				ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();

				//String[] temptemp = notification.get(position);
				notificationIds.add(notification.get(position)[4]);


				//create params
				Map<String, String> mParams = new HashMap<String,String>();
				mParams.put("idsToRemove",notification.get(position)[4]);

				//create error listener
				Response.ErrorListener errorListner = new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
					}
				};

				//craete the listener on call back
				StringVolleyResponseListener listener = createNewRequest(ConstantManager.DISMISS_NOTIFICATION);
				CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"notifications/dismissNotification",mParams,listener,errorListner);

				//set tag
				volleyRequest.setTag("Public");

				//add the request into request queue
				requestQueue.add(volleyRequest);

				//remove from database
				adapter.remove(adapter.getItem(position));
				adapter.notifyDataSetChanged();
				return false;
			}
		});
		return rowView;

	}

	public void approveFriendRequest(String id,String nid)
	{

		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("requestorID",GeneralManager.authManager.userId);
		mParams.put("targetID", id);
		mParams.put("nid", nid);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.LINK_FRIEND);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"friends/linkFriend",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Public");

		//add the request into request queue
		requestQueue.add(volleyRequest);

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
					case ConstantManager.DISMISS_NOTIFICATION:
					case ConstantManager.SET_NOTIFICATION_VIEWED:

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
}

package com.my.dhreelife.fragment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.my.dhreelife.R;
import com.my.dhreelife.activity.SponsorDetailsActivity;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.NotificationArrayListAdapter;
import com.my.dhreelife.util.PublicAdvertisementArrayListAdapter;
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


/**
 * Created by Obana on 8/13/13.
 */
public class PublicActivity extends Fragment implements ActionBar.TabListener{
	ListView publicList ;
	private Fragment mFragment;
	private List<String[]> notificationList = null;
	private List<String[]> sponsorList = null;
	private Button notificationButton;
	private Button advertisementButton;
	private boolean advertisementOff = true;
	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private boolean onLoading = false;
	private int offset = 0;
	private ProgressBar loadingMoreNotificationPb;
	private RequestQueue requestQueue;


	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.PUBLIC_ACTIVITY_ID;

		ConstantManager.CURRENT_FRAGMENT = PublicActivity.this;
		//initialize the request queue object
		if(getActivity()!=null)
			requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();

		View v = inflater.inflate(R.layout.activity_public, container, false);
		notificationList = new ArrayList<String[]>();
		offset = 0;
		if(v!=null)
		{
			publicList = (ListView) v.findViewById(R.id.public_list);
			loadingPb = (ProgressBar) v.findViewById(R.id.publicLoadingPb);
			loadingMoreNotificationPb = (ProgressBar) v.findViewById(R.id.notificationWindowPb);
			loadingTxt = (TextView) v.findViewById(R.id.publicLoadingTxt);


			//show progress bar, loading notification
			showProgress(true);
			refreshNotificationList();

			notificationButton = (Button) v.findViewById(R.id.btn_notification);
			advertisementButton = (Button) v.findViewById(R.id.btn_advertisement);

			notificationButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
					//on execute only if the current list is advertisement
					if(!advertisementOff&&requestQueue!=null)
					{
						advertisementOff = true;
						offset = 0;
						//cancel all pending request
						requestQueue.cancelAll("Public");


						//create params
						Map<String, String> mParams = new HashMap<String,String>();
						mParams.put("id",GeneralManager.authManager.userId);
						mParams.put("offset","0");

						//create error listener
						Response.ErrorListener errorListner = new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								if(getActivity()!=null)
									Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
							}
						};

						//craete the listener on call back
						StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_NOTIFICATION_LIST);
						CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"notifications/getNotificationList",mParams,listener,errorListner);

						//set tag
						volleyRequest.setTag("Public");

						//add the request into request queue
						requestQueue.add(volleyRequest);


						showProgress(true);
						loadingTxt.setText("Loading ..");
						advertisementButton.setBackgroundResource(R.drawable.sponsored1);
						notificationButton.setBackgroundResource(R.drawable.notification2);
					}
				}
			});

			publicList.setOnScrollListener(new AbsListView.OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {


				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					//already scroll to the bottom
					final int lastItem = firstVisibleItem + visibleItemCount;

					if(lastItem==totalItemCount&&!onLoading&&advertisementOff)
					{
						loadingMoreNotificationPb.setVisibility(View.VISIBLE);
						offset+=ConstantManager.NUMBER_OF_NEW_LOAD_EVENT;
						refreshNotificationList();
					}
				}
			});

			advertisementButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
					//only execute only if the current list is notification
					showProgress(true);
					if(advertisementOff&&requestQueue!=null)
					{
						//cancel all pending request
						requestQueue.cancelAll("Public");
						//reset the variable for notification
						notificationList = new ArrayList<String[]>();
						offset = 0;

						advertisementOff = false;
						advertisementButton.setBackgroundResource(R.drawable.sponsored2);
						notificationButton.setBackgroundResource(R.drawable.notification1);




						//create params
						Map<String, String> mParams = new HashMap<String,String>();
						mParams.put("id",GeneralManager.authManager.userId);

						//create error listener
						Response.ErrorListener errorListner = new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								if(getActivity()!=null)
									Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
							}
						};

						//craete the listener on call back
						StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_ALL_SPONSOR_ADVERTISEMENT);
						CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"sponsors/getAllSponsorAdvertisement",mParams,listener,errorListner);

						//set tag
						volleyRequest.setTag("Public");

						//add the request into request queue
						requestQueue.add(volleyRequest);



					}
				}
			});


		}

		return v;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new PublicActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);
		//if request queue is not null then cancel all
		if(requestQueue!=null)
			requestQueue.cancelAll("Public");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}




	public void showProgress(boolean show)
	{
		if(show)
		{
			publicList.setVisibility(View.GONE);
			loadingPb.setVisibility(View.VISIBLE);
			loadingTxt.setVisibility(View.VISIBLE);
		}
		else
		{
			publicList.setVisibility(View.VISIBLE);
			loadingPb.setVisibility(View.GONE);
			loadingTxt.setVisibility(View.GONE);
		}
	}

	public void approveFriendRequest(String id,String nid)
	{
		Long.parseLong(id);


		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("requestorID",GeneralManager.authManager.userId);
		mParams.put("targetID", id);
		mParams.put("nid", nid);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(getActivity()!=null)
					Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.LINK_FRIEND);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"sponsors/getAllSponsorAdvertisement",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Public");

		//add the request into request queue
		requestQueue.add(volleyRequest);
	}

	public void refreshNotificationList()
	{
		if(requestQueue!=null)
		{
			onLoading = true;


			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",GeneralManager.authManager.userId);
			mParams.put("offset", String.valueOf(offset));

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_NOTIFICATION_LIST);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"notifications/getNotificationList",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Public");

			//add the request into request queue
			requestQueue.add(volleyRequest);
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
					onLoading = false;
					showProgress(false);

					switch(taskId)
					{
					case ConstantManager.LINK_FRIEND:

						try {
							JSONObject result = new JSONObject (response);
							if(result!=null)
							{
								if(!result.has("error"))
								{
									//friend request approved, update notification list
									try {
										Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_LONG).show();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								else
								{
									try {
										if(getActivity()!=null)
											Toast.makeText(getActivity(), result.getString("error"), Toast.LENGTH_LONG).show();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}

						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;

					case ConstantManager.GET_ALL_SPONSOR_ADVERTISEMENT:

						try {
							JSONArray result = new JSONArray (response);

							if (result != null) {
								if(publicList!=null)
								{
									sponsorList = new ArrayList<String[]>();
									for(int i=0;i<result.length();i++)
									{
										try {
											final JSONObject jsonObject = (JSONObject) result.get(i);
											String temp[] = new String[8];
											temp[0] = new JSONObject(jsonObject.getString("Sponsor")).getString("id");
											temp[1] = new JSONObject(jsonObject.getString("Sponsor")).getString("title");
											temp[2] = new JSONObject(jsonObject.getString("Sponsor")).getString("description");
											temp[3] = new JSONObject(jsonObject.getString("Sponsor")).getString("location");
											temp[4] = new JSONObject(jsonObject.getString("Sponsor")).getString("maxattendees");
											temp[5] = new JSONObject(jsonObject.getString("Sponsor")).getString("profile_url");
											temp[6] = new JSONObject(jsonObject.getString("Sponsor")).getString("time");
											temp[7] = new JSONObject(jsonObject.getString("Sponsor")).getString("flier_url");
											sponsorList.add(temp);
										}
										catch (JSONException e) {
											e.printStackTrace();
										}

									}
									if(getActivity()!=null&&!advertisementOff)
									{
										PublicAdvertisementArrayListAdapter adapter = new PublicAdvertisementArrayListAdapter(getActivity(),sponsorList);
										publicList.setAdapter(adapter);
										publicList.setSelection(publicList.getCount()-1);
										publicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, final View view,
													int position, long id) {
												GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
												final String[] selectedAdvertisement = (String[]) parent.getItemAtPosition(position);
												Intent myIntent = new Intent(getActivity(), SponsorDetailsActivity.class);
												//pass user id to next activity
												myIntent.putExtra("sponsorEvent", "1");
												myIntent.putExtra("title", selectedAdvertisement[1]);
												myIntent.putExtra("description", selectedAdvertisement[2]);
												myIntent.putExtra("location", selectedAdvertisement[3]);
												myIntent.putExtra("maxattendees", selectedAdvertisement[4]);
												myIntent.putExtra("flierurl", selectedAdvertisement[7]);
												myIntent.putExtra("time", selectedAdvertisement[6]);

												getActivity().startActivity(myIntent);

											}
										});
									}
								}

							}

						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						break;

					case ConstantManager.GET_NOTIFICATION_LIST:
						//hide the progress bar
						loadingMoreNotificationPb.setVisibility(View.INVISIBLE);
						try {
							JSONArray result = new JSONArray (response);

							if (result != null) {
								if(publicList!=null)
								{
									int lastViewedPosition = publicList.getFirstVisiblePosition();

									//get offset of first visible view
									View v = publicList.getChildAt(0);
									int topOffset = (v == null) ? 0 : v.getTop();

									for(int i=0;i<result.length();i++)
									{
										try {
											final JSONObject jsonObject = (JSONObject) result.get(i);
											String temp[] = new String[5];
											temp[0] = new JSONObject(jsonObject.getString("Notification")).getString("message");
											temp[1] = new JSONObject(jsonObject.getString("Notification")).getString("type");
											temp[2] = new JSONObject(jsonObject.getString("Notification")).getString("refer_id");
											temp[3] = new JSONObject(jsonObject.getString("Notification")).getString("state");
											temp[4] = new JSONObject(jsonObject.getString("Notification")).getString("id");
											notificationList.add(temp);
										}
										catch (JSONException e) {
											e.printStackTrace();
										}

									}
									if(getActivity()!=null&&advertisementOff)
									{
										NotificationArrayListAdapter adapter = new NotificationArrayListAdapter(getActivity(),notificationList);
										publicList.setAdapter(adapter);
										publicList.setSelectionFromTop(lastViewedPosition, topOffset);
										if(offset>notificationList.size())
										{
											onLoading = true;
											Toast.makeText(getActivity().getApplicationContext(), "No more notification.", Toast.LENGTH_LONG).show();
										}
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


}

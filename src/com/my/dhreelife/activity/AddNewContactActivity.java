package com.my.dhreelife.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.SearchUserArrayListAdapter;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class AddNewContactActivity extends Activity{


	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private Context context = this;
	private List<String[]> names;
	private ListView searchResult;
	private Button backButton;
	private RequestQueue requestQueue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_add_new_friend);
		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();
		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);

		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(this, R.raw.click, 1); 
		ConstantManager.CURRENT_TASK = ConstantManager.ADD_NEW_CONTACT_ACTIVITY_ID;

		loadingPb = (ProgressBar) findViewById(R.id.friendRequestLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.friendRequestLoadingTxt);
		showProgress(false);
		Button btn1 = (Button) findViewById(R.id.friend_request);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1, 1, 0, 1);
				EditText newContactUsername = (EditText) findViewById(R.id.add_new_contact_username);
				if(!newContactUsername.getText().toString().equals(""))
				{	
					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("User[keyword]", newContactUsername.getText().toString());

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.SEARCH_USER);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/searchUser",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("Public");

					//add the request into request queue
					requestQueue.add(volleyRequest);


					showProgress(true);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please input the keyword.", Toast.LENGTH_LONG).show();
				}
			}
		});

		backButton = (Button) findViewById(R.id.addNewFriendBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_new_contact, menu);
		return true;
	}


	public void populateSearchResultList(JSONArray result)
	{
		searchResult = (ListView) findViewById(R.id.linearLayout1);
		names = new ArrayList<String[]>();
		for(int i=0;i<result.length();i++)
		{
			try {
				final JSONObject jsonObject = (JSONObject) result.get(i);
				String temp[] = new String[3];
				temp[0] = new JSONObject(jsonObject.getString("User")).getString("name");
				temp[1] = new JSONObject(jsonObject.getString("User")).getString("id");
				temp[2] = new JSONObject(jsonObject.getString("User")).getString("profilephoto");
				names.add(temp);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		SearchUserArrayListAdapter adapter = new SearchUserArrayListAdapter(this,names);
		searchResult.setAdapter(adapter);
		searchResult.setOnItemClickListener(new OnItemClickListener()
		{
			@Override 
			public void onItemClick(AdapterView<?> arg0, View arg1,final int position, final long id)
			{ 
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1, 1, 0, 1);
				if(ConstantManager.friendManager!=null&&ConstantManager.friendManager.isFriend(String.valueOf(id)))
				{
					Toast.makeText(context, "This person is already in your friend list.", Toast.LENGTH_SHORT).show();
				}
				else if(String.valueOf(id).equals(GeneralManager.authManager.userId))
				{
					Toast.makeText(context, "You can not add yourself as a friend.", Toast.LENGTH_SHORT).show();
				}
				else
				{

					final Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.activity_send_friend_request);

					//layout
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					dialog.getWindow().setAttributes(lp);

					dialog.show();

					Button dialogButtonuYes = (Button) dialog.findViewById(R.id.send_friend_request_yes);
					Button dialogButtonNo = (Button) dialog.findViewById(R.id.send_friend_request_no);
					// if button is clicked, close the custom dialog
					dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							sendFriendRequest(String.valueOf(id));
							ArrayAdapter adapter = (ArrayAdapter) searchResult.getAdapter();
							adapter.remove(adapter.getItem(position));
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
		});


		showProgress(false);
	}

	public void sendFriendRequest(String id)
	{
		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("requestorID",GeneralManager.authManager.userId);
		mParams.put("targetID", id);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.SEND_FRIEND_REQUEST);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"friends/sendFriendRequest",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("AddNewContact");

		//add the request into request queue
		requestQueue.add(volleyRequest);

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
				int taskId = getTaskId();
				showProgress(false);
				switch(taskId)
				{
				case ConstantManager.SEARCH_USER:

					try {
						JSONArray result = new JSONArray(response);
						if (result != null) {
							populateSearchResultList(result);

						}
					}
					catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
				case ConstantManager.SEND_FRIEND_REQUEST:

					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								Toast.makeText(context, "Friend request sent.", Toast.LENGTH_LONG).show();
							}
						}
					}
					catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;

				}
			}
		};
		stringVolleyResponseListener.setTaskId(taskId); 
		return stringVolleyResponseListener;
	}
}

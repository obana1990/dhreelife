package com.my.dhreelife.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.model.User;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class TagGroupActivity extends Activity{

	private User[] groups;
	private CheckBox[] checkbox;
	private Button btnCompleteTag;
	private Button backButton;
	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private final int OK = 1;
	private String tagList="";
	private String tagListNameOnly="";
	private ProgressDialog progressDialog;
	private Context context;
	private RequestQueue requestQueue;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_group);
		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();
		context = this;
		ConstantManager.CURRENT_TASK = ConstantManager.TAG_FRIEND_ACTIVITY_ID;
		progressDialog = new ProgressDialog(context);
		loadingPb = (ProgressBar) findViewById(R.id.contactLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.contactLoadingTxt); 
		backButton = (Button)findViewById(R.id.tagFriendBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});
		Bundle extras = getIntent().getExtras();
		if(extras!=null)
		{

			btnCompleteTag = (Button) findViewById(R.id.btnCompleteTag);
			btnCompleteTag.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(context, "", "Tagging group...", true);
					for(int i=0;i<checkbox.length;i++)
					{
						if(checkbox[i].isChecked())
						{
							tagListNameOnly = tagListNameOnly+groups[i].getName()+",";
							tagList = tagList+ groups[i].getUserId()+",";
						}
					}

					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("ids",tagList);

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_GROUP_USER_ID);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"groupUsers/getGroupUsersId",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("Public");

					//add the request into request queue
					requestQueue.add(volleyRequest);



				}
			});

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",GeneralManager.authManager.userId);

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_FRIEND_LIST);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"friends/getFriendList",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Public");

			//add the request into request queue
			requestQueue.add(volleyRequest);


		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}


	public void populateGroupList()
	{
		LinearLayout ln1 = (LinearLayout) findViewById(R.id.linearLayoutGroupList);
		checkbox = new CheckBox[groups.length];
		for(int i=0;i<groups.length;i++)
		{
			checkbox[i] = new CheckBox(this.getBaseContext());
			TextView nameOfFriend = new TextView(this.getBaseContext());
			nameOfFriend.setText(groups[i].getName());

			LinearLayout linearLayout = new LinearLayout(this.getBaseContext());
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			linearLayout.setWeightSum(100);
			nameOfFriend.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 80));
			nameOfFriend.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			nameOfFriend.setTextColor(Color.GRAY);
			checkbox[i].setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 20));
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.addView(nameOfFriend);
			linearLayout.addView(checkbox[i]);
			ln1.addView(linearLayout);
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

	@Override
	public void finish() {
		// Prepare data intent 
		Intent data = new Intent();
		data.putExtra("tagList", tagList);
		data.putExtra("tagListNameOnly", tagListNameOnly);
		// Activity finished ok, return the data
		setResult(OK, data);
		super.finish();
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
				case ConstantManager.GET_FRIEND_LIST:

					try {
						JSONObject result = new JSONObject(response);
						JSONArray groupListInJson;
						if (result != null) {

							groupListInJson = new JSONArray (result.getString("grouplist"));
							groups = new User[groupListInJson.length()];
							for(int i=0;i<groupListInJson.length();i++)
							{

								final JSONObject jsonObject = (JSONObject) groupListInJson.get(i);
								String temp[] = new String[2];
								temp[0] = new JSONObject(jsonObject.getString("Group")).getString("name");
								temp[1] = new JSONObject(jsonObject.getString("Group")).getString("id");
								groups[i] = new User();
								groups[i].setName(temp[0]);
								groups[i].setUserId(temp[1]);
							}
							populateGroupList();
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
				case ConstantManager.GET_GROUP_USER_ID:

					try {
						JSONObject result = new JSONObject(response);
						if(!result.has("error"))
						{
							tagList = result.getString("ids");
							finish();
						}
					} catch (JSONException e1) {
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
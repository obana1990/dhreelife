package com.my.dhreelife.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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


@SuppressLint("NewApi")
public class AddNewGroupActivity extends Activity {


	private User[] friends;
	private CheckBox[] checkbox;

	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private ProgressDialog progressDialog;
	private Context context;
	private Button backButton; 
	private RequestQueue requestQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_add_new_group);

		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();

		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);

		context = this; 

		ConstantManager.CURRENT_TASK = ConstantManager.ADD_NEW_GROUP_ACTIVITY_ID;
		loadingPb = (ProgressBar) findViewById(R.id.groupLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.groupLoadingTxt);
		progressDialog = new ProgressDialog(context);

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



		Button createGroup = (Button) findViewById(R.id.btn_create_group);
		createGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				EditText groupName = (EditText) findViewById(R.id.group_name);

				if(groupName.getText().toString().length()>0)
				{
					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("Group[name]",groupName.getText().toString());
					mParams.put("Group[id]",GeneralManager.authManager.userId);

					int memberSize = 0;
					for(int i=0;i<friends.length;i++)
					{
						if(checkbox[i].isChecked())
						{ 
							mParams.put("Group[groupusers]["+String.valueOf(i)+"]",friends[i].getUserId());
							memberSize+=1;
						}
					}
					if(memberSize>0)
					{
						progressDialog = ProgressDialog.show(context, "", "Creating group...", true);

						//create error listener
						Response.ErrorListener errorListner = new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
							}
						};

						//craete the listener on call back
						StringVolleyResponseListener listener = createNewRequest(ConstantManager.ADD_GROUP);
						CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"groups/create",mParams,listener,errorListner);

						//set tag
						volleyRequest.setTag("AddNewGroupActivity");

						//add the request into request queue
						requestQueue.add(volleyRequest);


					}
					else
						Toast.makeText(getBaseContext(), "Please choose at least one user to add in the group.", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getBaseContext(), "Please specify the group name.", Toast.LENGTH_LONG).show();
				}
			}
		});

		backButton = (Button) findViewById(R.id.groupBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void populateFriendList()
	{
		checkbox = new CheckBox[friends.length];
		LinearLayout ln1 = (LinearLayout) findViewById(R.id.linearLayout1);

		for(int i=0;i<friends.length;i++)
		{
			checkbox[i] = new CheckBox(this.getBaseContext());
			TextView nameOfFriend = new TextView(this.getBaseContext());
			nameOfFriend.setText(friends[i].getName());
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
		data.putExtra("REFRESH", "TRUE");
		// Activity finished ok, return the data
		setResult(ConstantManager.OK, data);
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
						if (result != null) 
						{
							JSONArray jsonArray;
							jsonArray = new JSONArray(result.getString("friendlist"));
							friends = new User[jsonArray.length()];
							for(int i=0;i<jsonArray.length();i++)
							{
								try {
									final JSONObject jsonObject = (JSONObject) jsonArray.get(i);
									String temp[] = new String[2];
									temp[0] = new JSONObject(jsonObject.getString("Friend")).getString("name");
									temp[1] = new JSONObject(jsonObject.getString("Friend")).getString("friend_id");
									friends[i] = new User();
									friends[i].setName(temp[0]);
									friends[i].setUserId(temp[1]);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							populateFriendList();
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
				case ConstantManager.ADD_GROUP:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) 
						{
							if (result != null) {
								if(!result.has("error"))
								{
									Toast.makeText(getBaseContext(), "New group added.", Toast.LENGTH_LONG).show();
									finish();
								}
								else
								{
									Toast.makeText(getBaseContext(), "Cannot add group, please try again later.", Toast.LENGTH_LONG).show();
									finish();
								}
							}
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

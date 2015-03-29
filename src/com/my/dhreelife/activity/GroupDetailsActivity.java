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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.my.dhreelife.util.GroupDetailsArrayListAdapter;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;


public class GroupDetailsActivity extends Activity{

	private String groupId;
	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private Context context;
	private Activity activity;
	private Button btnUpdateGroup;
	private final int TAG_FRIEND_ACTIVITY = 950;
	private final int OK = 1;
	private Button btnDeleteGroup;
	private Button btnAddUser;
	private Button backButton;
	private boolean refreshNeeded = false;
	private ProgressDialog progressDialog;
	private ListView groupUserListView;
	private RequestQueue requestQueue;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_group_details);
		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();
		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);

		context = this;
		ConstantManager.CURRENT_TASK = ConstantManager.GROUP_DETAILS_ACTIVITY_ID;
		Bundle extras = getIntent().getExtras();
		groupId = extras.getString("groupId");
		progressDialog = new ProgressDialog(context);


		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("id",groupId);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_GROUP_USER);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"groupUsers/getGroupUserList",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Public");

		//add the request into request queue
		requestQueue.add(volleyRequest);



		loadingPb = (ProgressBar) findViewById(R.id.groupLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.groupLoadingTxt); 

		btnUpdateGroup = (Button) findViewById(R.id.btn_update_group);
		btnDeleteGroup = (Button) findViewById(R.id.btn_delete_group);
		groupUserListView = (ListView) findViewById(R.id.groupUserList);
		btnAddUser = (Button) findViewById(R.id.btnAddGroupUser);
		showProgress(true);

		backButton = (Button) findViewById(R.id.groupDetailsBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});


		btnAddUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,TagFriendActivity.class);
				intent.putExtra("userId", GeneralManager.authManager.userId);
				startActivityForResult(intent,TAG_FRIEND_ACTIVITY);

			}
		});

		btnUpdateGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GroupDetailsArrayListAdapter adapter = (GroupDetailsArrayListAdapter) groupUserListView.getAdapter();
				List<String> data = adapter.getData();

				//create params
				Map<String, String> mParams = new HashMap<String,String>();
				mParams.put("Group[id]",groupId);

				loadingTxt.setText("Submitting request..");
				for(int i=0;i<data.size();i++)
				{
					mParams.put("Group[groupusers]["+String.valueOf(i)+"]",data.get(i));
				}

				//create error listener
				Response.ErrorListener errorListner = new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
					}
				};

				//craete the listener on call back
				StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_GROUP_USER);
				CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"groupUsers/updateGroup",mParams,listener,errorListner);

				//set tag
				volleyRequest.setTag("Public");

				//add the request into request queue
				requestQueue.add(volleyRequest);



				showProgress(true);
			}
		});

		btnDeleteGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.activity_remove_group);

				//layout
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				dialog.getWindow().setAttributes(lp);

				dialog.show();

				Button dialogButtonuYes = (Button) dialog.findViewById(R.id.btnRemoveGroupYes);
				Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnRemoveGroupNo);
				// if button is clicked, close the custom dialog
				dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//remove the group
						progressDialog = ProgressDialog.show(context, "", "Removing group...", true);
						requestRemoveGroup();
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
		});



	}



	public void showProgress(boolean show)
	{
		if(show)
		{
			loadingPb.setVisibility(View.VISIBLE);
			loadingTxt.setVisibility(View.VISIBLE);
			btnUpdateGroup.setEnabled(false);
			btnDeleteGroup.setEnabled(false);
			btnAddUser.setEnabled(false);
			btnUpdateGroup.setVisibility(View.INVISIBLE);
			btnDeleteGroup.setVisibility(View.INVISIBLE);
			groupUserListView.setVisibility(View.INVISIBLE);
		}
		else
		{
			loadingPb.setVisibility(View.GONE);
			loadingTxt.setVisibility(View.GONE);
			btnUpdateGroup.setEnabled(true);
			btnDeleteGroup.setEnabled(true);
			btnAddUser.setEnabled(true);
			btnUpdateGroup.setVisibility(View.VISIBLE);
			btnDeleteGroup.setVisibility(View.VISIBLE);
			groupUserListView.setVisibility(View.VISIBLE);
		}
	}

	public void requestRemoveGroup()
	{
		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("id",groupId);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.REMOVE_GROUP);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"groups/removeGroup",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Public");

		//add the request into request queue
		requestQueue.add(volleyRequest);


	}

	@Override
	public void finish() {
		// Prepare data intent 
		Intent data = new Intent();
		if(refreshNeeded)
			data.putExtra("REFRESH", "TRUE");
		// Activity finished ok, return the data
		setResult(GeneralManager.constantManager.OK, data);
		super.finish();
	}

	public void repopulateGroup()
	{

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == OK && requestCode == TAG_FRIEND_ACTIVITY)
		{
			String tagListName = data.getExtras().getString("tagListNameOnly");
			String tagListId = data.getExtras().getString("tagList");

			if(tagListName!=null&&tagListId!=null&&tagListName.length()>0&&tagListId.length()>0)
			{
				String temp1[] = tagListId.split(",");
				String temp2[] = tagListName.split(",");

				//they should match in length
				if(temp1.length==temp2.length)
				{
					GroupDetailsArrayListAdapter adapter = (GroupDetailsArrayListAdapter) groupUserListView.getAdapter();
					for(int i=0;i<temp1.length;i++)
					{
						adapter.add(temp1[i]+","+temp2[i]);
					}

				}
			}
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
				case ConstantManager.GET_GROUP_USER:

					try {
						JSONArray result = new JSONArray(response);
						List<String> arrayListContent = new ArrayList<String>();
						for(int i=0;i<result.length();i++)
						{

							try {
								JSONObject temp = (JSONObject) result.get(i);
								final JSONObject jsonObject = new JSONObject(temp.getString("GroupUser"));

								String id = jsonObject.getString("user_id");
								String name = jsonObject.getString("user_name");
								String groupUserInfo = id+","+name;
								arrayListContent.add(groupUserInfo);							

								//build up a list instead of using the array
								//listOfMemberName.add(jsonObject.getString("user_name"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						GroupDetailsArrayListAdapter adapter = new GroupDetailsArrayListAdapter(context,arrayListContent);
						groupUserListView.setAdapter(adapter);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;

				case ConstantManager.REMOVE_GROUP:

					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								Toast.makeText(context, "Group removed.", Toast.LENGTH_LONG).show();
								refreshNeeded = true;
								finish();
							} else
							{
								Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;

				case ConstantManager.UPDATE_GROUP_USER:

					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								Toast.makeText(context, "Group updated.", Toast.LENGTH_LONG).show();
							} else
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
		};
		stringVolleyResponseListener.setTaskId(taskId); 
		return stringVolleyResponseListener;
	}

}

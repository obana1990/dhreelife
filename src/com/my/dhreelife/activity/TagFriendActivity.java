package com.my.dhreelife.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.my.dhreelife.util.TagFriendArrayListAdapter;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class TagFriendActivity extends Activity{

	private User[] friends;
	private EditText searchContactList;
	private ListView contactList;
	private boolean search = false;
	private Button btnCompleteTag;
	private Button backButton;
	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private final int OK = 1;
	private String tagList="";
	private String tagListNameOnly="";
	private RequestQueue requestQueue;
	private Context context = this; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_friend);
		ConstantManager.CURRENT_TASK = ConstantManager.TAG_FRIEND_ACTIVITY_ID;
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
			contactList = (ListView) findViewById(R.id.contact_list);
			contactList.setDivider(null);
			searchContactList = (EditText) findViewById(R.id.search_contact_list);

			if (searchContactList != null) {
				searchContactList.setHint("Search");
				searchContactList.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

					}

					@Override
					public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
						int textLength = searchContactList.getText().length();
						if (textLength == 0) {
							searchContactList.setHint("Search");
							search = false;
						} else {
							searchContactList.setHint("");
							search = true;
						}
						populateContactList(friends);
					}

					@Override
					public void afterTextChanged(Editable editable) {

					}
				});
			}

			btnCompleteTag = (Button) findViewById(R.id.btnCompleteTag);
			btnCompleteTag.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					tagList = "";
					tagListNameOnly="";
					for(int i=0;i<friends.length;i++)
					{
						if(friends[i].checked)
						{
							tagList = tagList+ friends[i].getUserId()+",";
							tagListNameOnly = tagListNameOnly+ friends[i].getName()+",";
						}

					}
					finish();
				}
			});

			//initialize the request queue object
			requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();

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

	@SuppressLint("UseSparseArrays")
	@SuppressWarnings("unchecked")
	public void populateContactList(User[] friends) {
		if (friends != null && friends.length > 0) {
			String[] sectionHeader = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
					"V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
			int sectionHeaderCounter = 0;
			showProgress(false);
			((LinearLayout)loadingPb.getParent()).invalidate();



			@SuppressWarnings("rawtypes")
			List<String[]> contact = new ArrayList();
			@SuppressWarnings("rawtypes")
			List<String> contactNameOnly = new ArrayList();
			//make a list of friend of name
			for (int i = 0; i < friends.length; i++) {
				String[] namesAndUserName = new String[2];
				namesAndUserName[0] = friends[i].getName();
				namesAndUserName[1] = friends[i].getUserId();
				contact.add(namesAndUserName);
				contactNameOnly.add(friends[i].getName());
			}
			Map<Integer, String> headerPosition = new HashMap<Integer, String>();

			//if search is needed
			if (search) {
				if (searchContactList != null) {
					String keyword = searchContactList.getText().toString();
					List<String[]> searchResult = new ArrayList<String[]>();
					for (int i = 0; i < contact.size(); i++) {
						if (contact.get(i)[0].contains(keyword)) {
							searchResult.add(contact.get(i));
						}
					}
					contact = new ArrayList<String[]>();
					contactNameOnly = new ArrayList<String>();
					for (int i = 0; i < searchResult.size(); i++) {
						contact.add(searchResult.get(i));
						contactNameOnly.add(searchResult.get(i)[0]);
					}
				}
			}
			//sort the list by name
			Collections.sort(contact, new Comparator<String[]>() {
				@Override
				public int compare(final String[] object1, final String[] object2) {
					return object1[0].compareToIgnoreCase(object2[0]);
				}
			} );
			//sort the list by name
			Arrays.sort(friends, new Comparator<User>() {
				@Override
				public int compare(User arg0, User arg1) {
					return arg0.getName().compareToIgnoreCase(arg1.getName());
				}
			} );
			for(int i=0;i<contact.size();i++)
			{
				friends[i].setName(contact.get(i)[0]);
				friends[i].setUserId(contact.get(i)[1]);
			}
			IgnoreCaseComparator icc = new IgnoreCaseComparator();
			Collections.sort(contactNameOnly,icc);


			//arrange the contact with header
			for (int i = 0; i < contact.size() && sectionHeaderCounter < sectionHeader.length; i++) {
				if (contact.get(i)[0].length() > 0) {
					//header found
					if (contact.get(i)[0].substring(0, 1).equalsIgnoreCase(sectionHeader[sectionHeaderCounter])) {
						if(!headerPosition.containsValue(sectionHeader[sectionHeaderCounter]))
							//only add if it's the first contact in that header
							headerPosition.put(i, sectionHeader[sectionHeaderCounter]);
						sectionHeaderCounter = 0;
					} 
					else {
						if (!headerPosition.containsValue(contact.get(i)[0].substring(0, 1))) {
							sectionHeaderCounter++;
							i--;
						}
					}
				}
			}

			TagFriendArrayListAdapter myAdapter = new TagFriendArrayListAdapter(this, contact ,contactNameOnly,headerPosition,friends);
			contactList.setAdapter(myAdapter);



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

	private class IgnoreCaseComparator implements Comparator<String> {
		public int compare(String strA, String strB) {
			return strA.compareToIgnoreCase(strB);
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
				case ConstantManager.GET_FRIEND_LIST:

					try {
						JSONObject result = new JSONObject(response);
						
						JSONArray friendListInJson;
						if (result != null) {
							try {
								friendListInJson = new JSONArray (result.getString("friendlist"));
								friends = new User[friendListInJson.length()];
								for(int i=0;i<friendListInJson.length();i++)
								{
									try {
										final JSONObject jsonObject = (JSONObject) friendListInJson.get(i);
										String temp[] = new String[3];
										temp[0] = new JSONObject(jsonObject.getString("Friend")).getString("name");
										temp[1] = new JSONObject(jsonObject.getString("Friend")).getString("friend_id");
										temp[2] = new JSONObject(jsonObject.getString("Friend")).getString("profilephoto");
										friends[i] = new User();
										friends[i].setName(temp[0]);
										friends[i].setUserId(temp[1]);
										friends[i].setProfileURL(temp[2]);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								populateContactList(friends);
							} catch (JSONException e1) {
								e1.printStackTrace();
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
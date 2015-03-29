package com.my.dhreelife.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.my.dhreelife.activity.AddNewContactActivity;
import com.my.dhreelife.activity.AddNewGroupActivity;
import com.my.dhreelife.model.User;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.ContactArrayListAdapter;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class ContactActivity extends Fragment implements ActionBar.TabListener {
	private Fragment mFragment;
	private ImageButton addContact;
	private EditText searchContactList;
	private ListView contactList;
	private boolean search = false;
	private Dialog dialog;

	private User[] friends;
	private User[] groups;
	private String userName;
	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private static boolean repopulateAfterResume = false;
	private RequestQueue requestQueue;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.CONTACT_ACTIVITY_ID;

		//initialize the request queue object
		if(getActivity()!=null)
			requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();

		requestFriendList();
		View v = inflater.inflate(R.layout.activity_contact, container, false);
		if (v != null) {
			loadingPb = (ProgressBar) v.findViewById(R.id.contactLoadingPb);
			loadingTxt = (TextView) v.findViewById(R.id.contactLoadingTxt); 

			contactList = (ListView) v.findViewById(R.id.contact_list);
			searchContactList = (EditText) v.findViewById(R.id.search_contact_list);

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
						populateContactList(friends,groups);
					}

					@Override
					public void afterTextChanged(Editable editable) {

					}
				});
			}


			addContact = (ImageButton) v.findViewById(R.id.add_contact);
			if (addContact != null)
				addContact.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
						dialog = new Dialog(getActivity());
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.activity_add_new_contact);
						//layout
						WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
						lp.copyFrom(dialog.getWindow().getAttributes());
						lp.width = WindowManager.LayoutParams.MATCH_PARENT;
						dialog.getWindow().setAttributes(lp);

						dialog.show();
						Button dialogButton1 = (Button) dialog.findViewById(R.id.btn_new_friend);
						Button dialogButton2 = (Button) dialog.findViewById(R.id.btn_new_group);
						// if button is clicked, close the custom dialog
						dialogButton1.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent myIntent = new Intent(getActivity(), AddNewContactActivity.class);
								//pass user id to next activity
								myIntent.putExtra("userName", userName);
								myIntent.putExtra("userId", GeneralManager.authManager.userId);
								getActivity().startActivity(myIntent);
								dialog.dismiss();
							}
						});
						dialogButton2.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent myIntent = new Intent(getActivity(), AddNewGroupActivity.class);
								//pass user id to next activity
								myIntent.putExtra("userId", GeneralManager.authManager.userId);
								startActivityForResult(myIntent, GeneralManager.constantManager.CREATE_GROUP_ACTIVITY);
								dialog.dismiss();
							}
						});
					}
				});
			if (contactList != null) {
				contactList.setDivider(null);
				//populateContactList(friends);

			} else {
				if(getActivity()!=null)
					Toast.makeText(getActivity().getBaseContext(), "Contact list was empty.", Toast.LENGTH_SHORT).show();
			}
		}



		return v;
	}



	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new ContactActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);

		//if request queue is not null then cancel all
		if(requestQueue!=null)
			requestQueue.cancelAll("Contact");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	@SuppressLint("UseSparseArrays")
	public void populateContactList(User[] friends,User[] groups) {
		List<String[]> contact = new ArrayList<String[]>();
		Map<Integer, String> headerPosition = new HashMap<Integer, String>();
		int numberOfContact = 0;
		List<String> contactNameOnly = new ArrayList<String>();
		if (friends != null && friends.length > 0) {
			String[] sectionHeader = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
					"V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
			int sectionHeaderCounter = 0;
			showProgress(false);
			((LinearLayout)loadingPb.getParent()).invalidate();




			//make a list of friend of name
			for (int i = 0; i < friends.length; i++) {
				String[] namesAndUserName = new String[3];
				namesAndUserName[0] = friends[i].getName();
				namesAndUserName[1] = friends[i].getUserId();
				namesAndUserName[2] = friends[i].getProfileURL();
				contact.add(namesAndUserName);
				contactNameOnly.add(friends[i].getName());
			}


			//if search is needed
			if (search) {
				if (searchContactList != null) {
					String keyword = searchContactList.getText().toString();
					List<String[]> searchResult = new ArrayList<String[]>();

					for (int i = 0; i < contact.size(); i++) {
						if (contact.get(i)[0].toLowerCase().contains(keyword.toLowerCase())) {
							numberOfContact++;
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
			else
			{
				numberOfContact = friends.length;
			}
			//sort the list by name
			Collections.sort(contact, new Comparator<String[]>() {
				@Override
				public int compare(final String[] object1, final String[] object2) {
					return object1[0].compareToIgnoreCase(object2[0]);
				}
			} );
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


		}
		if(groups!=null&& groups.length>0)
		{
			headerPosition.put(contact.size(), "Group");
			for(int i=0;i<groups.length;i++)
			{
				String []group = new String[2];
				group[0] = groups[i].getName();
				group[1] = groups[i].getUserId();
				contact.add(group);
				contactNameOnly.add(group[0]);
			}


		}

		if(groups.length>0||friends.length>0)
		{
			if(getActivity()!=null)
			{
				ContactArrayListAdapter myAdapter = new ContactArrayListAdapter(getActivity().getBaseContext(),ContactActivity.this,contact ,contactNameOnly,headerPosition,numberOfContact);
				contactList.setAdapter(myAdapter);
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

	public void requestFriendList()
	{
		if(requestQueue!=null)
		{
			//clear the list
			if(contactList!=null)
				contactList.setAdapter(null);

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
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_FRIEND_LIST);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"friends/getFriendList",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Contact");

			//add the request into request queue
			requestQueue.add(volleyRequest);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == ConstantManager.OK && (requestCode == GeneralManager.constantManager.CREATE_GROUP_ACTIVITY||requestCode == GeneralManager.constantManager.GROUP_DETAILS_ACTIVITY
				||requestCode == GeneralManager.constantManager.REMOVE_FRIEND_ACTIVITY)) {
			if (data.hasExtra("REFRESH")) {
				showProgress(true);
				requestFriendList();
			}
		}
	}


	public static boolean isRepopulateAfterResume() {
		return repopulateAfterResume;
	}



	public static void setRepopulateAfterResume(boolean repopulateAfterResume) {
		ContactActivity.repopulateAfterResume = repopulateAfterResume;
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
				if(getActivity()!=null)
				{
					int taskId = getTaskId();
					showProgress(false);
					switch(taskId)
					{
					case ConstantManager.GET_FRIEND_LIST:

						try {
							JSONObject result = new JSONObject(response);
							if(result!=null)
							{
								JSONArray friendListInJson;
								JSONArray groupListInJson;
								//friend part
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
								//group part
								groupListInJson = new JSONArray (result.getString("grouplist"));
								groups = new User[groupListInJson.length()];
								for(int i=0;i<groupListInJson.length();i++)
								{
									try {
										final JSONObject jsonObject = (JSONObject) groupListInJson.get(i);
										String temp[] = new String[3];
										temp[0] = new JSONObject(jsonObject.getString("Group")).getString("name");
										temp[1] = new JSONObject(jsonObject.getString("Group")).getString("id");

										groups[i] = new User();
										groups[i].setName(temp[0]);
										groups[i].setUserId(temp[1]);

									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								ConstantManager.friendManager.writeFriendToSharedPreferences(friends);
								populateContactList(friends,groups);
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

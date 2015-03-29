package com.my.dhreelife.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.Session;
import com.my.dhreelife.R;
import com.my.dhreelife.activity.EditPasswordActivity;
import com.my.dhreelife.activity.ProfileActivity;
import com.my.dhreelife.activity.ReminderConfigurationActivity;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.SettingArrayListAdapter;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;
import com.my.dhreelife.util.manager.SessionManager;


@SuppressLint("NewApi")
public class SettingActivity extends Fragment implements ActionBar.TabListener{

	private Fragment mFragment;
	private ListView settingList;
	private ProgressDialog progressDialog;
	private Context context;
	private RequestQueue requestQueue;
	//private Button updateSecurityAnswerBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.SETTING_ACTIVITY_ID;
		context = getActivity().getApplicationContext();
		View v = inflater.inflate(R.layout.activity_setting, container, false);

		//setup the menu
		settingList = (ListView) v.findViewById(R.id.lstSetting);
		final List<String> settingMenu = new ArrayList<String>();
		settingMenu.add("My Profile");
		settingMenu.add("Edit Password");
		settingMenu.add("Event Reminder");
		settingMenu.add("Logout");
		SettingArrayListAdapter adapter = new SettingArrayListAdapter(getActivity().getApplicationContext(),settingMenu);
		settingList.setAdapter(adapter);
		settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch(position)
				{
				default:
				case 0:
					intent = new Intent(context,ProfileActivity.class);
					getActivity().startActivity(intent);
					break;
				case 1:
					intent = new Intent(context,EditPasswordActivity.class);
					getActivity().startActivity(intent);
					break;
				case 2:
					intent = new Intent(context,ReminderConfigurationActivity.class);
					getActivity().startActivity(intent);
					break;
				case 3:
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
					final Dialog dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.activity_logout_dialog);

					//layout
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					dialog.getWindow().setAttributes(lp);

					dialog.show();

					Button dialogButtonuYes = (Button) dialog.findViewById(R.id.btnYes);
					Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnNo);
					// if button is clicked, close the custom dialog
					dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							progressDialog = new ProgressDialog(getActivity());

							progressDialog.setMessage("Logging out...");
							progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									requestQueue.cancelAll("Public");
								}
							});
							progressDialog.show();
							//initialize the request queue object
							if(getActivity()!=null)
							{
								requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();

								//create params
								Map<String, String> mParams = new HashMap<String,String>();
								mParams.put("id",GeneralManager.authManager.userId);
								mParams.put("deviceid", "");

								//create error listener
								Response.ErrorListener errorListner = new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError error) {
										if(getActivity()!=null)
											Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
									}
								};

								//craete the listener on call back
								StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_DEVICE_ID);
								CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/updateDeviceId",mParams,listener,errorListner);

								//set tag
								volleyRequest.setTag("Public");

								//add the request into request queue
								requestQueue.add(volleyRequest);

							}

						}
					});
					dialogButtonNo.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					break;
				}

			}
		});




		return v;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new SettingActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		fragmentTransaction.remove(mFragment);
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

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
					case ConstantManager.UPDATE_DEVICE_ID:

						try {
							JSONObject result = new JSONObject(response);
							if (result != null) {

								if(result.has("error"))
								{
									Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
								}
								else
								{
									Session facebookSession = Session.getActiveSession();
									facebookSession.closeAndClearTokenInformation();
									SessionManager session = new SessionManager(getActivity());
									session.logoutUser(GeneralManager.authManager.fbId);
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

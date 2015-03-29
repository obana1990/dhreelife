package com.my.dhreelife.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.ImageLoader;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class ContactDetailsActivity extends Activity {

	private  TextView contactName;
	private  TextView contactFbId;
	private  TextView contactUsername;
	private  TextView contactUserId;
	private  TextView contactPhoneNumber;
	private  TextView contactLocation;
	private TextView contactEventParticipated;
	private TextView contactJoined;
	private  TextView txtJoined;
	private  TextView txtFbId;
	private  TextView txtUserId;
	private  TextView txtPhoneNumber;
	private  TextView txtLocation;
	private  TextView txtUserName;
	private  TextView txtEventParticipated;

	private LinearLayout lytContactInfoContainer;

	private ImageView profilePhoto;
	private ImageLoader imageLoader; 
	private Button btnRemoveFriend;
	private Button backButton;

	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private Activity activity;
	private String userId;
	private Context context;
	private boolean friendRemoved = false;
	private ProgressDialog progressDialog;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private RequestQueue requestQueue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_contact_details);
		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();

		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);

		activity = this;
		context = this;
		ConstantManager.CURRENT_TASK = ConstantManager.CONTACT_DETAILS_ACTIVITY_ID;
		progressDialog = new ProgressDialog(context);

		loadingPb = (ProgressBar) findViewById(R.id.contactDetailsLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.contactDetailsLoadingTxt);
		imageLoader=new ImageLoader(this);
		btnRemoveFriend = (Button) findViewById(R.id.btnRemoveFriend);
		btnRemoveFriend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.activity_remove_friend);

				//layout
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				dialog.getWindow().setAttributes(lp);

				dialog.show();

				Button dialogButtonuYes = (Button) dialog.findViewById(R.id.btnRemoveFriendYes);
				Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnRemoveFriendNo);
				// if button is clicked, close the custom dialog
				dialogButtonuYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						progressDialog = ProgressDialog.show(context, "", "Removing user...", true);

						//create params
						Map<String, String> mParams = new HashMap<String,String>();
						mParams.put("id1",userId);
						mParams.put("id2",GeneralManager.authManager.userId);

						//create error listener
						Response.ErrorListener errorListner = new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
							}
						};

						//craete the listener on call back
						StringVolleyResponseListener listener = createNewRequest(ConstantManager.REMOVE_FRIEND);
						CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/remove",mParams,listener,errorListner);

						//set tag
						volleyRequest.setTag("ContactDetails");

						//add the request into request queue
						requestQueue.add(volleyRequest);


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

		backButton = (Button) findViewById(R.id.contactBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});

		Bundle extras = getIntent().getExtras();
		if(extras!=null)
		{
			userId = extras.getString("userId");

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",userId);

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.CONTACT_DETAILS_ACTIVITY);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/view",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("ContactDetails");

			//add the request into request queue
			requestQueue.add(volleyRequest);



			contactName = (TextView) findViewById(R.id.contact_name);
			contactFbId = (TextView) findViewById(R.id.contact_fb_id);
			contactUsername = (TextView) findViewById(R.id.contact_user_name);
			contactUserId = (TextView) findViewById(R.id.contact_user_id);
			contactPhoneNumber = (TextView) findViewById(R.id.contactPhoneNumber);
			contactLocation = (TextView) findViewById(R.id.contactLocation);
			contactEventParticipated = (TextView) findViewById(R.id.contactEventParticipated);
			contactJoined =  (TextView) findViewById(R.id.contactJoined);
			profilePhoto =  (ImageView) findViewById(R.id.contactProfilePhotoImg);

			txtJoined = (TextView) findViewById(R.id.txtJoined);
			txtFbId = (TextView) findViewById(R.id.txtFbID);
			txtUserId = (TextView) findViewById(R.id.txtUserID);
			txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
			txtLocation = (TextView) findViewById(R.id.txtLocation);
			txtUserName = (TextView) findViewById(R.id.txtUserName);
			txtEventParticipated = (TextView) findViewById(R.id.txtEventParticipated);

			lytContactInfoContainer = (LinearLayout) findViewById(R.id.lytContactInfoContainer);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}


	public void showProgress(boolean show)
	{
		if(show)
		{
			loadingPb.setVisibility(View.VISIBLE);
			loadingTxt.setVisibility(View.VISIBLE);

			profilePhoto.setVisibility(View.GONE);
			lytContactInfoContainer.setVisibility(View.VISIBLE);
			contactEventParticipated.setVisibility(View.VISIBLE);
			contactName.setVisibility(View.GONE);
			contactFbId.setVisibility(View.GONE);
			contactUsername.setVisibility(View.GONE);
			contactUserId.setVisibility(View.GONE);
			contactPhoneNumber.setVisibility(View.GONE);
			contactLocation.setVisibility(View.GONE);
			contactJoined.setVisibility(View.GONE);

			txtFbId.setVisibility(View.GONE);
			txtUserId.setVisibility(View.GONE);
			txtPhoneNumber.setVisibility(View.GONE);
			txtLocation.setVisibility(View.GONE);
			txtUserName.setVisibility(View.GONE);
			txtEventParticipated.setVisibility(View.GONE);
			txtJoined.setVisibility(View.GONE);
		}
		else
		{
			loadingPb.setVisibility(View.GONE);
			loadingTxt.setVisibility(View.GONE);

			profilePhoto.setVisibility(View.VISIBLE);
			lytContactInfoContainer.setVisibility(View.VISIBLE);
			contactEventParticipated.setVisibility(View.VISIBLE);
			contactName.setVisibility(View.VISIBLE);
			contactFbId.setVisibility(View.VISIBLE);
			contactUsername.setVisibility(View.VISIBLE);
			contactUserId.setVisibility(View.VISIBLE);
			contactPhoneNumber.setVisibility(View.VISIBLE);
			contactLocation.setVisibility(View.VISIBLE);
			contactJoined.setVisibility(View.VISIBLE);

			txtFbId.setVisibility(View.VISIBLE);
			txtUserId.setVisibility(View.VISIBLE);
			txtPhoneNumber.setVisibility(View.VISIBLE);
			txtLocation.setVisibility(View.VISIBLE);
			txtUserName.setVisibility(View.VISIBLE);
			txtEventParticipated.setVisibility(View.VISIBLE);
			txtJoined.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		// Prepare data intent 
		Intent data = new Intent();
		if(friendRemoved)
			data.putExtra("REFRESH", "TRUE");
		// Activity finished ok, return the data
		setResult(GeneralManager.constantManager.OK, data);
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
				case ConstantManager.CONTACT_DETAILS_ACTIVITY:

					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								contactName.setText(result.getString("name"));
								contactFbId.setText(result.getString("fbid"));
								contactUsername.setText(result.getString("username"));
								contactUserId.setText(result.getString("id"));
								if(!result.getString("phonenumber").equals("null"))
									contactPhoneNumber.setText(result.getString("phonenumber"));
								contactEventParticipated.setText(result.getString("eventparticipated"));
								try {
									SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date date = stringToDate.parse(result.getString("joined"));

									contactJoined.setText(dateFormat.format(date));
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								profilePhoto.setBackground(getResources().getDrawable(R.drawable.profile_border));
								imageLoader.setImageSize(100);
								imageLoader.DisplayImage(result.getString("profilephoto"), profilePhoto);
								btnRemoveFriend.setEnabled(true);
								btnRemoveFriend.setVisibility(View.VISIBLE);
							}
						}
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break; 
				case ConstantManager.REMOVE_FRIEND:
					showProgress(false);
					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								//refresh needed on finish of this activity
								friendRemoved  =true;
								finish();
							}
							else
								Toast.makeText(getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
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
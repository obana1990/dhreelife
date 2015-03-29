package com.my.dhreelife.activity;



import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;



public class EditPasswordActivity extends Activity{

	private Context context;
	private EditText currentPassword;
	private EditText newPassword1;
	private EditText newPassword2;
	private Button updatePasswordBtn;

	private ProgressBar loadingPb2;
	private TextView loadingTxt2;
	private RequestQueue requestQueue;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_password);
		context = getApplicationContext();
		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);


		//initialize widget
		currentPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword1 = (EditText) findViewById(R.id.newPassword1);
		newPassword2 = (EditText) findViewById(R.id.newPassword2);
		loadingPb2 = (ProgressBar) findViewById(R.id.contactDetailsLoadingPb2);
		loadingTxt2 = (TextView) findViewById(R.id.contactDetailsLoadingTxt2);


		updatePasswordBtn = (Button)findViewById(R.id.updatePassword);
		//updateSecurityAnswerBtn = (Button)findViewById(R.id.updateSecurityAnswer);
		updatePasswordBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				if(currentPassword.getText().toString().equals(""))
				{
					Toast.makeText(context, "Please key in your current password.", Toast.LENGTH_LONG).show();
				}
				else if(newPassword1.getText().toString().length()<9)
				{
					Toast.makeText(context, "Password has to be at least 9 characters.", Toast.LENGTH_LONG).show();
				}
				else
				{
					if(newPassword1.getText().toString().equals(newPassword2.getText().toString()))
					{
						showProgress(true);


						//initialize the request queue object
						requestQueue = new VolleyRequestQueue(context).getRequestQueueInstance();
						
						//create params
						Map<String, String> mParams = new HashMap<String,String>();
						mParams.put("id",GeneralManager.authManager.userId);
						mParams.put("oldPassword",currentPassword.getText().toString());
						mParams.put("newPassword",newPassword1.getText().toString());

						//create error listener
						Response.ErrorListener errorListner = new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
							}
						};

						//craete the listener on call back
						StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPDATE_PASSWORD);
						CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/updatePassword",mParams,listener,errorListner);

						//set tag
						volleyRequest.setTag("Public");

						//add the request into request queue
						requestQueue.add(volleyRequest);

					}
					else
					{
						Toast.makeText(context, "New password does not match", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}


	public void showProgress(boolean show)
	{
		if(show)
		{
			loadingPb2.setVisibility(View.VISIBLE);
			loadingTxt2.setVisibility(View.VISIBLE);
		}
		else
		{
			loadingPb2.setVisibility(View.GONE);
			loadingTxt2.setVisibility(View.GONE);
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
				case ConstantManager.UPDATE_PASSWORD:

					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								Toast.makeText(context, result.getString("message"), Toast.LENGTH_LONG).show();
							}
							else
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

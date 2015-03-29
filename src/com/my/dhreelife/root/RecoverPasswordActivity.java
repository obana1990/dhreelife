package com.my.dhreelife.root;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class RecoverPasswordActivity extends Activity{

	private ProgressBar loadingPb;
	private TextView loadingTxt;
	private Button submitBtn;
	private EditText emailAddress;
	private EditText securityQuestionAnswer;
	private TextView textViewSecurityQuestion;
	private Context context;
	private boolean submitSecurityQuestion = false;
	private Button backButton;
	//Progress view
	private LinearLayout registrationFormLayout;

	private RequestQueue requestQueue;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recover_password);
		ConstantManager.CURRENT_TASK = ConstantManager.RECOVER_PASSWORD_ACTIVITY_ID;
		context = this;
		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();

		registrationFormLayout = (LinearLayout) findViewById(R.id.recoverPasswordFormLayout);
		loadingPb = (ProgressBar) findViewById(R.id.contactLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.contactLoadingTxt); 
		emailAddress = (EditText)findViewById(R.id.emailAddress);
		securityQuestionAnswer = (EditText)findViewById(R.id.securityQuestionAnswer);
		textViewSecurityQuestion = (TextView)findViewById(R.id.securityQuestion);
		submitBtn = (Button) findViewById(R.id.recover_password_submit_button);
		submitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(submitSecurityQuestion)
				{
					showProgress(true);
					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("username", emailAddress.getText().toString());
					mParams.put("securityanswer", securityQuestionAnswer.getText().toString());

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.RECOVER_PASSWORD);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/resetPassword",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("Public");

					//add the request into request queue
					requestQueue.add(volleyRequest);


				}
				else
				{
					showProgress(true);

					//create params
					Map<String, String> mParams = new HashMap<String,String>();
					mParams.put("username", emailAddress.getText().toString());

					//create error listener
					Response.ErrorListener errorListner = new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					};

					//craete the listener on call back
					StringVolleyResponseListener listener = createNewRequest(ConstantManager.GET_SECURITY_QUESTION);
					CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/getSecurityQuestion",mParams,listener,errorListner);

					//set tag
					volleyRequest.setTag("Public");

					//add the request into request queue
					requestQueue.add(volleyRequest);



				}
			}
		});

		backButton = (Button) findViewById(R.id.recoverPasswordBackButton);
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
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}


	private void showProgress(final boolean show) {
		if(show)
		{
			loadingPb.setVisibility(View.VISIBLE);
			loadingTxt.setVisibility(View.VISIBLE);
			registrationFormLayout.setVisibility(View.GONE);
		}
		else
		{
			loadingPb.setVisibility(View.GONE);
			loadingTxt.setVisibility(View.GONE);
			registrationFormLayout.setVisibility(View.VISIBLE);
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
				case ConstantManager.RECOVER_PASSWORD:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) {
							try {
								if(!result.has("error"))
								{
									String[] securityChoice = ConstantManager.securityChoice;

									int index = Integer.parseInt(result.getString("securityquestion"));
									String securityQuestion = securityChoice[index];
									textViewSecurityQuestion.setText("Please answer the follwing question and press the submit button.\n"+securityQuestion);
									textViewSecurityQuestion.setVisibility(View.VISIBLE);
									securityQuestionAnswer.setVisibility(View.VISIBLE);
									emailAddress.setVisibility(View.GONE);
									submitSecurityQuestion = true;
								}
								else
								{
									Toast.makeText(getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}catch (NumberFormatException e) {
								e.printStackTrace();
							} 
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
				case ConstantManager.GET_SECURITY_QUESTION:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) {
							if(result.has("error"))
							{
								Toast.makeText(context, "Your password has been reseted and an email has been sent to your email inbox.\nPlease check your inbox.", Toast.LENGTH_LONG).show();
								finish();
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

package com.my.dhreelife.root;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.my.dhreelife.util.manager.SessionManager;


public class RegisterActivity extends Activity{
	private String userName;
	private String password1;
	@SuppressWarnings("unused")
	private String password2;
	private String name;
	private EditText editTextUserName;
	private EditText editTextPassword1;
	private EditText editTextPassword2;
	private EditText editTextName;
	private EditText editTextPhoneNumber;
	private EditText editTextSecurityQuestion;
	private ImageView validUsername;
	private ImageView validPasswordFormat;
	private ImageView validPassword;
	private ImageView validName;
	private ImageView validPhoneNumber;
	private ImageView validSecurityQuestion;
	private CheckBox agreedTermsAndConditions;

 
	private boolean nameNotNull = false;
	private boolean passwordMatchedAndNotNull = false;
	private boolean userNameNotExistAndNotNull = false;
	private boolean correctPasswordFormat = false;
	private boolean correctPhoneNumber = false;
	private boolean securityQuestionNotNull = false;
	private Spinner securityQuestionChoice;
	private Context context;
	private Button backButton;
	//Progress view
	private ProgressDialog progressDialog;
	private RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(this, R.raw.click, 1); 
		ConstantManager.CURRENT_TASK = ConstantManager.REGISTER_ACTIVITY_ID;
		context = this;
		@SuppressWarnings("static-access")
		TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(getBaseContext().TELEPHONY_SERVICE);
		String phoneNumber = tMgr.getLine1Number();

		progressDialog = new ProgressDialog(context);
		Button submitButton = (Button) findViewById(R.id.register_submit_button);
		editTextUserName = (EditText) findViewById(R.id.register_user_name);
		editTextPassword1 = (EditText) findViewById(R.id.register_password1);
		editTextPassword2 = (EditText) findViewById(R.id.register_password2);
		editTextName = (EditText) findViewById(R.id.register_name);
		editTextSecurityQuestion = (EditText)findViewById(R.id.securityQuestion);

		validSecurityQuestion = (ImageView)findViewById(R.id.validSecurityQuestion);
		validPhoneNumber = (ImageView)findViewById(R.id.validPhoneNumber);
		validPasswordFormat = (ImageView)findViewById(R.id.validPasswordFormat);
		validUsername = (ImageView) findViewById(R.id.validUserName);
		validPassword = (ImageView) findViewById(R.id.validPassword);
		validName = (ImageView) findViewById(R.id.validName);
		editTextPhoneNumber = (EditText)findViewById(R.id.phoneNumber);
		editTextPhoneNumber.setText(phoneNumber);
		securityQuestionChoice = (Spinner)findViewById(R.id.securityQuestionChoice);
		editTextPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(!editTextPhoneNumber.getText().toString().equals(""))
					{
						validPhoneNumber.setImageResource(R.drawable.tick);
						correctPhoneNumber = true;
					}
					else
					{
						validPhoneNumber.setImageResource(R.drawable.cross);
						correctPhoneNumber = false;
					}
					validPhoneNumber.setVisibility(View.VISIBLE);
				}
			}
		});
		editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(!editTextUserName.getText().toString().equals(""))
					{
						String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
						CharSequence inputStr = editTextUserName.getText().toString();

						Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(inputStr);
						if (!matcher.matches()) {
							Toast.makeText(context, "Please use a valid email address.", Toast.LENGTH_LONG).show();
							validUsername.setImageResource(R.drawable.cross);
							userNameNotExistAndNotNull = false;
							validUsername.setVisibility(View.VISIBLE);
						}
					}
					else
					{
						validUsername.setImageResource(R.drawable.cross);
						userNameNotExistAndNotNull = false;
						validUsername.setVisibility(View.VISIBLE);
					}

				}

			}
		});

		editTextUserName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int j, int i2, int i3) {
				if(!editTextUserName.getText().toString().equals(""))
				{
					String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
					CharSequence inputStr = editTextUserName.getText().toString();

					Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(inputStr);
					if (matcher.matches()) {
						validUsername.setImageResource(R.drawable.tick);
						userNameNotExistAndNotNull = true;
					}
					else
					{
						//Toast.makeText(context, "Please use a valid email address.", Toast.LENGTH_LONG).show();
						validUsername.setImageResource(R.drawable.cross);
						userNameNotExistAndNotNull = false;
						validUsername.setVisibility(View.VISIBLE);
					}
				}
				else
				{
					validUsername.setImageResource(R.drawable.cross);
					userNameNotExistAndNotNull = false;
					validUsername.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		validPasswordFormat.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(editTextPassword1.getText().toString().length()>ConstantManager.MIN_PASSWORD_LENGTH)
					{
						correctPasswordFormat = true;
						validPasswordFormat.setImageResource(R.drawable.tick);
					}	
					else
						validPasswordFormat.setImageResource(R.drawable.cross);
					validPasswordFormat.setVisibility(View.VISIBLE);
				}

			}
		});
		editTextPassword1.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(editTextPassword1.getText().toString().equals(""))
					{
						if(!hasFocus)
							Toast.makeText(context, "Password cannot be blank.", Toast.LENGTH_LONG).show();
						validPasswordFormat.setImageResource(R.drawable.cross);
						validPasswordFormat.setVisibility(View.VISIBLE);
						correctPasswordFormat = false;
					}
					else
					{
						if(editTextPassword1.getText().toString().length()<ConstantManager.MIN_PASSWORD_LENGTH)
						{

							Toast.makeText(context, "Please choose a password that at least 9 characters long.", Toast.LENGTH_LONG).show();
							validPasswordFormat.setImageResource(R.drawable.cross);
							correctPasswordFormat = false;

						}
						else
						{
							correctPasswordFormat = true;
							validPasswordFormat.setImageResource(R.drawable.tick);
						}
						validPasswordFormat.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		editTextPassword2.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(editTextPassword2.getText().toString().equals(""))
					{
						if(!hasFocus)
							Toast.makeText(context, "Password cannot be blank.", Toast.LENGTH_LONG).show();
						validPassword.setImageResource(R.drawable.cross);
						validPassword.setVisibility(View.VISIBLE);
						passwordMatchedAndNotNull = false;
					}
					else
					{
						if(!editTextPassword1.getText().toString().equals(editTextPassword2.getText().toString()))
						{
							if(!hasFocus)
								Toast.makeText(context, "Password does not match.", Toast.LENGTH_LONG).show();
							validPassword.setImageResource(R.drawable.cross);
							passwordMatchedAndNotNull = false;
						}
						else
						{

							validPassword.setImageResource(R.drawable.tick);
							passwordMatchedAndNotNull = true;
						}
						validPassword.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		editTextPassword2.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int j, int i2, int i3) {
				if(editTextPassword2.getText().toString().equals(""))
				{
					validPassword.setImageResource(R.drawable.cross);
					validPassword.setVisibility(View.VISIBLE);
					passwordMatchedAndNotNull = false;
				}
				else
				{
					if(!editTextPassword1.getText().toString().equals(editTextPassword2.getText().toString()))
					{
						validPassword.setImageResource(R.drawable.cross);
						passwordMatchedAndNotNull = false;
					}
					else
					{

						validPassword.setImageResource(R.drawable.tick);
						passwordMatchedAndNotNull = true;
					}
					validPassword.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(!editTextName.getText().toString().equals(""))
					{
						nameNotNull = true;
						validName.setImageResource(R.drawable.tick);
					}
					else
					{
						nameNotNull = false;
						Toast.makeText(context, "Please key in your name.", Toast.LENGTH_LONG).show();
						validName.setImageResource(R.drawable.cross);
					}
					validName.setVisibility(View.VISIBLE);
				}


			}
		});
		editTextSecurityQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(!editTextSecurityQuestion.getText().toString().equals(""))
					{
						securityQuestionNotNull = true;
						validSecurityQuestion.setImageResource(R.drawable.tick);
					}
					else
					{
						securityQuestionNotNull = false;
						Toast.makeText(context, "Please key in the security question's answer.", Toast.LENGTH_LONG).show();
						validSecurityQuestion.setImageResource(R.drawable.cross);
					}
					validSecurityQuestion.setVisibility(View.VISIBLE);
				}
			}
		});

		editTextSecurityQuestion.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int j, int i2, int i3) {
				if(!editTextSecurityQuestion.getText().toString().equals(""))
				{
					securityQuestionNotNull = true;
					validSecurityQuestion.setImageResource(R.drawable.tick);
				}
				else
				{
					securityQuestionNotNull = false;
					Toast.makeText(context, "Please key in the security question's answer.", Toast.LENGTH_LONG).show();
					validSecurityQuestion.setImageResource(R.drawable.cross);
				}
				validSecurityQuestion.setVisibility(View.VISIBLE);
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		agreedTermsAndConditions = (CheckBox)findViewById(R.id.termsAndConditionCheckBox);
		if (submitButton != null) {
			submitButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
					name = ((EditText) findViewById(R.id.register_name)).getText().toString();
					userName = ((EditText) findViewById(R.id.register_user_name)).getText().toString();
					password1 = ((EditText) findViewById(R.id.register_password1)).getText().toString();
					password2 = ((EditText) findViewById(R.id.register_password2)).getText().toString();


					if (passwordMatchedAndNotNull&&nameNotNull&&userNameNotExistAndNotNull&&correctPasswordFormat&&correctPhoneNumber&&securityQuestionNotNull) 
					{
						if(agreedTermsAndConditions.isChecked())
						{
							showProgress(true);
							
							//initialize the request queue object
							requestQueue = new VolleyRequestQueue(context).getRequestQueueInstance();
							
							//create params
							Map<String, String> mParams = new HashMap<String,String>();
							mParams.put("User[username]", userName);  
							mParams.put("User[password]", password1);  
							mParams.put("User[name]", name); 
							mParams.put("User[phonenumber]", editTextPhoneNumber.getText().toString()); 
							mParams.put("User[mobiletype]", ConstantManager.ANDROID_MOBILE_TYPE); 
							mParams.put("User[securityquestion]", String.valueOf(securityQuestionChoice.getSelectedItemPosition())); 
							mParams.put("User[securityanswer]", editTextSecurityQuestion.getText().toString()); 

							//create error listener
							Response.ErrorListener errorListner = new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
								}
							};

							//craete the listener on call back
							StringVolleyResponseListener listener = createNewRequest(ConstantManager.REGISTER);
							CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/add",mParams,listener,errorListner);

							//set tag
							volleyRequest.setTag("Public");

							//add the request into request queue
							requestQueue.add(volleyRequest);

						} else {
							Toast.makeText(getBaseContext(), "Please check the terms and conditions.", Toast.LENGTH_LONG).show();
						}

					}
					else
					{
						Toast.makeText(getBaseContext(), "Please fill in the required information correctly.", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
		backButton = (Button) findViewById(R.id.registerBackButton);
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
			progressDialog = ProgressDialog.show(context, "", "Submitting registration...", true);
		}
		else
		{
			progressDialog.dismiss();
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
				case ConstantManager.REGISTER:

					try {
						JSONObject result = new JSONObject(response);
						if(result!=null)
						{
							if(!result.has("error"))
							{
								GeneralManager.sessionManager = new SessionManager(context);
								Toast.makeText(getApplicationContext(), "Registration succeed", Toast.LENGTH_LONG).show();
								GeneralManager.sessionManager.createLoginSession(result.getString("name"), result.getString("username"), result.getString("id"),result.getString("profilephoto"));
								Intent intent  = new Intent(context, WelcomeActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.putExtra("user_id", result.getString("id"));
								//flag will not work on jelly bean, use finish affinity instead
								intent.putExtra("user_name", result.getString("username"));
								ActivityCompat.finishAffinity((Activity) context);
								startActivity(intent);
							}
							else
							{
								Toast.makeText(getApplicationContext(), result.getString("error"), Toast.LENGTH_LONG).show();
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

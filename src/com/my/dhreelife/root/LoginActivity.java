package com.my.dhreelife.root;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;
import com.my.dhreelife.util.manager.SessionManager;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends FragmentActivity{

	//facebook
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final List<String> PERMISSIONS = Arrays.asList("user_likes","public_profile","email", "user_status","user_friends");
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int FRAGMENT_COUNT = SELECTION +1;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private boolean isResumed = false;

	private List<NameValuePair> nameValuePairs;  
	private Context context;
	// UI references.
	private EditText username;
	private EditText password;
	private TextView mLoginStatusMessageView;

	private ProgressDialog progressDialog;
	private TextView hyperlinkForgetPassword;
	private boolean fbAttemptedLogin = false;
	private String fbId = "";
	private boolean loggedIn = false;
	private RequestQueue requestQueue;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		//initiate sound effect

		//initialize the request queue object
		requestQueue = new VolleyRequestQueue(this).getRequestQueueInstance();

		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(this, R.raw.click, 1); 
		context = this;

 

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		//facebook login button
		LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("user_likes","public_profile","email", "user_status","user_friends"));

		//set the current task id in constant manager
		ConstantManager.CURRENT_TASK = ConstantManager.LOGIN_ACTIVITY_ID;
		//hide the keyboard if user did not click on the text box
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//initiate session manager
		GeneralManager.sessionManager =  new SessionManager(this);
		//check if it's logged in
		loggedIn = GeneralManager.sessionManager.checkLogin();

		// Set up the login form.
		username = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		progressDialog = new ProgressDialog(context);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//try login
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				attemptLogin();
			}
		});
		findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//start Register activity
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});

		hyperlinkForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);
		makeTextViewHyperlink(hyperlinkForgetPassword);
		hyperlinkForgetPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RecoverPasswordActivity.class);
				startActivity(intent);
			}
		}); 
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		username.setError(null);
		password.setError(null);

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = username.getText().toString();

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (!matcher.matches()) {
			username.setError("Please key a valid email address.");
			return;
		}

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(password.getText().toString())) {
			password.setError(getString(R.string.error_field_required));
			focusView = password;
			cancel = true;

			//minimum length for a password
		} else if (password.getText().toString().length() < 4) {
			password.setError(getString(R.string.error_invalid_password));
			focusView = password;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("User[username]", username.getText().toString());
			mParams.put("User[password]", password.getText().toString());

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.LOGIN);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/login",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Login");

			//add the request into request queue
			requestQueue.add(volleyRequest);

		}
	}




	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if(show)
		{
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Logging in...");
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if(requestQueue!=null)
					{
						requestQueue.cancelAll("Login");
					}
				}
			});
			progressDialog.show();
		}
		else
			progressDialog.dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;
		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (state.isOpened()&&!loggedIn) {
			if(!fbAttemptedLogin)
			{
				fbAttemptedLogin = true;
				Request request = Request.newMeRequest(session, 
						new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user,
							com.facebook.Response response) {
						if (user != null) {
							fbId = user.getId();
							String email = user.getProperty("email").toString();
							String name = user.getFirstName()+" "+user.getLastName();
							if (session == Session.getActiveSession()) {
								if(session.getPermissions().equals(PERMISSIONS))
								{
									requestPublishPermissions(session);
								}
								// Set the id for the ProfilePictureView
								// view that in turn displays the profile picture.


								showProgress(true);
								getUserFriendListAndRegister(session,email,name,fbId);
							}

						}
						if (response.getError() != null) {
							Toast.makeText(context, response.getError().toString(), Toast.LENGTH_LONG).show();
						}

					}
				});
				request.executeAsync();
			}

		} else if (state.isClosed()) {

		}
	}

	private void fbLoginToDhreeLifeServer(final Session session,String facebookId) {
		showProgress(true);

		//create params
		Map<String, String> mParams = new HashMap<String,String>();
		mParams.put("fbid", facebookId);

		//create error listener
		Response.ErrorListener errorListner = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};

		//craete the listener on call back
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.FB_LOGIN);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/fbLogin",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Login");

		//add the request into request queue
		requestQueue.add(volleyRequest);

	}


	private void getUserFriendListAndRegister(final Session session,final String email,final String name,final String facebookId)
	{
		Request request = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {


			@Override
			public void onCompleted(List<GraphUser> users,
					com.facebook.Response response) {

				//create params
				Map<String, String> mParams = new HashMap<String,String>();
				mParams.put("User[username]", email);
				mParams.put("User[name]", name);
				mParams.put("User[fbid]", facebookId);

				for(int i=0;i<users.size();i++)
				{
					GraphUser temp = users.get(i);
					mParams.put("User[fbFriendsId]["+String.valueOf(i)+"]",temp.getId());
				}

				//create error listener
				Response.ErrorListener errorListner = new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
					}
				};

				//craete the listener on call back
				StringVolleyResponseListener listener = createNewRequest(ConstantManager.FB_REGISTER);
				CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/add",mParams,listener,errorListner);

				//set tag
				volleyRequest.setTag("Login");

				//add the request into request queue
				requestQueue.add(volleyRequest);

			}
		});
		request.executeAsync();
	}

	private void requestPublishPermissions(Session session) {
		if (session != null) {
			Session.NewPermissionsRequest newPermissionsRequest = 
					new Session.NewPermissionsRequest(this, PERMISSIONS).
					setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
		else
		{
			Toast.makeText(context, "session null.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Sets a hyperlink style to the textview.
	 */
	public static void makeTextViewHyperlink(TextView tv) {
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(tv.getText());
		ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(ssb, TextView.BufferType.SPANNABLE);
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
				case ConstantManager.FB_REGISTER:

					try {
						JSONObject result = new JSONObject(response);
						if(!result.has("error"))
						{
							showProgress(false);

							GeneralManager.sessionManager.createLoginSession(result.getString("name"), result.getString("username"), result.getString("id"),result.getString("profilephoto"));
							Intent intent  = new Intent(context, WelcomeActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
							GeneralManager.authManager.userId = result.getString("id");
							startActivity(intent);

						}
						else
						{
							try {
								// share preferences deleted, but user already registered. 
								// do normal fb login
								if(result.getString("error").equals("Username exist, please use another one."))
								{
									fbLoginToDhreeLifeServer(Session.getActiveSession(),fbId);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
				case ConstantManager.FB_LOGIN:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) {

							if(!result.has("error"))
							{
								Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();

								GeneralManager.sessionManager.createLoginSession(result.getString("name"),  result.getString("username"), result.getString("id"),result.getString("profilephoto"));
								Intent intent  = new Intent(context, WelcomeActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
								GeneralManager.authManager.userId = result.getString("id");
								GeneralManager.authManager.fbId = result.getString("fbid");
								GeneralManager.authManager.profileURL = result.getString("profilephoto");
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
				case ConstantManager.LOGIN:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) {

							if(!result.has("error"))
							{
								Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();
								GeneralManager.sessionManager.createLoginSession(result.getString("name"), result.getString("username"), result.getString("id"),result.getString("profilephoto"));
								Intent intent  = new Intent(context, WelcomeActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
								GeneralManager.authManager.userId = result.getString("id");
								GeneralManager.authManager.profileURL = result.getString("profilephoto");
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

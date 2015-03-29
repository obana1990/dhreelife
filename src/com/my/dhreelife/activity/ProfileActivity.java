package com.my.dhreelife.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.ImageLoader;
import com.my.dhreelife.util.ImageUploadRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;


public class ProfileActivity extends Activity{
	private  TextView contactName;
	private  TextView contactFbId;
	private  TextView contactUsername;
	private  TextView contactUserId;
	private  TextView contactPhoneNumber;
	private  TextView contactLocation;
	private  ProgressBar loadingPb;
	private  TextView loadingTxt;
	private  Button changeProfilePhoto;
	private  ImageView profilePhoto;
	private  TextView txtJoined;
	private  TextView txtFbId;
	private  TextView txtUserId;
	private  TextView txtPhoneNumber;
	private  TextView txtLocation;
	private  TextView txtUserName;
	private  TextView txtEventParticipated;
	private TextView contactEventParticipated;
	private TextView contactJoined;
	private Button backButton;
	private LinearLayout lytContactInfoContainer;
	private ProgressDialog progressDialog;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private RequestQueue requestQueue;
	private Context context;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile);
		context = this;



		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);

		loadingPb = (ProgressBar) findViewById(R.id.contactDetailsLoadingPb);
		loadingTxt = (TextView) findViewById(R.id.contactDetailsLoadingTxt);

		contactName = (TextView) findViewById(R.id.contact_name);
		contactFbId = (TextView) findViewById(R.id.contact_fb_id);
		contactUsername = (TextView) findViewById(R.id.contact_user_name);
		contactUserId = (TextView) findViewById(R.id.contact_user_id);
		contactEventParticipated = (TextView) findViewById(R.id.contactEventParticipated);
		contactJoined =  (TextView) findViewById(R.id.contactJoined);
		contactPhoneNumber = (TextView) findViewById(R.id.contactPhoneNumber);
		contactLocation = (TextView) findViewById(R.id.contactLocation);

		txtJoined = (TextView) findViewById(R.id.txtJoined);
		txtFbId = (TextView) findViewById(R.id.txtFbID);
		txtUserId = (TextView) findViewById(R.id.txtUserID);
		txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
		txtLocation = (TextView) findViewById(R.id.txtLocation);
		txtUserName = (TextView) findViewById(R.id.txtUserName);
		txtEventParticipated = (TextView) findViewById(R.id.txtEventParticipated);

		lytContactInfoContainer = (LinearLayout) findViewById(R.id.lytContactInfoContainer);

		profilePhoto = (ImageView) findViewById(R.id.imgProfilePhoto);
		profilePhoto.setScaleType(ScaleType.FIT_XY);
		changeProfilePhoto = (Button) findViewById(R.id.btnChangeProfilePhoto);
		changeProfilePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
			}
		});

		backButton = (Button) findViewById(R.id.profileBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});

		showProgress(true);
		//get profile details
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
		StringVolleyResponseListener listener = createNewRequest(ConstantManager.CONTACT_DETAILS_ACTIVITY);
		CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"users/view",mParams,listener,errorListner);

		//set tag
		volleyRequest.setTag("Contact");

		//add the request into request queue
		requestQueue.add(volleyRequest);
	}




	public void showProgress(boolean show)
	{
		if(show)
		{
			loadingPb.setVisibility(View.VISIBLE);
			loadingTxt.setVisibility(View.VISIBLE);

			profilePhoto.setVisibility(View.GONE);
			lytContactInfoContainer.setVisibility(View.GONE);
			contactEventParticipated.setVisibility(View.GONE);
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
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if(resultCode==ConstantManager.RESULT_OK)
		{
			if(data!=null)
			{
				progressDialog = ProgressDialog.show(this, "", "Uploading photo...", true);
				Thread thread = new Thread()
				{
					@Override
					public void run() {
						Uri selectedImageUri = data.getData();

						String tmpProfilePath;
						try {
							tmpProfilePath = getAndStoreFileFromUri(selectedImageUri);


							//create error listener
							Response.ErrorListener errorListner = new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									progressDialog.dismiss();
									Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
								}
							};

							//craete the listener on call back
							StringVolleyResponseListener listener = createNewRequest(ConstantManager.UPLOAD_PHOTO_ACTIVITY_ID);
							File imageFile = new File(getApplicationContext().getFilesDir(),"tmp.jpg");
							ImageUploadRequest imageUploadRequest = new ImageUploadRequest(GeneralManager.constantManager.url+"/Uploads/upload",errorListner,listener,imageFile,context);


							//set tag
							imageUploadRequest.setTag("Profile");
							imageUploadRequest.setRetryPolicy(new DefaultRetryPolicy(
									50000, 
									DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
									DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); 

							//add the request into request queue
							requestQueue.add(imageUploadRequest);
						} catch (IOException e) {
							progressDialog.dismiss();
							Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				};

				thread.start();

			}
			else
			{
				Toast.makeText(this, "Ops, something went wrong. Please try again later.",Toast.LENGTH_LONG).show();
			}
		}
	}


	private String getAndStoreFileFromUri(Uri uri) throws IOException {
		ParcelFileDescriptor parcelFileDescriptor =
				getContentResolver().openFileDescriptor(uri, "r");
		FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

		InputStream fileStream = new FileInputStream(fileDescriptor);
		File imageFile = new File(getApplicationContext().getFilesDir(),"tmp.jpg");
		OutputStream tmpProfilePhoto = new FileOutputStream(imageFile);

		byte[] buffer = new byte[1024];
		int length;

		while((length = fileStream.read(buffer)) > 0)
		{
			tmpProfilePhoto.write(buffer, 0, length);
		}

		//reduced the size of image before upload
		Bitmap reducedSizeImage = decodeImage(getApplicationContext().getFilesDir()+"/tmp.jpg");

		//fix the orientation
		reducedSizeImage = decodeImage(getApplicationContext().getFilesDir()+"/tmp.jpg");
		ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
		int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		int rotationInDegrees = exifToDegrees(rotation);
		Matrix matrix = new Matrix();
		if (rotation != 0f) {

			matrix.preRotate(rotationInDegrees);
			reducedSizeImage = Bitmap.createBitmap(reducedSizeImage, 0, 0, reducedSizeImage.getWidth(), reducedSizeImage.getHeight(), matrix, true);
		}

		//Convert bitmap to byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		reducedSizeImage.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		FileOutputStream fos = new FileOutputStream(imageFile);
		fos.write(bitmapdata);
		fileStream.close();
		fos.close();
		tmpProfilePhoto.close();
		return imageFile.getAbsolutePath();
	}

	public Bitmap decodeImage(String filePath) 
	{ 
		// Decode image size 
		BitmapFactory.Options o = new BitmapFactory.Options(); 
		o.inJustDecodeBounds = true; 
		BitmapFactory.decodeFile(filePath, o); 

		// The new size we want to scale to 
		final int REQUIRED_SIZE = 1024; 

		// Find the correct scale value. It should be the power of 2. 
		int width_tmp = o.outWidth, height_tmp = o.outHeight; 
		int scale = 1; 
		while (true) 
		{ 
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) 
				break; 
			width_tmp /= 2; 
			height_tmp /= 2; 
			scale *= 2; 
		} 

		// Decode with inSampleSize 
		BitmapFactory.Options o2 = new BitmapFactory.Options(); 
		o2.inSampleSize = scale; 
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2); 
		return bitmap;

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
				if(progressDialog!=null)
					progressDialog.dismiss();
				switch(taskId)
				{
				case ConstantManager.CONTACT_DETAILS_ACTIVITY:

					try {
						JSONObject result = new JSONObject(response);
						if (result != null) 
						{
							if(!result.has("error"))
							{
								contactName.setText(result.getString("name"));
								contactName.setTextColor(Color.BLACK);
								if(result.getString("fbid")!=null&&!result.getString("fbid").equals("null"))
								{
									contactFbId.setText(result.getString("fbid"));
									contactFbId.setTextColor(Color.BLACK);
								}

								contactUsername.setText(result.getString("username"));
								contactUsername.setTextColor(Color.BLACK);
								contactUserId.setText(result.getString("id"));
								contactUserId.setTextColor(Color.BLACK);
								if(result.getString("phonenumber")!=null&&!result.getString("phonenumber").equals("null"))
								{
									contactPhoneNumber.setText(result.getString("phonenumber"));
									contactPhoneNumber.setTextColor(Color.BLACK);
								}
								contactEventParticipated.setText(result.getString("eventparticipated"));
								try {
									SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date date = stringToDate.parse(result.getString("joined"));

									contactJoined.setText(dateFormat.format(date));
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
								//display profile photo

								profilePhoto.setBackground(getResources().getDrawable(R.drawable.profile_border));
								ImageLoader imageLoader = new ImageLoader(context);
								imageLoader.setImageSize(100);
								imageLoader.DisplayImage(result.getString("profilephoto"), profilePhoto);

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

				case ConstantManager.UPLOAD_PHOTO_ACTIVITY_ID:
					JSONObject result;
					try {
						result = new JSONObject(response);

						if (result != null) 
						{
							if(!result.has("error"))
							{
								Toast.makeText(context, result.getString("message"), Toast.LENGTH_LONG).show();

								showProgress(true);
								Bitmap bitmap = decodeImage(getApplicationContext().getFilesDir()+"/tmp.jpg");
								profilePhoto.setImageBitmap(bitmap);
								showProgress(false);
							}
							else
							{
								Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		};
		stringVolleyResponseListener.setTaskId(taskId); 
		return stringVolleyResponseListener;
	}

	private static int exifToDegrees(int exifOrientation) {        
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
		return 0;    
	} 
}

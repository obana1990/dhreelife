package com.my.dhreelife.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.dhreelife.R;
import com.my.dhreelife.util.ImageLoader;
import com.my.dhreelife.util.manager.GeneralManager;

public class SponsorDetailsActivity extends Activity {
	private Button backButton;
	private Button createSponsorEventButton;
	private String title;
	private String description;
	private String location;
	private String maxAttendees;
	private String time;
	private String flierUrl;

	private TextView txtSponsorTitle;
	private TextView txtSponsorDescription;
	private TextView txtSponsorLocation;
	private TextView txtSponsorMaxAttendees; 
	private TextView txtSponsorTime;
	private ImageView imgFlier;

	private ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sponsor_details);

		//set the layout size
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);



		imageLoader = new ImageLoader(this);

		title = getIntent().getStringExtra("title");
		description = getIntent().getStringExtra("description");
		location = getIntent().getStringExtra("location");
		maxAttendees = getIntent().getStringExtra("maxattendees");
		flierUrl = getIntent().getStringExtra("flierurl");
		time = getIntent().getStringExtra("time");

		imgFlier = (ImageView) findViewById(R.id.imgSponsorFlier);
		txtSponsorTitle = (TextView) findViewById(R.id.txtSponsorTitle);
		txtSponsorDescription = (TextView) findViewById(R.id.txtSponsorDescription);
		txtSponsorLocation = (TextView) findViewById(R.id.txtSponsorLocation);
		txtSponsorMaxAttendees = (TextView) findViewById(R.id.txtSponsorMaxAttendees);
		txtSponsorTime = (TextView) findViewById(R.id.txtSponsorTime);

		txtSponsorTitle.setText(title);
		txtSponsorDescription.setText(description);
		txtSponsorLocation.setText("Location : "+location);
		txtSponsorMaxAttendees.setText("Max Attendees : "+maxAttendees);
		txtSponsorTime.setText("Start at : "+time);


		backButton = (Button) findViewById(R.id.sponsorEventBackButton);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});

		createSponsorEventButton = (Button) findViewById(R.id.btnCreateSponsorEvent);
		createSponsorEventButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
				Intent myIntent = new Intent(SponsorDetailsActivity.this, AddNewEventActivity.class);
				//pass user id to next activity
				myIntent.putExtra("sponsorEvent", "1");
				myIntent.putExtra("eventName", title);
				myIntent.putExtra("eventDescription", description);
				myIntent.putExtra("eventLocation", location);
				myIntent.putExtra("eventMaxAttendees", maxAttendees);

				startActivityForResult(myIntent,GeneralManager.constantManager.CREATE_EVENT_ACTIVITY);

			}
		});

		imageLoader.DisplayImage(flierUrl, imgFlier);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sponsor_details, menu);
		return true;
	}

}

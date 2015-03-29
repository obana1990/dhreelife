package com.my.dhreelife.root;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.my.dhreelife.R;
import com.my.dhreelife.fragment.EventConfirmationActivity;
import com.my.dhreelife.fragment.EventExtraInformationActivity;
import com.my.dhreelife.fragment.MapActivity;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.FriendManager;
import com.my.dhreelife.util.manager.GeneralManager;

public class PendingEventDetailsActivity extends Activity {
    private final int OK = 1;
    public static boolean refreshListOnFinish = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_pending_event_details);
        
        //initiate sound effect
        GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(this, R.raw.click, 1); 
        
        ConstantManager.CURRENT_TASK = ConstantManager.PENDING_EVENT_DETAILS_ACTIVITY_ID;
        
        // read friend from shared preferences
        ConstantManager.friendManager = new FriendManager(this);
        ConstantManager.friendManager.readFriendFromSharedPreferences();
        

        Bundle bundle = new Bundle();
        bundle.putString("eventId",getIntent().getStringExtra("eventId"));

        ActionBar.Tab tab;
        ActionBar actionBar = getActionBar();

        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(false);

        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(false);

        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create Event Tab
        tab = actionBar.newTab().setTabListener(new EventExtraInformationActivity());
        // Create your own custom icon
        tab.setIcon(R.drawable.extra_info_tab);
        //tab.setText("Extra Info");
        actionBar.addTab(tab);

        // Create Event Tab
        tab = actionBar.newTab().setTabListener(new EventConfirmationActivity());
        // Create your own custom icon
        tab.setIcon(R.drawable.event_tab);
        //tab.setText("Event");
        actionBar.addTab(tab);

        // Create Map Tab
        tab = actionBar.newTab().setTabListener(new MapActivity());
        // Create your own custom icon
        tab.setIcon(R.drawable.map_tab);
        //tab.setText("Map");
        actionBar.addTab(tab);
        actionBar.setSelectedNavigationItem(1);
    }
    @Override
	public void finish() {
	  // Prepare data intent 
	  Intent data = new Intent();
	  if(refreshListOnFinish)
		  data.putExtra("REFRESH", "TRUE");
	  // Activity finished ok, return the data
	  setResult(OK, data);
	  super.finish();
	}
    
}

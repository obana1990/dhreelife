package com.my.dhreelife.fragment;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.my.dhreelife.R;
import com.my.dhreelife.util.CustomVolleyRequest;
import com.my.dhreelife.util.StringVolleyResponseListener;
import com.my.dhreelife.util.VolleyRequestQueue;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;


public class MapActivity extends Fragment implements ActionBar.TabListener{
	private Fragment mFragment;
	private GoogleMap map;
	private LatLng currLatLng = null;
	private View view;
	private Button getDirection ;
	private String eventId;
	private String destination;
	private Location currentLocation;
	private RequestQueue requestQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ConstantManager.CURRENT_TASK = ConstantManager.MAP_ACTIVITY_ID;
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.activity_map, container, false);
			MapsInitializer.initialize(view.getContext());
		} catch (InflateException exp) {
			/* map is already there, just return view as it is */
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		eventId = getActivity().getIntent().getStringExtra("eventId");

		//initialize the request queue object
		if(getActivity()!=null)
		{
			requestQueue = new VolleyRequestQueue(getActivity().getBaseContext()).getRequestQueueInstance();
			//create params
			Map<String, String> mParams = new HashMap<String,String>();
			mParams.put("id",eventId);

			//create error listener
			Response.ErrorListener errorListner = new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if(getActivity()!=null)
						Toast.makeText(getActivity().getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				}
			};

			//craete the listener on call back
			StringVolleyResponseListener listener = createNewRequest(ConstantManager.VIEW_EVENT);
			CustomVolleyRequest volleyRequest = new CustomVolleyRequest(Method.POST,GeneralManager.constantManager.url+"events/view",mParams,listener,errorListner);

			//set tag
			volleyRequest.setTag("Map");

			//add the request into request queue
			requestQueue.add(volleyRequest);
		}

		

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		setGetDirectionButtonListener();
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				currentLocation = location;
				displayCurrentLocation(location);

			}

			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {

				if(getActivity()!=null)
				{
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which){
							case DialogInterface.BUTTON_POSITIVE:
								Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								getActivity().startActivity(intent);
								dialog.dismiss();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								dialog.dismiss();
								break;
							}
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("Location is not enabled, do you want to enable it?").setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
				}
			}
		};

		// Register the listener with the Location Manager to receive location updates
		if(locationManager!=null)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);




		return view;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mFragment = new MapActivity();
		// Attach fragment1.xml layout
		fragmentTransaction.replace(android.R.id.content, mFragment);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

		Fragment map2 = mFragment.getFragmentManager().findFragmentById(R.id.map);
		mFragment.getFragmentManager().beginTransaction().remove(map2).commit();
		fragmentTransaction.remove(mFragment);
		//if request queue is not null then cancel all
		if(requestQueue!=null)
			requestQueue.cancelAll("Map");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}


	public void displayCurrentLocation(Location location)
	{
		final double lat =location.getLatitude();
		final double lng = location.getLongitude();
		currLatLng = new LatLng(lat,lng);

		MarkerOptions markerOption = new MarkerOptions();
		markerOption.position(currLatLng);
		markerOption.snippet("Your current location");
		markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.android_location));


		map.addMarker(markerOption);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 15));
	}

	public void setGetDirectionButtonListener()
	{
		getDirection = (Button) view.findViewById(R.id.map_get_direction);
		if(getDirection!=null)
		{
			getDirection.setVisibility(View.GONE);
			getDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(currentLocation!=null)
					{
						GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
						String uri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude()+","+currentLocation.getLongitude()+"&daddr="+destination;

						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
						intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
						startActivity(intent);
					}
				}
			});
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
					switch(taskId)
					{
					case ConstantManager.VIEW_EVENT:

						try {
							JSONObject result = new JSONObject(response);
							if (result != null) 
							{
								if(!result.has("error"))
								{
									destination = result.getString("location");
									getDirection.setVisibility(View.VISIBLE);
								}
								else
								{
									Toast.makeText(getActivity().getApplicationContext(),result.getInt("error") , Toast.LENGTH_LONG).show();
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

package com.my.dhreelife.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my.dhreelife.R;
import com.my.dhreelife.activity.ContactDetailsActivity;
import com.my.dhreelife.activity.GroupDetailsActivity;
import com.my.dhreelife.fragment.ContactActivity;
import com.my.dhreelife.util.manager.GeneralManager;
 

/**
 * Created by Obana on 7/19/13.
 */
public class ContactArrayListAdapter extends ArrayAdapter<String> {
	private List<String[]> listOfContact;
	private Context context;
	@SuppressLint("UseSparseArrays")
	private Map<Integer,String> headerPosition = new HashMap<Integer,String>();
	private int contactOnlyLength;
	private Fragment fragment = null;
	private ImageLoader imageLoader; 

	public ContactArrayListAdapter(Context context,Fragment fragment,List<String[]> listOfContact,List<String> listOfContactNameOnly ,Map<Integer,String> headerPosition,int contactOnlyLength) {
		super(context, R.layout.contact_list_view, listOfContactNameOnly);
		this.context = context;
		this.listOfContact = listOfContact;
		this.headerPosition = headerPosition;
		this.contactOnlyLength = contactOnlyLength;
		this.fragment = fragment;
		imageLoader=new ImageLoader(context);
		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(context, R.raw.click, 1); 
	}

	@Override
	public View getView(final int position, final View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = null;

		if(headerPosition.get(position)!=null)
		{
			rowView = inflater.inflate(R.layout.contact_top_section_list_view, parent, false);
			TextView sectionHeaderLabel = (TextView) rowView.findViewById(R.id.contact_section_header);
			if(sectionHeaderLabel!=null)
			{
				sectionHeaderLabel.setText(headerPosition.get(position));
			}
		}
		else
		{
			rowView = inflater.inflate(R.layout.contact_list_view, parent, false);
		}
		if(headerPosition.get(position+1)==null)
		{
			View divider= (View) rowView.findViewById(R.id.customdivider);
			if(divider!=null)
			{
				divider.setBackgroundColor(Color.GRAY);
			}
		}

		//only set on click if it's not the header
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		//don't display the "g" in front of group name
		if(position<contactOnlyLength)
			textView.setText(listOfContact.get(position)[0]);
		else
			textView.setText(listOfContact.get(position)[0]);
		LinearLayout contactClickableSection = (LinearLayout) rowView.findViewById(R.id.contactclickable);
		if(contactClickableSection!=null&&position<contactOnlyLength)
		{
			contactClickableSection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(context==null)
						Toast.makeText(getContext(), "context is null", Toast.LENGTH_LONG).show();
					else
					{
						GeneralManager.soundManager.sp.play(GeneralManager.soundManager.soundId, 1, 1,0, 0, 1);
						final String item = listOfContact.get(position)[1];
						Intent intent = new Intent(context, ContactDetailsActivity.class);
						intent.putExtra("userId",item);
						fragment.startActivityForResult(intent,GeneralManager.constantManager.REMOVE_FRIEND_ACTIVITY);
					}
				}
			});
		}
		else
		{
			contactClickableSection.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final String groupId = listOfContact.get(position)[1];
					Intent myIntent = new Intent(context, GroupDetailsActivity.class);
					//pass user id to next activity
					myIntent.putExtra("groupId", groupId);
					ContactActivity.setRepopulateAfterResume(true);
					fragment.startActivityForResult(myIntent,GeneralManager.constantManager.GROUP_DETAILS_ACTIVITY);

				}
			});
		}
		
		//only display profile for contact
		if(position<contactOnlyLength)
			imageLoader.DisplayImage(listOfContact.get(position)[2], imageView);
		return rowView;
	}

}

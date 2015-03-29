package com.my.dhreelife.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.my.dhreelife.R;
import com.my.dhreelife.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Obana on 7/19/13.
 */
public class TagFriendArrayListAdapter extends ArrayAdapter<String> {
	private List<String[]> listOfContact;
	private Context context;
	private Map<Integer,String> headerPosition = new HashMap<Integer,String>();
	private User[] friends;
	private ImageLoader imageLoader;

	public TagFriendArrayListAdapter(Context context, List<String[]> listOfContact,List<String> listOfContactNameOnly ,Map<Integer,String> headerPosition,User[] friends) {
		super(context, R.layout.tag_friend_list_view, listOfContactNameOnly);
		this.context = context;
		this.listOfContact = listOfContact;
		this.headerPosition = headerPosition;
		this.friends = friends;
		imageLoader = new ImageLoader(context);
	}

	@Override
	public View getView(final int position, final View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = null;

		if(headerPosition.get(position)!=null)
		{
			rowView = inflater.inflate(R.layout.tag_friend_top_section_list_view, parent, false);
			TextView sectionHeaderLabel = (TextView) rowView.findViewById(R.id.contact_section_header);
			if(sectionHeaderLabel!=null)
			{
				sectionHeaderLabel.setText(headerPosition.get(position));
			}
		}
		else
		{
			rowView = inflater.inflate(R.layout.tag_friend_list_view, parent, false);
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
		TextView tagUserId = (TextView) rowView.findViewById(R.id.tagUserId);
		CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.tagCheckbox);
		if(friends[position].checked)
			checkbox.setChecked(true);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {


			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if(checked)
					friends[position].checked = true;
				else
					friends[position].checked = false;
			}

		});
		//don't display the "g" in front of group name
		if(position<friends.length)
			textView.setText(listOfContact.get(position)[0]);
		else
			textView.setText(listOfContact.get(position)[0]);

		tagUserId.setText(listOfContact.get(position)[1]);
		//TODO create Contact class and attach photo.
		imageLoader.DisplayImage(friends[position].getProfileURL(), imageView);
		return rowView;
	}


}

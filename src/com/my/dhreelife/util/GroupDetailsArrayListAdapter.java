package com.my.dhreelife.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.dhreelife.R;

/**
 * Created by Obana on 7/19/13.
 */
public class GroupDetailsArrayListAdapter extends ArrayAdapter<String> {
	private List <String> groupUserList;
	private Context context;
	private Activity activity;
	public GroupDetailsArrayListAdapter(Context context, List<String> groupUserList) {
		super(context, R.layout.group_user_list_view, groupUserList);
		this.groupUserList = groupUserList;
		this.context = context;
	} 

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.group_user_list_view, parent, false);
		if(groupUserList!=null)
		{
			TextView groupUserName = (TextView) rowView.findViewById(R.id.group_user_name);
			String temp = groupUserList.get(position);
			final String id = temp.split(",")[0];
			String name = temp.split(",")[1];
			
			groupUserName.setText(name);
			
			Button tempButton = (Button) rowView.findViewById(R.id.btn_delete_group_user);
			tempButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removeGroupUserWithId(id);
				}
			});
			
			//make no text
			tempButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 20));
			tempButton.setText("");
			tempButton.setBackgroundResource(R.drawable.delete1);

		}
		
		
		
		
		return rowView;
	}
	
	public void removeGroupUserWithId(String idToDelete)
	{
		for(int i=0;i<groupUserList.size();i++)
		{
			String temp = groupUserList.get(i);
			String id = temp.split(",")[0];
			if(id.equals(idToDelete))
			{
				groupUserList.remove(i);
				notifyDataSetChanged();
				i=groupUserList.size();
			}
		}
	}
	
	@Override
	public void add(String groupUserToAdd)
	{
		if(!groupUserList.contains(groupUserToAdd))
		{
			groupUserList.add(groupUserToAdd);
			notifyDataSetChanged();
		}
	}
	
	public List<String> getData()
	{
		return groupUserList;
	}

}

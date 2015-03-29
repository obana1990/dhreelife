package com.my.dhreelife.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.dhreelife.R;
import com.my.dhreelife.util.manager.GeneralManager;

/**
 * Created by Obana on 7/19/13.
 */
public class SettingArrayListAdapter extends ArrayAdapter<String> {
	private List<String> settingMenu;
	private Context context;

	public SettingArrayListAdapter(Context context, List<String> settingMenu) {
		super(context.getApplicationContext(), R.layout.setting_list_view, settingMenu);
		this.context = context;
		this.settingMenu = settingMenu;
		//initiate sound effect
		GeneralManager.soundManager.soundId  = GeneralManager.soundManager.sp.load(context, R.raw.click, 1); 
	}
 

	@SuppressWarnings("static-access")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getApplicationContext()
				.getSystemService(context.getApplicationContext().LAYOUT_INFLATER_SERVICE);

		final View rowView = inflater.inflate(R.layout.setting_list_view, parent, false);

		TextView menu = (TextView) rowView.findViewById(R.id.txtSettingMenu);
		menu.setText(settingMenu.get(position));

		ImageView icon = (ImageView) rowView.findViewById(R.id.imgSettingMenuIcon);
		switch(position)
		{
		default:
		case 0:
			icon.setImageResource(R.drawable.menu_icon_profile);
			break;
		case 1:
			icon.setImageResource(R.drawable.menu_icon_edit_password);
			break;
		case 2:
			icon.setImageResource(R.drawable.menu_icon_reminder);
			break;
		case 3:
			icon.setImageResource(R.drawable.menu_icon_logout);
			break;	
		}
		return rowView;

	}

}

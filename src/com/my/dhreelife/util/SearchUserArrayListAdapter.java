package com.my.dhreelife.util;

import java.util.List;
import com.my.dhreelife.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchUserArrayListAdapter extends ArrayAdapter<String[]> {
	private final int NAME = 0;
	private final int ID = 1;
	private Context context;
	private List<String[]>names;
	private ImageLoader imageLoader; 


	public SearchUserArrayListAdapter(Context context, List<String[]> names) {
		super(context, R.layout.search_user_result_list_view, names);
		this.context = context;
		this.imageLoader=new ImageLoader(context);
		this.names = names;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.search_user_result_list_view, parent, false);

		TextView nameTag = (TextView) rowView.findViewById(R.id.username);
		TextView emailTag = (TextView) rowView.findViewById(R.id.useremail);
		ImageView profilePhoto = (ImageView) rowView.findViewById(R.id.searchUserProfilePhotoImg);

		nameTag.setText(names.get(position)[NAME]);

		emailTag.setText(names.get(position)[ID]);
		nameTag.setTextSize(14.0f);
		emailTag.setTextSize(14.0f);
		
		imageLoader.DisplayImage(names.get(position)[2], profilePhoto);

		return rowView;
	}
	
	@Override
    public long getItemId(int position) {
		return Long.parseLong(names.get(position)[ID]);
    }

}
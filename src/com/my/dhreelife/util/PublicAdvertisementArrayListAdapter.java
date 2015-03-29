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

/**
 * Created by Obana on 7/19/13.
 */
public class PublicAdvertisementArrayListAdapter extends ArrayAdapter<String[]> {
	private List<String[]> sponsorList;
	private Context context;
    private ImageLoader imageLoader; 

    public PublicAdvertisementArrayListAdapter(Context context, List<String[]> sponsorList) {
        super(context, R.layout.event_list_view, sponsorList);
        this.context = context;
        this.sponsorList = sponsorList;
        imageLoader=new ImageLoader(context);
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.public_advertisement_list_view, parent, false);
        if(sponsorList!=null)
        {
            TextView eventTitle;
            TextView eventLocation;
            ImageView eventIcon;

            eventTitle = (TextView) rowView.findViewById(R.id.txtSponsorTitle);
            eventLocation = (TextView) rowView.findViewById(R.id.txtSponsorDescription);
            eventIcon = (ImageView)rowView.findViewById(R.id.imgSponsorIcon);
            
            eventTitle.setText(sponsorList.get(position)[1]);
            eventLocation.setText(sponsorList.get(position)[2]);
            
            imageLoader.DisplayImage(sponsorList.get(position)[5], eventIcon);
        }

        return rowView;

}
}

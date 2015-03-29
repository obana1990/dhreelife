package com.my.dhreelife.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.dhreelife.R;
import com.my.dhreelife.util.manager.GeneralManager;

/**
 * Created by Obana on 7/19/13.
 */
public class ChatArrayListAdapter extends ArrayAdapter<String[]> {
	List<String[]> message;
    Context context;
    String ownerId;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ChatArrayListAdapter(Context context, List<String[]> message,String ownerId) {
        super(context, R.layout.contact_list_view, message);
        this.context = context;
        this.message = message;
        this.ownerId = ownerId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if(message.get(position)[2].equals(GeneralManager.authManager.userId))
        {
        	rowView = inflater.inflate(R.layout.chatting_conversation_view_right, parent, false);
        }
        else
        {
        	rowView = inflater.inflate(R.layout.chatting_conversation_view_left, parent, false);
        }
        
        TextView textView1 = (TextView) rowView.findViewById(R.id.chat_date);
        TextView textView2 = (TextView) rowView.findViewById(R.id.chat_content);
        TextView senderName = (TextView) rowView.findViewById(R.id.chatSenderNameTxt);

        LinearLayout chatBox = (LinearLayout) rowView.findViewById(R.id.chat_box);
        
		try {
			Date date = stringToDate.parse(message.get(position)[3]);
			textView1.setText(DateFormat.getDateTimeInstance(
		            DateFormat.MEDIUM, DateFormat.SHORT).format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        textView2.setText(message.get(position)[1]);
        if(message.get(position)[2].equals(GeneralManager.authManager.userId))
        	senderName.setText("Me");
        else
        	senderName.setText(message.get(position)[0]);


        return rowView;
    }

}

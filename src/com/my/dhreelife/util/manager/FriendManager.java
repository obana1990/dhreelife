package com.my.dhreelife.util.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.my.dhreelife.model.User;

public class FriendManager {
	private List<User> friends;
	private Context _context;
	
	private final String PREF_NAME = "friendlist";
	private final String FRIEND_NUMBER = "FRIEND_NUMBER";
	public static int PRIVATE_MODE = 0;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
	// Constructor
	public FriendManager(Context context){
		this._context = context;
	}
	
	public boolean isFriend(String id)
	{
		if(friends!=null&&friends.size()>0)
		{
			for(int i=0;i<friends.size();i++)
			{
				if(friends.get(i).getUserId().equals(id))
					return true;
			}
		}
		return false;
	}
	
	public void readFriendFromSharedPreferences()
	{
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		// first line = friend number , size
		int size = pref.getInt(FRIEND_NUMBER, 0);
		friends = new ArrayList<User>();
		// data are separated by comma, first one was id, followed by name and profile URL.
		if(size>0)
		{
			for(int i=0;i<size;i++)
			{
				String line = pref.getString(String.valueOf(i), "");
				if(!line.equals(""))
				{
					String[] lineBreakDown = line.split(",");
					User friend = new User();
					friend.setUserId(lineBreakDown[0]);
					friend.setName(lineBreakDown[1]);
					friend.setProfileURL(lineBreakDown[2]);
					friends.add(friend);
				}
			}
		}
	}
	
	public void writeFriendToSharedPreferences(User[] friends)
	{
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		editor.clear();
		int size = friends.length;
		editor.putInt(FRIEND_NUMBER, size);
		if(size>0)
		{
			for(int i=0;i<size;i++)
			{
				String line = friends[i].getUserId()+","+friends[i].getName()+","+friends[i].getProfileURL();
				editor.putString(String.valueOf(i), line);
			}
		}
		editor.commit();
		readFriendFromSharedPreferences();
	}
	
	public void addFriendToSharedPreferences(User friend)
	{
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		int size = pref.getInt(FRIEND_NUMBER, 0);
		editor.putInt(FRIEND_NUMBER, size+1);
		String line = friend.getUserId()+","+friend.getName()+","+friend.getProfileURL();
		editor.putString(String.valueOf(size-1), line);
		editor.commit();
	}
	
	public void setFriendList(List<User> friends)
	{
		this.friends = friends;
	}
}

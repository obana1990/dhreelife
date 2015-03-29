package com.my.dhreelife.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyRequestQueue {
	private Context context;
	private static RequestQueue requestQueueInstance;
	
	public VolleyRequestQueue(Context context)
	{
		this.context = context;
	}
	
	public RequestQueue getRequestQueueInstance()
	{
		if(requestQueueInstance==null)
		{
			requestQueueInstance =  Volley.newRequestQueue(context);
		}
		return requestQueueInstance;
	}
}

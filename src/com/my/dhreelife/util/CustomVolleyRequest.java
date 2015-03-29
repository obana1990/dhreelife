package com.my.dhreelife.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;

public class CustomVolleyRequest extends Request<String> {

	private Map<String, String> mParams;
	private StringVolleyResponseListener stringVolleyResponseListener;
	
	public CustomVolleyRequest(int method, String url, Map<String, String> mParams,StringVolleyResponseListener stringVolleyResponseListener, ErrorListener listener) {
		super(method, url, listener);
		this.stringVolleyResponseListener = stringVolleyResponseListener;
		this.mParams = mParams;
	}
 
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String response) {
		stringVolleyResponseListener.onStringResponse(response);
	}
	
	@Override
    public Map<String, String> getParams() {
        return mParams;
    }

}

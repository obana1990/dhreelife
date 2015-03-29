package com.my.dhreelife.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.my.dhreelife.util.manager.GeneralManager;
 
public class ImageUploadRequest extends Request<String> {
 
    private static final String FILE_PART_NAME = "file";
 
    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final StringVolleyResponseListener mListener;
    private final File mImageFile;
	protected Map<String, String> headers;
	private Context context;
     
    public ImageUploadRequest(String url, ErrorListener errorListener, StringVolleyResponseListener listener,File imageFile,Context context)
    {
        super(Method.POST, url, errorListener);
 
        mListener = listener;
        mImageFile = imageFile;
        
        buildMultipartEntity();
    }
 
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
	    Map<String, String> headers = super.getHeaders();
 
	    if (headers == null
	            || headers.equals(Collections.emptyMap())) {
	        headers = new HashMap<String, String>();
	    }
	    
	    headers.put("Accept", "application/json");
	    
	    return headers;
	}
 
    private void buildMultipartEntity()
    {
       	mBuilder.addBinaryBody(FILE_PART_NAME, mImageFile, ContentType.create("image/jpeg"), mImageFile.getName());
       	try {
			mBuilder.addPart("userid", new StringBody(GeneralManager.authManager.userId));
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
       	mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
       	mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }
 
    @Override
    public String getBodyContentType()
    {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    
    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
        	mBuilder.build().writeTo(bos);
        }
        catch (IOException e)
        {
        	Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }
        
        return bos.toByteArray();
    }
    
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response)
    {
    	String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
        	Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
    
    @Override
    protected void deliverResponse(String response)
    {
        mListener.onStringResponse(response);
    }
}
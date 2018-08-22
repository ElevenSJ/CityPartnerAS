package com.lyp.net;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpManager {

	public static String httpPost(String uriAPI,List<NameValuePair> params)
	{
		String result = null;
		HttpPost httpRequest =new HttpPost(uriAPI);
		
	    try{
	    
	    	if(params !=null)
	    	{
	    		httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
	    	}
	     HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
	    
	     if(httpResponse.getStatusLine().getStatusCode()==200)
	     {
	    	 result=EntityUtils.toString(httpResponse.getEntity());
	     }else{
	    	 return Errors.ERROR_SERVER;
	     }
	    }catch(Exception e){
	        return Errors.ERROR_NET;
	    }
	    return result;
	}
	
	public static String httpGet(String uriAPI)
    {
        String result = null;
        Log.i("httpget","uriAPI="+uriAPI);
        HttpGet httpRequest =new HttpGet(uriAPI);
        
        try{
        
         HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
        
         if(httpResponse.getStatusLine().getStatusCode()==200)
         {
             result=EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
         }else{
             return Errors.ERROR_SERVER;
         }
        }catch(Exception e){
            Log.e("httpget",e.getMessage());
            return Errors.ERROR_NET;
        }
        return result;
    }
}

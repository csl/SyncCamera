package com.camera;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SyncCamera extends Activity 
{
	String TAG = "SyncCamera debug: ";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Checking Status
        if (CheckInternet(3))
        {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null)
            {
                Log.v(TAG, " Location providers: " + provider);
                //Start searching for location and update the location text when update available
                //startFetchingLocation();
            }else{
                // Notify users and show settings if they want to enable GPS
            }        	
        }
        else
        {
        	
        }
        
        showNotification();     
    }
    
    private String getMyIp(){
        InetAddress ia;
        try {
                ia = InetAddress.getLocalHost();
                return ia.getHostAddress();
        } catch (UnknownHostException e) {
                Log.i("Err. get my IP failed.", e.toString());
        }
        return "err";
    }
    
    private long SyncTimeStamp()
    {
    	long timestamp_before, timestamp_after;
        try {
        		timestamp_before = System.currentTimeMillis()/1000;
        		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME, 1);
        		timestamp_after = System.currentTimeMillis()/1000;
                return timestamp_after - timestamp_before;
        } catch (Exception e) {
                Log.i("Err. get my IP failed.", e.toString());
        }
        return -1;
    }
    
    private boolean CheckInternet(int retry)
    {
    	boolean has = false;
    	for (int i=0; i<=retry; i++)
    	{
    		has = HaveInternet();
    		if (has == true) break;    		
    	}
    	
		return has;
    }
    
    private boolean HaveInternet()
    {
	     boolean result = false;
	     
	     ConnectivityManager connManager = (ConnectivityManager) 
	                                getSystemService(Context.CONNECTIVITY_SERVICE); 
	      
	     NetworkInfo info = connManager.getActiveNetworkInfo();
	     
	     if (info == null || !info.isConnected())
	     {
	    	 result = false;
	     }
	     else 
	     {
		     if (!info.isAvailable())
		     {
		    	 result =false;
		     }
		     else
		     {
		    	 result = true;
		     }
     }
    
     return result;
    }

	protected void showNotification() 
	{
        CharSequence from ="SyncCamera";
        CharSequence message ="running";


		//Intent intent = new Intent(this, rWebView.class);
        Intent intent = this.getIntent();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification notif = new Notification(R.drawable.icon , "SyncCamera",  System.currentTimeMillis());
		
		notif.setLatestEventInfo(this, from, message, contentIntent);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(R.string.app_name, notif);

    }

	void delenot() 
	{
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }    
    
    
}
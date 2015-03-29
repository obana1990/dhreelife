package com.my.dhreelife;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.my.dhreelife.fragment.EventConfirmationActivity;
import com.my.dhreelife.root.PendingEventDetailsActivity;
import com.my.dhreelife.root.WelcomeActivity;
import com.my.dhreelife.util.IncomingCall;
import com.my.dhreelife.util.manager.ConstantManager;
import com.my.dhreelife.util.manager.GeneralManager;
import com.my.dhreelife.util.manager.SessionManager;

public class GcmIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GcmIntentService() {
        super(ConstantManager.SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);

    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");

    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
      
        String message = intent.getExtras().getString("message");
        String type = intent.getExtras().getString("type");
        int eventId = 0;
        if(intent.getExtras().getString("eid")!=null)
        {
        	try
        	{
        		eventId = Integer.parseInt(intent.getExtras().getString("eid"));
        	}
        	catch (Exception e)
        	{
        		generateNotification(context, "Something went wrong exception occured",0,0);
        	}
        }
        
        if(type!=null)
        {
        	//type 2 = chat
        	if(type.equals("2"))
        	{
        		//only generate notification when the user is not at chat window
        		if(ConstantManager.CURRENT_TASK!=ConstantManager.EVENT_CONFIRMATION_ACTIVITY_ID)
        			generateNotification(context, message,2,eventId);
        		else
        		{
        			//even if user is at chat window but event id is not equal to incoming notification event id we generate notification too
        			if(GeneralManager.constantManager.CURRENT_CHAT_WINDOW_EVENT_ID!=eventId)
        				generateNotification(context, message,2,eventId);
        			else
        			{
        				try
            			{
            				//user currently in chat window, update chat box
                			EventConfirmationActivity.commentsCount+=1;
                			IncomingCall incomingCall = (IncomingCall) GeneralManager.constantManager.CURRENT_FRAGMENT;
        					incomingCall.onCall();
            			}
            			catch (Exception e)
            			{
            				e.printStackTrace();
            			}
        			}
        			
        			
        		}
        	}
        		
        	//type 1 = normal notification - friend request, tagged in event, event launched
        	else
            	generateNotification(context, message,0,0);
        }
        else
        	generateNotification(context, "Something went wrong",0,0);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        // notifies user
        generateNotification(context, "Deleted Message",0,0);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,int type,int referId) {
        int icon = R.drawable.logo_v2;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        Log.i(TAG, "Generating Notification");
        String title = context.getString(R.string.app_name);
        GeneralManager.sessionManager = new SessionManager(context);
        if(GeneralManager.sessionManager.isLoggedIn())
        {
        	if(type==2)
            {
        		Log.i(TAG, "Type 2");
                Intent notificationIntent = new Intent(context, PendingEventDetailsActivity.class);
                SharedPreferences pref = context.getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
            	String userId = pref.getString(SessionManager.KEY_USER_ID,null);
            	if(userId==null)
            	{
            		pref = context.getSharedPreferences(ConstantManager.FACEBOOK_AUTH, SessionManager.PRIVATE_MODE);
            		userId = pref.getString(SessionManager.KEY_USER_ID,null);
            	}
                // set intent so it does not start a new activity
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                GeneralManager.authManager.userId = userId;
                notificationIntent.putExtra("eventId", String.valueOf(referId));
                PendingIntent intent =
                        PendingIntent.getActivity(context, 0, notificationIntent, 0);
                notification.setLatestEventInfo(context, title, message, intent);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                // Play default notification sound
                notification.defaults |= Notification.DEFAULT_SOUND;
                
                //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
                
                // Vibrate if vibrate is enabled
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, notification);      
            }
            else
            {
            	Log.i(TAG, "Other type");
                Intent notificationIntent = new Intent(context, WelcomeActivity.class);
                // set intent so it does not start a new activity
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notificationIntent.putExtra("notification", "true");
                
                PendingIntent intent =
                        PendingIntent.getActivity(context, 0, notificationIntent, 0);
                notification.setLatestEventInfo(context, title, message, intent);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                
                // Play default notification sound
                notification.defaults |= Notification.DEFAULT_SOUND;
                
                //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
                
                // Vibrate if vibrate is enabled
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, notification);      
            }
        }

    }

}

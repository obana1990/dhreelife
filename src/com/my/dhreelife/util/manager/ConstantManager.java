package com.my.dhreelife.util.manager;

import android.app.Fragment;

public class ConstantManager {
	//http TASK ID
	public final static int LOGIN = 1;
	public final static int REGISTER = 2;
	public final static int SEARCH_USER = 3;
	public final static int SEND_FRIEND_REQUEST = 4;
	public final static int GET_NOTIFICATION_LIST = 5;
	public final static int LINK_FRIEND = 6;
	public final static int GET_FRIEND_LIST = 7;
	public final static int CONTACT_DETAILS_ACTIVITY = 8;
	//public final static int CONTACT_ACTIVITY = 9;
	public final static int ADD_NEW_EVENT_ACTIVITY = 10;
	public final static int GET_ALL_EVENT = 11;
	public final static int VIEW_EVENT = 12;
	public final static int GET_COMMENT = 13;
	public final static int POST_COMMENT = 14;
	public final static int ATTENDEES_INFO = 15;
	public final static int GET_ATTENDEES_STATUS = 16;
	public final static int UPDATE_ATTEND_STATUS = 17;
	public final static int LAUNCH_EVENT = 18;
	public final static int SET_NOTIFICATION_VIEWED = 19;
	public final static int UPDATE_LON_LATITUDE = 20;
	public final static int GET_RATING_LIST = 21;
	public final static int RATE = 22;
	public final static int CHECK_USERNAME = 23;
	public final static int GET_SECURITY_QUESTION = 24;
	public final static int RECOVER_PASSWORD = 25;
	public final static int UPDATE_SECURITY_ANSWER = 26;
	public final static int UPDATE_PASSWORD = 27;
	public final static int UPDATE_DEVICE_ID = 28;
	public final static int DISMISS_NOTIFICATION = 29;
	public final static int FB_REGISTER = 30;
	public final static int FB_LOGIN = 31;
	public final static int INVITE_FRIENDS_TO_EVENT = 32;
	public final static int GET_EVENT_INFO_FOR_RELAUNCH = 33;
	public final static int GET_ALL_SPONSOR_ADVERTISEMENT = 34;
	public final static int RELAUNCH_EVENT_ACTIVITY = 35;
	public final static int REMOVE_EVENT = 36;
	public final static int REMOVE_FRIEND = 37;
	public final static int GET_GROUP_USER = 38;
	public final static int REMOVE_GROUP = 39;
	public final static int ADD_GROUP = 40;
	public final static int UPDATE_GROUP_USER = 41;
	public final static int GET_GROUP_USER_ID = 42;
	public final static String FACEBOOK_ACTIVITY = "FACEBOOK_ACTIVITY";
	public final static int UPDATE_EVENT = 43;
	public final static int UPDATE_ALARM_ID = 44;
	
	
	//CURRENT TASK ID
	public final static int LOGIN_ACTIVITY_ID = 1;
	public final static int REGISTER_ACTIVITY_ID = 2;
	public final static int WELCOME_ACTIVITY_ID = 3;
	public final static int JOINED_EVENT_DETAILS_ACTIVITY_ID = 4;
	public final static int PENDING_EVENT_DETAILS_ACTIVITY_ID = 5;
	public final static int RECOVER_PASSWORD_ACTIVITY_ID = 6;
	public final static int CALENDAR_ACTIVITY_ID = 7;
	public final static int CHECK_IN_EVENT_ACTIVITY_ID = 8;
	public final static int CONTACT_ACTIVITY_ID = 9;
	public final static int EVENT_ACTIVITY_ID = 10;
	public final static int EVENT_CONFIRMATION_ACTIVITY_ID = 11;
	public final static int EVENT_EXTRA_INFORMATION_ACTIVITY_ID = 12;
	public final static int MAP_ACTIVITY_ID = 13;
	public final static int POST_EVENT_ACTIVITY_ID = 14;
	public final static int PUBLIC_ACTIVITY_ID = 15;
	public final static int RATING_ACTIVITY_ID = 16;
	public final static int SETTING_ACTIVITY_ID = 17;
	public final static int SHARE_EVENT_ACTIVITY_ID = 18;
	public final static int UPLOAD_PHOTO_ACTIVITY_ID = 19;
	public final static int ADD_NEW_CONTACT_ACTIVITY_ID = 20;
	public final static int ADD_NEW_GROUP_ACTIVITY_ID = 21;
	public final static int ADD_NEW_EVENT_ACTIVITY_ID = 22;
	public final static int ATTENDEES_INFO_ACTIVITY_ID = 23;
	public final static int CONTACT_DETAILS_ACTIVITY_ID = 24;
	public final static int GROUP_DETAILS_ACTIVITY_ID = 25;
	public final static int TAG_FRIEND_ACTIVITY_ID = 26;
	
	public static final String SENDER_ID = "249738786276";
	
	//to indicate which task is running
	/*
	 * ROOT
	 * 1 = LoginActivity
	 * 2 = RegisterActivity
	 * 3 = WelcomeActivity
	 * 4 = JoinedEventDetailsActivity
	 * 5 = PendingEventDetailsActivity
	 * 6 = RecoverPasswordActivity
	 * FRAGMENT
	 * 7 = CalendarActivity
	 * 8 = CheckInEventActivity
	 * 9 = ContactActivity
	 * 10 = EventActivity
	 * 11 = EventConfirmationActivity
	 * 12 = EventExtraInformationActivity
	 * 13 = MapActivity
	 * 14 = PostEventActivity
	 * 15 = PublicActivity
	 * 16 = RatingActivity
	 * 17 = SettingActivity
	 * 18 = ShareEventActivity
	 * 19 = UploadPhotoActivity
	 * ACTIVITY
	 * 20 = AddNewContactActivity
	 * 21 = AddNewGroupActivity
	 * 22 = AddNewEventActivity
	 * 23 = AttendeesInfo
	 * 24 = ContactDetailsActivity
	 * 25 = GroupDetailsActivity
	 * 26 = TagFriendActivity
	 * 
	 */
	public static int CURRENT_TASK = -1;
	public static Fragment CURRENT_FRAGMENT;
	public Fragment CURRENT_FRAGMENT_PARENT;
	public static int CURRENT_CHAT_WINDOW_EVENT_ID = -1;

	public final String url= "http://www.dhreelife.com/";
	
	//INCOMING NOTIFICATION 
	public final static String TAG_EVENT_NOTIFICATION = "1";
	public final static String FRIEND_REQUEST_NOTIFICATION = "2";
	public final static String LAUNCH_EVENT_NOTIFICATION = "3";
	public final static String POST_CHAT_NOTIFICATION = "4";
	
	//INCOMING NOTIFICATION TYPE
	public final static String PUBLIC_NOTIFICATION_TYPE = "1";
	public final static String CHAT_NOTIFICATION_TYPE = "2";

	
	
	//STATUS
	public final static int OK = 1;
	public final static int RESULT_OK = -1;

	//ACTIVITY ID for startActivityForResult
	public final int CREATE_GROUP_ACTIVITY = 9000;
	public final int CREATE_EVENT_ACTIVITY = 9001;
	public final int VIEW_EVENT_ACTIVITY = 9002;
	public final int INVITE_FRIENDS_TO_EVENT_ACTIVITY = 9003;
	public final int REMOVE_FRIEND_ACTIVITY = 9004;
	public final int GROUP_DETAILS_ACTIVITY = 9005;
	public final int CALENDAR_ENTRY_DETAILS_ACTIVITY = 9006;
	

	//OTHER
	public final static String ANDROID_MOBILE_TYPE = "1";
	public final static String EVENT_REMINDER = "event_reminder";
	public final static String FACEBOOK_AUTH = "facebook_auth";
	public final static int PRIVATE_MODE = 1;
	public final static int DEFAULT_CALENDAR_ID = 1;
	public final static int MIN_PASSWORD_LENGTH = 9;
	//same with server, each time load 10 message
	public final static int NUMBER_OF_NEW_LOAD_MESSAGE = 10;
	public final static int NUMBER_OF_NEW_LOAD_EVENT = 15;
	public final static int NUMBER_OF_NEW_LOAD_NOTIFICATION = 15;
	public final static int TYPE = 1;
	public final static  int STATE = 3;
	public final static  int REFER_ID = 2;
	public final static int NOTIFICATION_ID = 4;
	public final static int NOTIFICATION_MESSAGE = 0;
	public final static  String FRIEND_REQUEST_TYPE = "2";
	public final static String NOTIFICATION_VIEWED = "1";

	//UPLOAD
	public static  int RESULT_LOAD_IMAGE = 1;
	public static  final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	//HTTP STATUS
	public static final int HTTP_OK = 200;
	public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
	
	public static final String[] securityChoice=
		{
		"What is your mother maiden name?","Where were you born?","What is your first pet name?","What is your favourite color?","What is your father name?",
        "What is your favourite food?","What is your secondary school name?","In what city or town was your first job?","In what city or town did your mother and father meet?","What is the first name of the boy or girl that you first kissed?"
		};
	
	// STORE FRIEND LIST IN SHARED PREFERENCES OFFLINE
	public static FriendManager friendManager;
}

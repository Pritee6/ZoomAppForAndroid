package us.zoom.sdkexample2;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParam4WithoutLogin;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingParameter;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements Constants, MeetingServiceListener, ZoomSDKInitializeListener {

	private final static String TAG = "Zoom SDK Example";
	
	public final static String ACTION_RETURN_FROM_MEETING = "us.zoom.sdkexample2.action.ReturnFromMeeting";
	public final static String EXTRA_TAB_ID = "tabId";
	
	public final static int TAB_WELCOME = 1;
	public final static int TAB_MEETING = 2;
	public final static int TAB_PAGE_2  = 3;

	private final static String DISPLAY_NAME = "Pritee LRM";

	private View viewTabWelcome;
	private View viewTabMeeting;
	private Button btnTabWelcome;
	private Button btnTabMeeting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		setupTabs();

		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(savedInstanceState == null) {
			ZoomSDKInitParams initParams = new ZoomSDKInitParams();
			initParams.appKey = SDK_KEY;
			initParams.appSecret = SDK_SECRET;
			initParams.domain = WEB_DOMAIN;
			initParams.enableLog = true;
			zoomSDK.initialize(this, this, initParams);
		}
		
		if(zoomSDK.isInitialized()) {
			Log.d(TAG, "Is Zoom SDK Initialized: " + zoomSDK.isInitialized());
			registerMeetingServiceListener();
		}
	}

	private void setupTabs() {
		viewTabWelcome = findViewById(R.id.viewTabWelcome);
		viewTabMeeting = findViewById(R.id.viewTabMeeting);
		btnTabWelcome = (Button)findViewById(R.id.btnTabWelcome);
		btnTabMeeting = (Button)findViewById(R.id.btnTabMeeting);
		
		selectTab(TAB_WELCOME);
		
		btnTabMeeting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectTab(TAB_MEETING);
			}
		});
		
		btnTabWelcome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectTab(TAB_WELCOME);
			}
		});
	}
	
	private void selectTab(int tabId) {
		if(tabId == TAB_WELCOME) {
			viewTabWelcome.setVisibility(View.VISIBLE);
			viewTabMeeting.setVisibility(View.GONE);
			btnTabWelcome.setSelected(true);
			btnTabMeeting.setSelected(false);
		} else if(tabId == TAB_PAGE_2) {
			viewTabWelcome.setVisibility(View.GONE);
			viewTabMeeting.setVisibility(View.GONE);
			btnTabWelcome.setSelected(false);
			btnTabMeeting.setSelected(false);
		} else if(tabId == TAB_MEETING) {
			ZoomSDK zoomSDK = ZoomSDK.getInstance();
			if(!zoomSDK.isInitialized()) {
				Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
				return;
			}
			
			MeetingService meetingService = zoomSDK.getMeetingService();
			if(meetingService == null)
				return;
			
			if(meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_IDLE){
				
				viewTabWelcome.setVisibility(View.GONE);
				viewTabMeeting.setVisibility(View.VISIBLE);
				btnTabWelcome.setSelected(false);
				btnTabMeeting.setSelected(true);
				
				startMeeting();
			} else {
				meetingService.returnToMeeting(this);
			}
			
			overridePendingTransition(0, 0);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		// disable animation
		overridePendingTransition(0,0);
		
		String action = intent.getAction();
		
		if(ACTION_RETURN_FROM_MEETING.equals(action)) {
			int tabId = intent.getIntExtra(EXTRA_TAB_ID, TAB_WELCOME);
			selectTab(tabId);
		}
	}

	@Override
	public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
		Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);
		
		if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
			Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG);
		} else {
			Toast.makeText(this, "Initialized Zoom SDK successfully.", Toast.LENGTH_LONG).show();
			
			registerMeetingServiceListener();
		}
	}

	private void registerMeetingServiceListener() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		MeetingService meetingService = zoomSDK.getMeetingService();
		if(meetingService != null) {
			meetingService.addListener(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		
		if(zoomSDK.isInitialized()) {
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeListener(this);
		}
		
		super.onDestroy();
	}

	public void startMeeting() {
		
		ZoomSDK zoomSDK = ZoomSDK.getInstance();

		if(!zoomSDK.isInitialized()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(MEETING_ID == null) {
			Toast.makeText(this, "MEETING_ID in Constants can not be NULL", Toast.LENGTH_LONG).show();
			return;
		}
		
		MeetingService meetingService = zoomSDK.getMeetingService();

		/*StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
		params.userId = USER_ID;
		params.zoomAccessToken = ZOOM_ACCESS_TOKEN;
		params.meetingNo = MEETING_ID;
		params.displayName = DISPLAY_NAME;

		int ret = meetingService.startMeetingWithParams(this, params, opts);*/

		JoinMeetingOptions joinMeetingOptions = new JoinMeetingOptions();

		StartMeetingOptions opts = new StartMeetingOptions();
		opts.no_driving_mode = true;
		opts.no_titlebar = true;
		opts.no_bottom_toolbar = true;
		opts.no_invite = true;
		opts.no_video = true;

		JoinMeetingParam4WithoutLogin params= new JoinMeetingParam4WithoutLogin();
//		StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
		params.userId = "";
		params.zoomAccessToken = "";
		params.meetingNo = MEETING_ID;
		params.displayName = DISPLAY_NAME;
		params.password = MEETING_PASSWORD;

//		int ret = meetingService.startMeetingWithParams(this, params, opts);
		int ret = meetingService.joinMeetingWithParams(this, params, joinMeetingOptions);

		Log.i(TAG, "onClickBtnStartMeeting, ret=" + ret);
	}

	@Override
	public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode,
			int internalErrorCode) {
		
		if(meetingStatus == meetingStatus.MEETING_STATUS_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
			Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
		}
		
		if(meetingStatus == MeetingStatus.MEETING_STATUS_IDLE || meetingStatus == MeetingStatus.MEETING_STATUS_FAILED) {
			selectTab(TAB_WELCOME);
		}
	}

	@Override
	public void onZoomAuthIdentityExpired() {

	}

	@Override
	public void onMeetingParameterNotification(MeetingParameter meetingParameter) {

	}
}

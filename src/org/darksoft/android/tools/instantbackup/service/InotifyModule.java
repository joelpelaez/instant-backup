package org.darksoft.android.tools.instantbackup.service;

import android.app.IntentService;
import android.content.Intent;

public class InotifyModule extends IntentService {

	public static final String INOTIFY_START =
			"org.darksoft.android.tools.instantbackup.INOTIFY_START";
	public static final String INOTIFY_STOP =
			"org.darksoft.android.tools.instantbackup.INOTIFY_STOP";
	public static final String INOTIFY_RECEIVE =
			"org.darksoft.android.tools.instantbackup.INOTIFY_RECEIVE";
	
	public InotifyModule() {
		super("InotifyModule");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

}

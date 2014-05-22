/*
 *  This file is part of Instant Backup
 *  FileMonitor.java - Main Service for Instant Backup.
 *  Copyright (C) 2012  Joel Peláez Jorge <joelpelaez@gmail.com>
 * 
 *  Instant Backup is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Instant Backup is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Instant Backup.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.darksoft.android.tools.instantbackup.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darksoft.android.nativelib.inotify.Inotify;
import org.darksoft.android.nativelib.inotify.InotifyLowEvent;
import org.darksoft.android.nativelib.inotify.MaxUserWatchesException;
import org.darksoft.android.tools.instantbackup.FileAction;
import org.darksoft.android.tools.instantbackup.FileNotify;
import org.darksoft.android.tools.instantbackup.InstantBackup;
import org.darksoft.android.tools.instantbackup.R;
import org.darksoft.android.tools.instantbackup.SingleMediaScanner;
import org.darksoft.android.tools.instantbackup.db.InotifyDataSource;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

public class FileMonitor extends Service {

	private static final int NOTIFICATION_ID = 0xdafd0001;
	private volatile boolean isValid;
	private Inotify mInotify;
	private List<FileNotify> mNotifyTable;
	private SparseArray<FileAction> mActionTable;
	private NotificationManager mNM;
	private InotifyDataSource datasource;

	private IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {

		FileMonitor getService() {
			return FileMonitor.this;
		}
	}

	private Thread mReadThread = new Thread(new Runnable() {

		@Override
		public void run() {
			Log.i("FileMonitor", "In Thread Service");
			while (isValid)
				executeAction(mInotify.getEvent());
		}
	}, "InotifyThread");

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			if (bundle.getString("update") != null) {
				updateInotify();
			}
		}
		return START_STICKY;
	}

	private void updateInotify() {
		// Remove all watchers
		mInotify.removeAllWatchers();

		// Get new NotifyTable
		mNotifyTable = datasource.getAllInotify();

		// Add all events to Inotify
		Iterator<FileNotify> it = mNotifyTable.iterator();
		while (it.hasNext()) {
			int w = 0;
			FileNotify f = it.next();
			try {
				w = mInotify.addWatch(f.filename, f.mask);
			} catch (MaxUserWatchesException e) {
				return;
			}
			FileAction action = new FileAction();
			action.setAction(f.action);
			action.setFileName(f.extra);
			action.setTypes(f.types);
			action.setSpecial(f.special);
			mActionTable.append(w, action);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Initialize all objetcs
		mInotify = new Inotify();
		mActionTable = new SparseArray<FileAction>();
		datasource = new InotifyDataSource(this);
		datasource.open();

		// Update inotify
		updateInotify();

		// Register a notification
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();

		// Execute InotifyThread
		isValid = true;
		mReadThread.start();

	}

	@Override
	public void onDestroy() {
		// Disable Thread
		isValid = false;

		// Remove notification
		mNM.cancel(R.string.file_monitor_started);
		Toast.makeText(this, R.string.file_monitor_stopped, Toast.LENGTH_SHORT)
				.show();
		stopForeground(true);

		// Remove all watchers
		mInotify.removeAllWatchers();
		mInotify.close();
		mActionTable.clear();

		// Close database
		datasource.close();

		// Kill worked thread
		mReadThread.interrupt();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@SuppressWarnings("deprecation")
	private void showNotification() {

		CharSequence text = getText(R.string.file_monitor_started);
		Notification notification = new Notification(R.drawable.ic_launcher,
				text, System.currentTimeMillis());

		Intent activity = new Intent(this, InstantBackup.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				activity, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(this, getText(R.string.service_label),
				text, contentIntent);

		Toast.makeText(this, R.string.file_monitor_started, Toast.LENGTH_SHORT)
				.show();

		startForeground(NOTIFICATION_ID, notification);
	}

	private void executeAction(InotifyLowEvent ev) {
		Log.i("FileMonitor", "In executeAction");

		// Get a current watcher and get action info
		int w = ev.getWatcher();
		FileAction action = mActionTable.get(w);
		if (action == null) {
			Log.i("FileMonitor", "In executeAction: null");
			return;
		}

		Log.i("FileMonitor", String.format("Filename: %1s", ev.getFileName()));
		if (action.getFileName() != null) {
			Log.i("FileMonitor",
					String.format("Filename Extra: %1s", action.getFileName()));
		}
		// Check if event watcher was removed
		if ((ev.getMaskEvent() & Inotify.IN_IGNORED) == Inotify.IN_IGNORED) {
			mActionTable.remove(w);
			Log.i("FileMonitor", "In executeAction: IN_IGNORE");
			return;
		}

		if (action.getSpecial() == FileAction.SPECIAL_RECURSIVE_NEW_FILE) {
			int childW = -1;
			try {
				childW = mInotify.addWatch(ev.getFileName(),
						Inotify.IN_CLOSE_WRITE | Inotify.IN_ONESHOT);
			} catch (MaxUserWatchesException e) {
				return;
			}
			ArrayList<FileAction> child = action.getChildActions();
			if (child == null) {
				child = new ArrayList<FileAction>();
				action.setChildActions(child);
			}

			FileAction newAction = new FileAction();
			newAction.setActionType(FileAction.ACTION_TYPE_CHILD);
			newAction.setAction(action.getAction());
			newAction.setFileName(action.getFileName());
			mActionTable.append(childW, newAction);
		} else if (action.getAction() == FileAction.ACTION_MULTIMEDIA) {
			new SingleMediaScanner(this, new File(ev.getFileName()));
			Log.i("FileMonitor", "In executeAction: ACTION_MULTIMEDIA");
		} else if (action.getAction() == FileAction.ACTION_COPY) {
			try {
				checkCopy(ev.getFileName(), action.getFileName());
			} catch (IOException e) {
			}
			Log.i("FileMonitor", "In executeAction: ACTION_COPY");
		} else if (action.getAction() == FileAction.ACTION_MOVE) {
			checkMove(ev.getFileName(), action.getFileName());
			Log.i("FileMonitor", "In executeAction: ACTION_MOVE");
		} else if (action.getAction() == FileAction.ACTION_DELETE) {
			checkDelete(ev.getFileName());
			Log.i("FileMonitor", "In executeAction: ACTION_DELETE");
		}
		Log.i("FileMonitor",
				String.format("File Action: %1x", action.getAction()));
		Log.i("FileMonitor", String.format("File Mask: %1x", ev.getMaskEvent()));
		if (action.getActionType() == FileAction.ACTION_TYPE_CHILD) {
			// Delete manually FileAction entry (IN_ONESHOT not cause IN_IGNORE)
			mActionTable.remove(w);
		}
	}

	private void checkDelete(String fileName) {
		File file = new File(fileName);
		file.delete();
	}

	private void checkMove(String src, String dst) {
		File fsrc = new File(src);
		File fdst = new File(dst);
		if (fdst.isDirectory()) {
			fsrc.renameTo(new File(fdst, fsrc.getName()));
		}
		fsrc.renameTo(fdst);
	}

	private void checkCopy(String src, String dst) throws IOException {
		File fdst = new File(dst);
		File fsrc = new File(src);
		if (fsrc.isDirectory()) {
			return;
		}
		if (fdst.isDirectory()) {
			File newdst = new File(fdst, fsrc.getName());
			newdst.createNewFile();
			copy(fsrc, newdst);
		}
		copy(fsrc, fdst);
	}

	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

}
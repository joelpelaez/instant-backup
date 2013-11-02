/*
 *  This file is part of Inofity Java Interface
 *  Inotify.java - Main Class for Inotify Wrapper
 *  Copyright (C) 2012  Joel Peláez Jorge <joelpelaez@gmail.com>
 * 
 *  Inofity Java Interface is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Inofity Java Interface is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Inofity Java Interface.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.darksoft.android.nativelib.inotify;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.util.SparseArray;

/**
 * Low level interface for inotify Linux Kernel API. This module is used for
 * monitor files, directories, etc. (inodes). This implementation is a Java
 * interface, it is simple.
 * 
 * @author Joel Peláez Jorge
 * 
 */
public class Inotify {

	/* the following are legal, implemented events that user-space can watch for */
	/**
	 * User-space event: File was accessed.
	 */
	public static final long IN_ACCESS = 0x00000001;

	/**
	 * User-space event: File was modified.
	 */
	public static final long IN_MODIFY = 0x00000002;

	/**
	 * User-space event: Metadata changed.
	 */
	public static final long IN_ATTRIB = 0x00000004;

	/**
	 * User-space event: Writtable file was closed.
	 */
	public static final long IN_CLOSE_WRITE = 0x00000008;

	/**
	 * User-space event: Unwrittable file closed
	 */
	public static final long IN_CLOSE_NOWRITE = 0x00000010;

	/**
	 * File was opened
	 */
	public static final long IN_OPEN = 0x00000020;

	/**
	 * User-space event: File was moved from X.
	 */
	public static final long IN_MOVED_FROM = 0x00000040;

	/**
	 * User-space event: File was moved to Y.
	 */
	public static final long IN_MOVED_TO = 0x00000080;

	/**
	 * User-space event: Subfile created.
	 */
	public static final long IN_CREATE = 0x00000100;

	/**
	 * User-space event: Subfile was deleted.
	 */
	public static final long IN_DELETE = 0x00000200;

	/**
	 * User-space event: Self was deleted.
	 */
	public static final long IN_DELETE_SELF = 0x00000400;

	/**
	 * User-space event: Self was moved.
	 */
	public static final long IN_MOVE_SELF = 0x00000800;

	/* the following are legal events. they are sent as needed to any watch */
	/**
	 * Kernel event: Backing filesystem was unmounted.
	 */
	public static final long IN_UNMOUNT = 0x00002000;

	/**
	 * Kernel event: Event queued overflowed.
	 */
	public static final long IN_Q_OVERFLOW = 0x00004000;

	/**
	 * Kernel event: File was ignored.
	 */
	public static final long IN_IGNORED = 0x00008000;

	/* helper events */
	/**
	 * Generic event: File was closed.
	 */
	public static final long IN_CLOSE = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE);

	/**
	 * Generic event: File was moved.
	 */
	public static final long IN_MOVE = (IN_MOVED_FROM | IN_MOVED_TO);

	/* special flags */
	/**
	 * Special flag: Only watch the path if it is a directory.
	 */
	public static final long IN_ONLYDIR = 0x01000000;

	/**
	 * Special flag: Don't follow a symlink.
	 */
	public static final long IN_DONT_FOLLOW = 0x02000000;

	/**
	 * Special flag: Exclude events on unlinked objects.
	 */
	public static final long IN_EXCL_UNLINK = 0x04000000;

	/**
	 * Special flag: Add to the mask of an already existing watch.
	 */
	public static final long IN_MASK_ADD = 0x20000000;

	/**
	 * Special flag: Event occurred against directory.
	 */
	public static final long IN_ISDIR = 0x40000000;

	/**
	 * Special flag: Only send event once.
	 */
	public static final long IN_ONESHOT = 0x80000000;

	private int mInotify;
	private SparseArray<Watcher> mTableWatch = null;

	static {
		System.loadLibrary("inotify_module");
	}
	
	/**
	 * Create a new instance of Inotify. Init the inotify monitor.
	 */
	public Inotify() {
		mTableWatch = new SparseArray<Watcher>();
		nativeInotifyInit();
	}

	/**
	 * A nested class for Watcher information.
	 * 
	 * @author Joel Peláez Jorge
	 * 
	 */
	public static class Watcher {
		public String mName;
		public long mMask;
	}

	/**
	 * A nested class for inotify events. For local use.
	 * 
	 * @author Joel Peláez Jorge
	 * 
	 */
	public static class Event {
		private long mMask;
		private int mWatch;
		private String mName = new String();

		public int getWatch() {
			return mWatch;
		}

		public String getName() {
			return mName;
		}

		public long getMask() {
			return mMask;
		}
	}

	/**
	 * Add a new watch to inotify monitor.
	 * 
	 * @param mName
	 *            Filename to monitor.
	 * @param mMask
	 *            Mask of events to accept and monitor.
	 * @return The watcher descriptor, or -1 on error.
	 */
	public int addWatch(String mName, long mMask) throws MaxUserWatchesException {
		int wd = nativeInotifyAddWatch(mName, mMask);
		if (wd == -1) {
			throw new MaxUserWatchesException();
		}
		Watcher wc = new Watcher();
		wc.mName = mName;
		wc.mMask = mMask;
		mTableWatch.put(Integer.valueOf(wd), wc);
		return wd;
	}

	public void removeWatch(int mWatcher) {
		Watcher wc = mTableWatch.get(Integer.valueOf(mWatcher));
		if (wc == null) {
			return;
		}
		nativeInotifyRmWatch(mWatcher);
		mTableWatch.remove(Integer.valueOf(mWatcher));
	}

	public InotifyLowEvent getEvent() {
		Watcher wd;
		Event ev;
		byte[] buffer = new byte[4096];
		FileDescriptor fd = new FileDescriptor();
		InotifyLowEvent pev = new InotifyLowEvent();
		nativeSetFileDescriptor(fd);
		@SuppressWarnings("resource")
		FileInputStream in = new FileInputStream(fd);
		try {
			in.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ev = nativeParseEvent(buffer);
		wd = mTableWatch.get(ev.getWatch());
		if (ev.getName() != null)
			pev.setFileName(wd.mName + "/" + ev.getName());
		else
			pev.setFileName(wd.mName);
		pev.setMaskEvent(ev.getMask());
		pev.setWatcher(ev.getWatch());
		return pev;
	}

	public SparseArray<Watcher> getTableWatch() {
		return mTableWatch;
	}
	
	public void removeAllWatchers() {
		for (int i = 0; i < mTableWatch.size(); i++) {
			int wdNum = mTableWatch.keyAt(i);
			removeWatch(wdNum);
		}
	}
	
	public void close() {
		nativeInotifyClose();
	}
	
	private native void nativeInotifyInit();

	private native int nativeInotifyAddWatch(String mName, long mMask);

	private native void nativeInotifyRmWatch(int mWatcher);

	private native Event nativeInotifyGetEvent();
	
	private native void nativeInotifyClose();
	
	private native void nativeSetFileDescriptor(FileDescriptor fd);
	
	private native Event nativeParseEvent(byte[] data);
}

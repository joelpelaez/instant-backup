/*
 *  This file is part of Inofity Java Interface
 *  InotifyEvent.java - High-level Class Event
 *  Copyright (C) 2012  Joel Peláez Jorge <joelpelaez@gmail.com>
 * 
 *  Inofity Java Interface is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Inofity Java Interface Control is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Inofity Java Interface.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.darksoft.android.nativelib.inotify;

public class InotifyEvent {
	private boolean mCreated = false;
	private boolean mDeleted = false;
	private boolean mDeletedSelf = false;
	private boolean mMovedFrom = false;
	private boolean mMovedTo = false;
	private boolean mMoved = false;
	private boolean mClosedInWrite = false;
	private boolean mClosedInNoWrite = false;
	private boolean mClosed = false;
	private boolean mOpened = false;
	private boolean mAccess = false;
	private boolean mDir = false;
	private String mName = null;

	public void processEvent(String name, long mask) {
		mName = name;
		if ((mask & Inotify.IN_ACCESS) == Inotify.IN_ACCESS) {
			this.mAccess = true;
		}
		if ((mask & Inotify.IN_CREATE) == Inotify.IN_CREATE) {
			this.mCreated = true;
		}
		if ((mask & Inotify.IN_MOVED_FROM) == Inotify.IN_MOVED_FROM) {
			this.mMoved = this.mMovedFrom = true;
		}
		if ((mask & Inotify.IN_MOVED_TO) == Inotify.IN_MOVED_TO) {
			this.mMoved = this.mMovedTo = true;
		}
		if ((mask & Inotify.IN_CLOSE_WRITE) == Inotify.IN_CLOSE_WRITE) {
			this.mClosed = this.mClosedInWrite = true;
		}
		if ((mask & Inotify.IN_CLOSE_NOWRITE) == Inotify.IN_CLOSE_NOWRITE) {
			this.mClosed = this.mClosedInNoWrite = true;
		}
		if ((mask & Inotify.IN_ISDIR) == Inotify.IN_ISDIR) {
			this.mDir = true;			
		}
		if ((mask & Inotify.IN_DELETE) == Inotify.IN_DELETE) {
			this.mDeleted = true;			
		}
		if ((mask & Inotify.IN_DELETE_SELF) == Inotify.IN_DELETE_SELF) {
			this.mDeletedSelf = true;			
		}
	}
	
	public String getFileName() {
		return mName;
	}
	
	public boolean isAccessed() {
		return mAccess;
	}
	
	public boolean isCreated() {
		return mCreated;
	}
	
	public boolean isDeleted() {
		return mDeleted;
	}
	
	public boolean isDeletedSelf() {
		return mDeletedSelf;
	}
	
	public boolean isMoved() {
		return mMoved;
	}
	
	public boolean isMovedFrom() {
		return mMovedFrom;
	}
	
	public boolean isMovedTo() {
		return mMovedTo;
	}
	
	public boolean isDir() {
		return mDir;
	}
	
	public boolean isOpened() {
		return mOpened;
	}
	
	public boolean isClosedInWrite() {
		return mClosedInWrite;
	}
	
	public boolean isClosedInNoWrite() {
		return mClosedInNoWrite;
	}
	
	public boolean isClosed() {
		return mClosed;
	}
}

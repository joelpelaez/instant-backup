/*
 *  This file is part of Inofity Java Interface
 *  InotifyLowEvent.java - Low-level Class Event
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

public class InotifyLowEvent  {

	private int mWatcher;
	private String mFileName;
	private long mMaskEvent;

	public void setFileName(String name) {
		this.mFileName = name;
	}
	
	public void setMaskEvent(long mask) {
		this.mMaskEvent = mask;
	}
	
	public void setWatcher(int wd) {
		this.mWatcher = wd;
	}
	
	public String getFileName() {
		return mFileName;
	}
	
	public long getMaskEvent() {
		return mMaskEvent;
	}
	
	public int getWatcher() {
		return mWatcher;
	}
	
	@Override
	public String toString() {
		return mFileName;
	}

}
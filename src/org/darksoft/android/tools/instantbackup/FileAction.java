/*
 *  This file is part of Instant Backup
 *  FileAction.java - File actions for Inotify interface.
 *  Copyright (C) 2012-2014  Joel Peláez Jorge <joelpelaez@gmail.com>
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

package org.darksoft.android.tools.instantbackup;

import java.util.ArrayList;

public class FileAction {

	public static final long ACTION_COPY = 0x0001;
	public static final long ACTION_MOVE = 0x0002;
	public static final long ACTION_DELETE = 0x0004;
	public static final long ACTION_MULTIMEDIA = 0x0008;

	public static final long SPECIAL_RECURSIVE_NEW_FILE = 0x0001;

	public static final long ACTION_TYPE_PARENT = 0x0000;
	public static final long ACTION_TYPE_CHILD = 0x0001;

	private long mActionType;
	private String mFileName;
	private long mAction;
	private FileType[] mTypes;
	private long mSpecial;
	private ArrayList<FileAction> mChild = null;

	public void setFileName(String name) {
		this.mFileName = name;
	}

	public void setAction(long action) {
		this.mAction = action;
	}

	public String getFileName() {
		return mFileName;
	}

	public long getAction() {
		return mAction;
	}

	public FileType[] getTypes() {
		return mTypes;
	}

	public void setTypes(FileType[] mTypes) {
		this.mTypes = mTypes;
	}

	public long getSpecial() {
		return mSpecial;
	}

	public void setSpecial(long mSpecial) {
		this.mSpecial = mSpecial;
	}

	public long getActionType() {
		return mActionType;
	}

	public void setActionType(long mActionType) {
		this.mActionType = mActionType;
	}

	public ArrayList<FileAction> getChildActions() {
		return mChild;
	}

	public void setChildActions(ArrayList<FileAction> mChild) {
		this.mChild = mChild;
	}

}

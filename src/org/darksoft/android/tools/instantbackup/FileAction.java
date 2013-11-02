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

package org.darksoft.android.tools.instantbackup;

public class FileNotify {
	public long id = -1;
	public long action = 0;
	public long mask = 0;
	public long special = 0;
	public FileType[] types = null;
	public String filename = null;
	public String extra = null;
	
	@Override
	public String toString() {
		return filename;
	}
}

/*
 *  This file is part of Instant Backup
 *  FileNotify.java - FileNotify data class.
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

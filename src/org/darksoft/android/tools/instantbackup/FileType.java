/*
 *  This file is part of Instant Backup
 *  FileType.java - Analyze filetype.
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

package org.darksoft.android.tools.instantbackup;

import java.io.File;

import android.webkit.MimeTypeMap;

public class FileType {
	private String mimeType;
	private String extension;

	public FileType() {
	}
	
	public FileType(String mime) {
		mimeType = mime;
	}
	
	public FileType(File file) {
		extension = MimeTypeMap
				.getFileExtensionFromUrl(file.toURI().toString());
		mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				extension);
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public String getExtension() {
		return extension;
	}
}

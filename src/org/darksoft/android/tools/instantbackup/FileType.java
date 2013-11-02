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

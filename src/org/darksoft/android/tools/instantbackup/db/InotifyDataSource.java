/*
 *  This file is part of Instant Backup
 *  InotifyDataSource.java - Interface for SQLite File Watchers.
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

package org.darksoft.android.tools.instantbackup.db;

import java.util.ArrayList;
import java.util.List;

import org.darksoft.android.tools.instantbackup.FileNotify;
import org.darksoft.android.tools.instantbackup.FileType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InotifyDataSource {
	private SQLiteDatabase database = null;
	private InotifySQLiteHelper dbHelper;
	private String[] allColumns = { InotifySQLiteHelper.COLUMN_ID,
			InotifySQLiteHelper.COLUMN_FILE, InotifySQLiteHelper.COLUMN_MASK,
			InotifySQLiteHelper.COLUMN_ACTION,
			InotifySQLiteHelper.COLUMN_SPECIAL,
			InotifySQLiteHelper.COLUMN_MIME, InotifySQLiteHelper.COLUMN_EXTRA };

	public InotifyDataSource(Context context) {
		dbHelper = new InotifySQLiteHelper(context);
	}

	public void open() throws SQLException {
		if (database != null)
			return;
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null)
			dbHelper.close();
	}

	public FileNotify createInotify(FileNotify file) {
		ContentValues content = new ContentValues();
		content.put(InotifySQLiteHelper.COLUMN_FILE, file.filename);
		content.put(InotifySQLiteHelper.COLUMN_MASK, file.mask);
		content.put(InotifySQLiteHelper.COLUMN_ACTION, file.action);
		content.put(InotifySQLiteHelper.COLUMN_SPECIAL, file.special);
		String mimes = "";
		if (file.types != null) {
			for (FileType type : file.types) {
				mimes += ":";
				mimes += type.getMimeType();
			}
		}
		if (mimes.contentEquals("")) {
			mimes = "none";
		}
		content.put(InotifySQLiteHelper.COLUMN_MIME, mimes);
		content.put(InotifySQLiteHelper.COLUMN_EXTRA, file.extra);
		long insertId = database.insert(InotifySQLiteHelper.TABLE_INOTIFY,
				null, content);
		file.id = insertId;
		Cursor cursor = database.query(InotifySQLiteHelper.TABLE_INOTIFY,
				allColumns, InotifySQLiteHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		cursor.close();

		return file;
	}

	public void deleteInotify(FileNotify file) {
		long id = file.id;
		database.delete(InotifySQLiteHelper.TABLE_INOTIFY,
				InotifySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<FileNotify> getAllInotify() {
		List<FileNotify> list = new ArrayList<FileNotify>();

		Cursor cursor = database.query(InotifySQLiteHelper.TABLE_INOTIFY,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			FileNotify file = cursorToInotify(cursor);
			list.add(file);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	private FileNotify cursorToInotify(Cursor cursor) {
		FileNotify file = new FileNotify();
		file.id = cursor.getLong(0);
		file.filename = cursor.getString(1);
		file.mask = cursor.getLong(2);
		file.action = cursor.getLong(3);
		file.special = cursor.getLong(4);
		String[] types = cursor.getString(5).split(":");
		file.types = new FileType[types.length];
		int i = 0;
		for (String type : types) {
			file.types[i++] = new FileType(type);
		}
		file.extra = cursor.getString(6);
		return file;
	}
}

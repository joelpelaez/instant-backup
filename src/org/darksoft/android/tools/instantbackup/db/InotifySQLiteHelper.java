/*
 *  This file is part of Instant Backup
 *  InotifySQLitehelper.java - SQLite Helper.
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InotifySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_INOTIFY = "Inotify";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FILE = "file";
	public static final String COLUMN_MASK = "mask";
	public static final String COLUMN_ACTION = "action";
	public static final String COLUMN_SPECIAL = "special";
	public static final String COLUMN_MIME = "mime";
	public static final String COLUMN_EXTRA = "extra";

	private static final String DATABASE_NAME = "inotify.sqlite";
	private static final int DATABASE_VERSION = 1;
	private final String sqlCreate = "CREATE TABLE " + TABLE_INOTIFY + " ("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FILE
			+ " TEXT NOT NULL, " + COLUMN_MASK + " INTEGER, " + COLUMN_ACTION
			+ " INTEGER, " + COLUMN_SPECIAL + " INTEGER, " + COLUMN_MIME
			+ " TEXT, " + COLUMN_EXTRA + " TEXT );";

	public InotifySQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		// Specific a static name and version
		super(context, DATABASE_NAME, factory, version);
	}

	public InotifySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(InotifySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXIST Inotify");
		db.execSQL(sqlCreate);
	}

}

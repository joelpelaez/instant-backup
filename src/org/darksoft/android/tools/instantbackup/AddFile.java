/*
 *  This file is part of Instant Backup
 *  AddFile.java - Add file Activity for FileMonitor.
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

import org.darksoft.android.nativelib.inotify.Inotify;
import org.openintents.intents.FileManagerIntents;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddFile extends Activity {

	private static final int REQUEST_SRC_DIR = 1;
	private static final int REQUEST_DST_DIR = 2;
	private EditText mEditTextSrc = null;
	private EditText mEditTextDst = null;
	private Button mButtonSrc = null;
	private Button mButtonDst = null;
	private Button mButtonAdd = null;
	private CheckBox mCheckBoxNewFile = null;
	private CheckBox mCheckBoxModified = null;
	private CheckBox mCheckBoxDeleted = null;
	private Spinner mSpinnerAction = null;

	private View.OnClickListener mSrcListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);
			intent.setData(Uri.parse("file:///sdcard"));
			intent.putExtra(FileManagerIntents.EXTRA_TITLE, getResources()
					.getString(R.string.dialog_src_title));
			intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT,
					getResources().getString(R.string.dialog_button));
			startActivityForResult(intent, REQUEST_SRC_DIR);
		}
	};

	private View.OnClickListener mDstListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);
			intent.setData(Uri.parse("file:///sdcard"));
			intent.putExtra(FileManagerIntents.EXTRA_TITLE, getResources()
					.getString(R.string.dialog_dst_title));
			intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT,
					getResources().getString(R.string.dialog_button));
			try {
				startActivityForResult(intent, REQUEST_DST_DIR);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(AddFile.this, R.string.error_pick,
						Toast.LENGTH_LONG).show();
			}
		}
	};
	private View.OnClickListener mAddlistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String filename;
			String dest;
			long special = 0;
			long mask = 0;
			long action = 0;

			// Get filenames (Source and Destination)
			filename = Uri.parse(mEditTextSrc.getText().toString()).getPath();
			dest = Uri.parse(mEditTextDst.getText().toString()).getPath();

			// Check selected events
			if (mCheckBoxNewFile.isChecked()) {
				mask |= Inotify.IN_CREATE;
				special |= FileAction.SPECIAL_RECURSIVE_NEW_FILE;
			}
			if (mCheckBoxModified.isChecked()) {
				mask |= Inotify.IN_MODIFY;
			}
			if (mCheckBoxDeleted.isChecked()) {
				mask |= Inotify.IN_DELETE;
				mask |= Inotify.IN_DELETE_SELF;
			}
			int pos = mSpinnerAction.getSelectedItemPosition();

			// Define Action
			if (pos == 0)
				action = FileAction.ACTION_COPY;
			if (pos == 1)
				action = FileAction.ACTION_MOVE;
			if (pos == 2)
				action = FileAction.ACTION_DELETE;
			if (pos == 3)
				action = FileAction.ACTION_MULTIMEDIA;

			Intent data = new Intent();
			data.putExtra("filename", filename);
			data.putExtra("dest", dest);
			data.putExtra("mask", mask);
			data.putExtra("action", action);
			data.putExtra("special", special);
			setResult(RESULT_OK, data);
			finish();
		}
	};

	private OnItemSelectedListener SpinnerItem = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (pos == 0 || pos == 1) {
				mEditTextDst.setEnabled(true);
				mButtonDst.setEnabled(true);
			} else {
				mEditTextDst.setEnabled(false);
				mButtonDst.setEnabled(false);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_file);

		mEditTextSrc = (EditText) findViewById(R.id.editText_src);
		mEditTextDst = (EditText) findViewById(R.id.editText_dst);
		mButtonSrc = (Button) findViewById(R.id.select_src);
		mButtonDst = (Button) findViewById(R.id.select_dst);
		mButtonAdd = (Button) findViewById(R.id.button_add);
		mCheckBoxNewFile = (CheckBox) findViewById(R.id.check_new_file);
		mCheckBoxModified = (CheckBox) findViewById(R.id.check_modified);
		mCheckBoxDeleted = (CheckBox) findViewById(R.id.check_deleted);
		mSpinnerAction = (Spinner) findViewById(R.id.spinner_actions);

		mButtonSrc.setOnClickListener(mSrcListener);
		mButtonDst.setOnClickListener(mDstListener);
		mButtonAdd.setOnClickListener(mAddlistener);
		mSpinnerAction.setOnItemSelectedListener(SpinnerItem);

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spinner_actions,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinnerAction.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_file, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SRC_DIR) {
			if (resultCode == RESULT_OK) {
				mEditTextSrc.setText(data.getData().toString());
			}
		}
		if (requestCode == REQUEST_DST_DIR) {
			if (resultCode == RESULT_OK) {
				mEditTextDst.setText(data.getData().toString());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected Activity getActivity() {
		return this;
	}
}

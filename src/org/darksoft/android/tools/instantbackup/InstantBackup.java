package org.darksoft.android.tools.instantbackup;

import java.util.List;

import org.darksoft.android.tools.instantbackup.db.InotifyDataSource;
import org.darksoft.android.tools.instantbackup.service.FileMonitor;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class InstantBackup extends ListActivity {

	private static final int REQUEST_ADD_FILE = 1;
	private InotifyDataSource datasource = null;
	private Button mAddButton = null;
	private Button mQuitButton = null;
	private Button mRestartButton = null;

	private View.OnClickListener mAddListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), AddFile.class);
			startActivityForResult(intent, REQUEST_ADD_FILE);
		}
	};

	private View.OnClickListener mQuitListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), FileMonitor.class);
			stopService(intent);
			finish();
		}
	};

	private View.OnClickListener mRestartListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), FileMonitor.class);
			stopService(intent);
			startService(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instant_backup);

		mAddButton = (Button) findViewById(R.id.add);
		mQuitButton = (Button) findViewById(R.id.quit);
		mRestartButton = (Button) findViewById(R.id.restart);

		mAddButton.setOnClickListener(mAddListener);
		mQuitButton.setOnClickListener(mQuitListener);
		mRestartButton.setOnClickListener(mRestartListener);

		datasource = new InotifyDataSource(this);
		datasource.open();

		List<FileNotify> list = datasource.getAllInotify();

		ArrayAdapter<FileNotify> adapter = new ArrayAdapter<FileNotify>(this,
				android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
		registerForContextMenu(getListView());

		Intent intent = new Intent(this, FileMonitor.class);
		startService(intent);
	}

	@Override
	public void onDestroy() {
		datasource.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_instant_backup, menu);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		FileNotify notify = (FileNotify) getListAdapter()
				.getItem(info.position);
		@SuppressWarnings("unchecked")
		ArrayAdapter<FileNotify> adapter = (ArrayAdapter<FileNotify>) getListAdapter();
		datasource.deleteInotify(notify);
		adapter.remove(notify);
		adapter.notifyDataSetChanged();
		Intent intent = new Intent(this, FileMonitor.class);
		intent.putExtra("update", "1.0");
		startService(intent);
		return super.onContextItemSelected(item);
	}

	protected Activity getActivity() {
		return this;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ADD_FILE) {
			if (resultCode == RESULT_OK) {
				FileNotify notify = new FileNotify();
				notify.filename = data.getExtras().getString("filename");
				notify.mask = data.getExtras().getLong("mask");
				notify.action = data.getExtras().getLong("action");
				notify.special = data.getExtras().getLong("special");
				notify.extra = data.getExtras().getString("dest");
				datasource.createInotify(notify);
				@SuppressWarnings("unchecked")
				ArrayAdapter<FileNotify> adapter = (ArrayAdapter<FileNotify>) getListAdapter();
				adapter.add(notify);
				adapter.notifyDataSetChanged();
				Intent intent = new Intent(this, FileMonitor.class);
				intent.putExtra("update", "1.0");
				startService(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			menu.setHeaderTitle(getResources().getText(R.string.options));
			menu.add(Menu.NONE, 1, Menu.NONE,
					getResources().getText(R.string.delete));
		}
	}

}

package com.example.neo.app.act3;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.neo.app.R;
import com.example.neo.app.act3.PullToRefreshListView.OnRefreshListener;

/**
 * TestRefreshActivity
 * 
 * @author zj
 * @since 2014-8-14 1.0
 */
public class TestRefreshActivity extends Activity {

	PullToRefreshListView listView;
	ArrayAdapter<String> adapter;
	List<String> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act3_pull_to_refresh);

		listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_view);
		data = new ArrayList<String>();
		data.add("new item0");
		data.add("new item0");
		data.add("new item0");
		data.add("new item0");
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data);
		listView.setAdapter(adapter);
		
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					// 刷新过程中需要做的操作在这里
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						data.add("new item1");
						data.add("new item2");
						data.add("new item3");
						data.add("new item4");
						return null;
					}

					// 刷新完成后要通知listview进行界面调整
					@Override
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}
				}.execute(new Void[] {});
			}
		});
	}
}

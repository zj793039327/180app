package com.example.neo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.neo.app.act1.Act2048;
import com.example.neo.app.act3.TestRefreshActivity;
import com.example.neo.app.commons.dialog.IgnitedDialogs;

public class MainActivity extends Activity {

	ListView mainMenu;
	SimpleAdapter sa;
	List<Map<String, Object>> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainMenu = (ListView) findViewById(R.id.main_menu);

		data = new ArrayList<Map<String, Object>>();
		addData();

		sa = new SimpleAdapter(this, data, R.layout.main_menu_item,
				new String[] { "img", "name", "desc" }, new int[] {
						R.id.act_icon, R.id.act_name, R.id.act_desc });

		mainMenu.setAdapter(sa);

		mainMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, Object> m = data.get(position);

				Class<Activity> c = (Class<Activity>) m.get("class");
				if (c == null) {
					showUnderWorkingDiolag();
					return;
				}
				Intent i = new Intent();
				i.setClassName(MainActivity.this, c.getCanonicalName());
				startActivity(i);
			}
		});
	}

	private void showUnderWorkingDiolag() {
		IgnitedDialogs.newMessageDialog(this, "提示", "正在建设中",
				android.R.drawable.ic_dialog_alert).show();
	}

	private void addData() {

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("img", R.drawable.ap1_icon);
		m.put("name", "2048");
		m.put("desc", "其实是csdn里面 '阳光小强' 同学的程序，借鉴之。");
		m.put("class", Act2048.class);
		data.add(m);

		m = new HashMap<String, Object>();
		m.put("img", R.drawable.ap2_icon);
		m.put("name", "贪吃蛇");
		m.put("desc", "还是csdn里面 '阳光小强' 同学的程序，借鉴之。");
		m.put("class", null);
		data.add(m);
		
		m = new HashMap<String, Object>();
		m.put("img", R.drawable.ap_normal);
		m.put("name", "下拉刷新ListView");
		m.put("desc", "自己实现一个，看下效果");
		m.put("class", TestRefreshActivity.class);
		data.add(m);
	}
}

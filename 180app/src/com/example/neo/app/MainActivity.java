package com.example.neo.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	ListView mainMenu;
	ArrayAdapter<String> aa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainMenu = (ListView) findViewById(R.id.main_menu);
		List<String> s = new ArrayList<String>();
		s.add("com.example.neo.app.act1.Act2048");
		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, s);
		mainMenu.setAdapter(aa);
		mainMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}
}

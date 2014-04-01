package com.vmodev.stackphotoview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private AnimStackView stackView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializedViews();
	}

	private void initializedViews() {
		stackView = (AnimStackView) findViewById(R.id.stackView);
		List<Profile> list = new ArrayList<Profile>();
		list.add(new Profile("Person A", R.drawable.avatar1));
		list.add(new Profile("Thỏ Ngọc", R.drawable.avatar2));
		list.add(new Profile("Thỏ Ngọc", R.drawable.avatar3));
		list.add(new Profile("Thỏ Ngọc", R.drawable.avatar2));
		list.add(new Profile("Thỏ Ngọc", R.drawable.avatar3));
		stackView.setAdapter(new ProfileViewAdapter(this, list));
	}
}

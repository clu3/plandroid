package com.vmodev.stackphotoview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileViewAdapter extends ArrayAdapter<Profile> {

	public ProfileViewAdapter(Context context, List<Profile> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.photo_item, null);
			holder = new ViewHolder();
			holder.imvAvatar = (ImageView) convertView
					.findViewById(R.id.imvAvatar);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Profile profile = getItem(position);
		holder.tvName.setText(profile.getName());
		holder.imvAvatar.setImageResource(profile.getResIdAvatar());
		return convertView;
	}

	private static class ViewHolder {
		public ImageView imvAvatar;
		public TextView tvName;
	}
}

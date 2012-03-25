package com.crack.nfc;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crack.storage.Friend;
import com.crack.storage.Repository;

public class FriendsListAdapter extends BaseAdapter {

	private ArrayList<Friend> friends;
	private Context context;

	public FriendsListAdapter(Context context) {
		this.context = context;
		friends = Repository.getInstance(context).getFriends();
	}

	//@Override
	public int getCount() {
		return friends.size();
	}

	//@Override
	public Object getItem(int arg0) {
		return friends.get(arg0);
	}

	//@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	//@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = (View) inflater.inflate(R.layout.friend_list_item, null);
		}
		Friend f = (Friend) getItem(arg0);

		ImageView iv = (ImageView) arg1.findViewById(R.id.imgProfileIcon);
		TextView txtName = (TextView) arg1.findViewById(R.id.txtName);
		TextView txtLastSeen = (TextView) arg1.findViewById(R.id.txtLastSeen);
		TextView txtEmail = (TextView) arg1.findViewById(R.id.txtEmail);

		txtName.setText(f.getName());
		txtLastSeen.setText(((System.currentTimeMillis()-f.getStaleness())/1000)/60 + " mins ago");
		txtEmail.setText(f.getEmail());

		// need to set the image
		try {
			//URL myUrl = new URL(f.getImageUrl());
			//InputStream inputStream = (InputStream) myUrl.getContent();
			//Drawable drawable = Drawable.createFromStream(inputStream, null);
			//iv.setImageDrawable(grabImageFromUrl(f.getImageUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arg1;
	}

	private Drawable grabImageFromUrl(String url) throws Exception {
		return Drawable.createFromStream((InputStream) new URL(url).getContent(), "src");
	}

}

package com.crack.nfc;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crack.storage.Friend;
import com.crack.storage.Repository;

public class FriendsListAdapter extends BaseAdapter implements Observer {

	private ArrayList<Friend> friends;
	private Context context;

	public FriendsListAdapter(Context context) {
		this.context = context;
		friends = Repository.getInstance(context).getFriends();
		Repository.getInstance(context).addObserver(this);
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Object getItem(int arg0) {
		return friends.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
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
		txtLastSeen.setText(f.getStaleness() + " days ago");
		txtEmail.setText(f.getEmail());
		//iv.setImageDrawable(grabImageFromUrl(f.getImageUrl()));
		return arg1;
	}

	//@Override
	public void update(Observable arg0, Object arg1) {
		friends = Repository.getInstance(context).getFriends();
		this.notifyDataSetChanged();
	}

}

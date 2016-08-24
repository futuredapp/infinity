package com.thefuntasty.infinity.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.test.R;
import com.thefuntasty.infinity.test.User;

public class SingleViewAdapter extends InfinityAdapter<User, SingleViewAdapter.ViewHolder> {

	@Override
	public ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user_type_1, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindContentViewHolder(SingleViewAdapter.ViewHolder holder, int position) {
		((TextView) holder.itemView).setText(getItem(position).toString());
	}

	@Override
	public int getFooterLayout() {
		return R.layout.footer_layout;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}

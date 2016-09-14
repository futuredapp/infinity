package com.thefuntasty.infinity.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.test.R;
import com.thefuntasty.infinity.test.User;

public class MultipleViewsAdapter extends InfinityAdapter<User, MultipleViewsAdapter.ViewHolder> {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	@Override
	public ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
		if (viewType == LEFT) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user_type_1, parent, false);
			return new ViewHolder(view);
		} else if (viewType == RIGHT) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user_type_2, parent, false);
			return new ViewHolder(view);
		} else {
			throw new IllegalStateException("Unknown viewType: " + String.valueOf(viewType));
		}
	}

	@Override
	public void onBindContentViewHolder(MultipleViewsAdapter.ViewHolder holder, int position) {
		if (getContentItemViewType(position) == LEFT) {
			((TextView) holder.itemView).setText(getContentItem(position).toString() + "L");
		} else {
			((TextView) holder.itemView).setText(getContentItem(position).toString() + "R");
		}
	}

	@Override
	public int getFooterLayout() {
		return R.layout.footer_layout;
	}

	@Override
	public int getContentItemViewType(int position) {
		return position % 2 == 0 ? LEFT : RIGHT;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}

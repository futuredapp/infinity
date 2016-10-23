package com.thefuntasty.infinity.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thefuntasty.infinity.InfinityAdapter;

public class SampleUserAdapter extends InfinityAdapter<User, SampleUserAdapter.ViewHolder> {

	private static final int LEFT = 0;
	private static final int RIGHT = 1;

	private static final int HEADER_PURPLE = 50;
	private static final int HEADER_RED = 51;

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

	@Override public ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
		if (viewType == HEADER_PURPLE) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_type_1, parent, false);
			return new ViewHolder(view);
		} else if (viewType == HEADER_RED) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_type_2, parent, false);
			return new ViewHolder(view);
		} else {
			throw new IllegalStateException("Unknown header viewType: " + String.valueOf(viewType));
		}
	}

	@Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
		((TextView) holder.itemView).setText("I'm header" + position);
	}

	@Override public int getHeaderCount() {
		return 2;
	}

	@Override public int getHeaderItemViewType(int position) {
		if (position == 0) {
			return HEADER_PURPLE;
		} else {
			return HEADER_RED;
		}

	}

	@Override
	public void onBindContentViewHolder(SampleUserAdapter.ViewHolder holder, int position) {
		((TextView) holder.itemView).setText(getContentItem(position).toString());
	}

	@Override
	public int getFooterLayout() {
		return R.layout.footer_layout;
	}

	@Override
	public int getContentItemViewType(int position) {
		return position % 2 == 0 ? LEFT : RIGHT;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ViewHolder(View itemView) {
			super(itemView);
		}
	}
}

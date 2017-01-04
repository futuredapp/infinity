package com.thefuntasty.infinity;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public final class FooterViewHolder extends RecyclerView.ViewHolder {
	View loading;
	View retry;

	FooterViewHolder(View v) {
		super(v);
		loading = v.findViewById(R.id.loading);
		retry = v.findViewById(R.id.retry);

		if (loading == null || retry == null) {
			throw new IllegalStateException("Footer view doesn't contain View with id/loading or id/retry");
		}
	}

	public View getLoadingView() {
		return loading;
	}

	public View getRetryView() {
		return retry;
	}
}

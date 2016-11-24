package com.thefuntasty.infinity;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public final class FooterViewHolder extends RecyclerView.ViewHolder {
	View loading;
	View tryAgain;

	FooterViewHolder(View v) {
		super(v);
		loading = v.findViewById(R.id.loading);
		tryAgain = v.findViewById(R.id.try_again);

		if (loading == null || tryAgain == null) {
			throw new IllegalStateException("Footer view doesn't contain View with id/loading or id/tryAgain");
		}
	}

	public View getLoadingView() {
		return loading;
	}

	public View getTryAgainView() {
		return tryAgain;
	}
}
package com.thefuntasty.infinity;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public final class FooterViewHolder extends RecyclerView.ViewHolder {
	public View loading;
	public View tryAgain;

	public FooterViewHolder(View v) {
		super(v);
		loading = v.findViewById(R.id.loading);
		tryAgain = v.findViewById(R.id.try_again);

		if (loading == null || tryAgain == null) {
			throw new IllegalStateException("Footer view doesn't contain View with id/loading or id/tryAgain");
		}
	}
}
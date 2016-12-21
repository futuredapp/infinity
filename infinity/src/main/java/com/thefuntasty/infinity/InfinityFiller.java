package com.thefuntasty.infinity;

import android.support.annotation.UiThread;

import java.util.List;

public interface InfinityFiller<T> {

	void onLoad(int limit, int offset, InfinityFiller.Callback<T> callback);

	class Callback<T> {
		protected boolean interrupted = false;

		@UiThread public void onData(List<T> collection) { }

		@UiThread public void onError(Throwable error) { }

		protected void setInterrupted() {
			interrupted = true;
		}
	}
}

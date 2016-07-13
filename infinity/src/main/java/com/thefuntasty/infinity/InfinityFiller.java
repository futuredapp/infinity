package com.thefuntasty.infinity;

import android.support.annotation.UiThread;

import java.util.List;

public abstract class InfinityFiller<T> {

	private InfinityFiller.Callback<T> firstPageCallback;
	private InfinityFiller.Callback<T> nextPageCallback;

	void resetCallbacks(InfinityFiller.Callback<T> firstPageCallback, InfinityFiller.Callback<T> nextPageCallback) {
		if (this.firstPageCallback != null) {
			this.firstPageCallback.setInterrupted();
		}

		if (this.nextPageCallback != null) {
			this.nextPageCallback.setInterrupted();
		}

		this.firstPageCallback = firstPageCallback;
		this.nextPageCallback = nextPageCallback;
	}

	public abstract void onLoad(int limit, int offset, Callback<T> callback);

	Callback<T> getFirstPageCallback() {
		return firstPageCallback;
	}

	Callback<T> getNextPageCallback() {
		return nextPageCallback;
	}

	public static class Callback<T> {
		protected boolean interrupted = false;

		@UiThread public void onData(List<T> collection) { }

		@UiThread public void onError(Throwable error) { }

		protected void setInterrupted() {
			interrupted = true;
		}
	}
}

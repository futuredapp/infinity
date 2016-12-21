package com.thefuntasty.infinity;

public class InfinityFillerImpl<T> {

	private InfinityFiller.Callback<T> firstPageCallback;
	private InfinityFiller.Callback<T> nextPageCallback;

	private InfinityFiller<T> infinityFiller;

	InfinityFillerImpl(InfinityFiller<T> infinityFiller) {
		this.infinityFiller = infinityFiller;
	}

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

	void onLoad(int limit, int offset, InfinityFiller.Callback<T> callback) {
		infinityFiller.onLoad(limit, offset, callback);
	}

	InfinityFiller.Callback<T> getFirstPageCallback() {
		return firstPageCallback;
	}

	InfinityFiller.Callback<T> getNextPageCallback() {
		return nextPageCallback;
	}
}

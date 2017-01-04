package com.thefuntasty.infinity;

import android.support.annotation.UiThread;

import java.util.List;

public interface InfinityFiller<T> {

	void onLoad(int limit, int offset, InfinityFiller.Callback<T> callback);

	class Callback<T> {
		protected boolean interrupted = false;

		/**
		 * Method to add data downloaded from data source to the adapter
		 *
		 * @param collection one page data downloaded from source (API)
		 */
		@UiThread public void onData(List<T> collection) { }

		/**
		 * Method to set error state to the adapter
		 * @param error throwable what holds information about error what occurred
		 */
		@UiThread public void onError(Throwable error) { }

		protected void setInterrupted() {
			interrupted = true;
		}
	}
}

package com.thefuntasty.infinity;

interface InfinityEventInterface {
	/**
	 * Called when first page loaded successfully
	 *
	 * @param pullToRefresh true if loading has been done via pull to refresh
	 */
	void onFirstLoaded(boolean pullToRefresh);

	/**
	 * Called when loading of first page produced error
	 * @param error Throwable representing exception thrown when obtaining/processing page
	 * @param pullToRefresh true if loading has been done via pull to refresh
	 */
	void onFirstUnavailable(Throwable error, boolean pullToRefresh);

	/**
	 * Called when first page loaded successfully but there are no items
	 * @param pullToRefresh true if loading has been done via pull to refresh
	 */
	void onFirstEmpty(boolean pullToRefresh);

	/**
	 * Called when non-first page successfully loaded
	 */
	void onNextLoaded();

	/**
	 * Called when loading of non-first page produced error
	 * @param error Throwable representing exception thrown when obtaining/processing page
	 */
	void onNextUnavailable(Throwable error);

	/**
	 * Called when loading of last page ends successfully
	 */
	void onFinished();

	/**
	 * Called before attempt to load first page
	 * @param pullToRefresh true if loading has been done via pull to refresh
	 */
	void onPreLoadFirst(boolean pullToRefresh);

	/**
	 * Called before attempt to load non-first page
	 */
	void onPreLoadNext();
}
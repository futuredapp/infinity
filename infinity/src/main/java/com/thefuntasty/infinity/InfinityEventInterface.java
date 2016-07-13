package com.thefuntasty.infinity;

public interface InfinityEventInterface {
	void onFirstLoaded(boolean pullToRefresh);

	void onFirstUnavailable(Throwable error, boolean pullToRefresh);

	void onFirstEmpty(boolean pullToRefresh);

	void onNextLoaded();

	void onNextUnavailable(Throwable error);

	void onFinished();

	void onPreLoadFirst(boolean pullToRefresh);

	void onPreLoadNext();
}
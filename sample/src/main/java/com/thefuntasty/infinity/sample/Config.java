package com.thefuntasty.infinity.sample;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.thefuntasty.infinity.InfinityEventListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Config {

	Context context;

	SampleUserAdapter adapter;
	RecyclerView.LayoutManager layoutManager;
	DataManager dataManager = DataManager.get();
	private InfinityEventListener eventListener;

	public Config(Context context) {
		this.context = context;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public void setAdapter(SampleUserAdapter adapter) {
		this.adapter = adapter;
	}

	public SampleUserAdapter getAdapter() {
		if (adapter == null) {
			return new SampleUserAdapter();
		} else {
			return adapter;
		}
	}

	public RecyclerView.LayoutManager getLayoutManager() {
		if (layoutManager == null) {
			layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
		}
		return layoutManager;
	}

	public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	public Observable<List<User>> getDataObservable(final int limit, final int offset) {
		Observable<User> observable = Observable.from(App.getConfig().getDataManager().getData());

		if (offset == 30) {
			return Observable.just(Collections.<User>emptyList())
					.delay(2, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io());
		} else if (offset != 0) { // next part
			return observable
					.take(limit)
					.buffer(limit)
					.delay(2, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io());
		} else { // first part
			return observable.skip(10)
					.take(limit)
					.buffer(limit)
					.delay(2, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io());
		}
	}

	public InfinityEventListener getEventListener(final SwipeRefreshLayout refresh) {
		if (eventListener == null) {
			eventListener = new InfinityEventListener() {
				@Override public void onFirstEmpty(boolean pullToRefresh) {
					if (pullToRefresh) {
						refresh.setRefreshing(false);
					}
				}

				@Override public void onFirstUnavailable(Throwable error, boolean pullToRefresh) {
					if (pullToRefresh) {
						refresh.setRefreshing(false);
					}
				}

				@Override public void onFirstLoaded(boolean pullToRefresh) {
					if (pullToRefresh) {
						refresh.setRefreshing(false);
					}
				}
			};
		}

		return eventListener;
	}

	private void setEventListener(InfinityEventListener eventListener) {
		this.eventListener = eventListener;
	}
}

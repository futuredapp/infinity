package com.thefuntasty.infinity.test;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.InfinityEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Config {

	Context context;

	InfinityAdapter adapter;
	RecyclerView.LayoutManager layoutManager;
	InfinityEventListener eventListener;
	int limit = 10;

	public Config(Context context) {
		this.context = context;
	}

	public InfinityAdapter getAdapter() {
		if (adapter == null) {
			adapter = new SampleUserAdapter();
		}

		return adapter;
	}

	public RecyclerView.LayoutManager getLayoutManager() {
		if (layoutManager == null) {
			layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
		}
		return layoutManager;
	}

	public Observable<List<User>> getDataObservable(final int limit, final int offset) {
		Observable<User> observable = Observable.from(getData());

		if (offset == 20) {
			return Observable.just(Collections.<User>emptyList())
					.delay(2, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io());
		} else if (offset != 0) { // next part
			return observable
					.skip(10)
					.take(limit)
					.buffer(limit)
					.delay(2, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io());
		} else { // first part
			return observable
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

	public int getLimit() {
		return limit;
	}

	public List<User> getData() {
		final ArrayList<User> data = new ArrayList<>(20);
		data.add(new User("Alice", "0"));
		data.add(new User("Bruno", "1"));
		data.add(new User("Cecil", "2"));
		data.add(new User("David", "3"));
		data.add(new User("Emil", "4"));
		data.add(new User("František", "5"));
		data.add(new User("Gertruda", "6"));
		data.add(new User("Honza", "7"));
		data.add(new User("Ivo", "8"));
		data.add(new User("Jan", "9"));
		data.add(new User("Kamil", "10"));
		data.add(new User("Lukáš", "11"));
		data.add(new User("Martin", "12"));
		data.add(new User("Norbert", "13"));
		data.add(new User("Otto", "14"));
		data.add(new User("Petr", "15"));
		data.add(new User("Quido", "16"));
		data.add(new User("Radek", "17"));
		data.add(new User("Stanislav", "18"));
		data.add(new User("Tomáš", "19"));

		return data;
	}
}

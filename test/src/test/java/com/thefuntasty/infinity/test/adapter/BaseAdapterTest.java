package com.thefuntasty.infinity.test.adapter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.InfinityEventListener;
import com.thefuntasty.infinity.InfinityFiller;
import com.thefuntasty.infinity.test.App;
import com.thefuntasty.infinity.test.User;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public abstract class BaseAdapterTest {
	protected RecyclerView recyclerView;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		recyclerView = new RecyclerView(RuntimeEnvironment.application);
		recyclerView.measure(0, 0);
		recyclerView.layout(0, 0, 100, 1000);

		final InfinityAdapter<User, ?> adapter = getAdapter();
		recyclerView.setLayoutManager(getLayoutManager());
		recyclerView.setAdapter(adapter);

		adapter.setLimit(getLimit());
		adapter.setFiller(new InfinityFiller<User>() {
			@Override public void onLoad(final int limit, final int offset, final InfinityFiller.Callback<User> callback) {
				getDataObservable(limit, offset)
						.subscribe(new Action1<List<User>>() {
							@Override public void call(List<User> users) {
								callback.onData(users);
							}
						}, new Action1<Throwable>() {
							@Override public void call(Throwable throwable) {
								callback.onError(throwable);
							}
						});
			}
		});
		adapter.setEventListener(getEventListener(null));
		adapter.start();
	}

	public int getLimit() {
		return App.get().getConfig().getLimit();
	}

	public RecyclerView.LayoutManager getLayoutManager() {
		return App.get().getConfig().getLayoutManager();
	}

	public InfinityAdapter<User, ?> getAdapter() {
		return App.get().getConfig().getAdapter();
	}

	public InfinityEventListener getEventListener(SwipeRefreshLayout refresh) {
		return App.get().getConfig().getEventListener(refresh);
	}

	public Observable<List<User>> getDataObservable(int limit, int offset) {
		return App.get().getConfig().getDataObservable(limit, offset);
	}
}

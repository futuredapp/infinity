package com.thefuntasty.infinity.sample;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.InfinityEventListener;
import com.thefuntasty.infinity.InfinityFiller;

import java.util.List;

import rx.functions.Action1;

public class SampleActivity extends AppCompatActivity {

	RecyclerView recyclerView;
	SwipeRefreshLayout refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		recyclerView = (RecyclerView) findViewById(R.id.recycler);
		refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);

		final InfinityAdapter<User, ?> adapter = new SampleUserAdapter();
		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override public void onRefresh() {
				adapter.restart(true);
			}
		});
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		adapter.setLimit(10);
		adapter.setFiller(new InfinityFiller<User>() {
			@Override public void onLoad(final int limit, final int offset, final InfinityFiller.Callback<User> callback) {
				DataManager.get().getDataObservable(limit, offset)
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
		adapter.setEventListener(new InfinityEventListener() {
			@Override public void onFirstLoaded(boolean pullToRefresh) {
				super.onFirstLoaded(pullToRefresh);
			}

			@Override public void onFirstUnavailable(Throwable error, boolean pullToRefresh) {
				super.onFirstUnavailable(error, pullToRefresh);
			}

			@Override public void onFirstEmpty(boolean pullToRefresh) {
				super.onFirstEmpty(pullToRefresh);
			}

			@Override public void onNextLoaded() {
				super.onNextLoaded();
			}

			@Override public void onNextUnavailable(Throwable error) {
				super.onNextUnavailable(error);
			}

			@Override public void onFinished() {
				super.onFinished();
			}

			@Override public void onPreLoadFirst(boolean pullToRefresh) {
				super.onPreLoadFirst(pullToRefresh);
			}

			@Override public void onPreLoadNext() {
				super.onPreLoadNext();
			}
		});
		adapter.start();
	}
}

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

		final InfinityAdapter<Number, ?> adapter = new SampleUserAdapter();
		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override public void onRefresh() {
				adapter.restart(true);
			}
		});
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		adapter.setLimit(5);
		adapter.setFiller(new InfinityFiller<Number>() {
			@Override public void onLoad(final int limit, final int offset, final InfinityFiller.Callback<Number> callback) {
				DataManager.get().getDataObservable(limit, offset)
						.subscribe(new Action1<List<Number>>() {
							@Override public void call(List<Number> numbers) {
								callback.onData(numbers);
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
				refresh.setRefreshing(false);
			}

			@Override public void onFirstUnavailable(Throwable error, boolean pullToRefresh) {
				refresh.setRefreshing(false);
			}

			@Override public void onFirstEmpty(boolean pullToRefresh) {
				refresh.setRefreshing(false);
			}

			@Override public void onNextLoaded() { }

			@Override public void onNextUnavailable(Throwable error) { }

			@Override public void onFinished() { }

			@Override public void onPreLoadFirst(boolean pullToRefresh) { }

			@Override public void onPreLoadNext() { }
		});
		adapter.start();
	}
}

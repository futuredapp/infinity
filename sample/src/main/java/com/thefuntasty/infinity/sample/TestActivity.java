package com.thefuntasty.infinity.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.thefuntasty.infinity.InfinityFiller;

import java.util.List;

import rx.functions.Action1;

public class TestActivity extends AppCompatActivity {

	RecyclerView recyclerView;
	SwipeRefreshLayout refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		recyclerView = (RecyclerView) findViewById(R.id.recycler);
		refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);

		final SampleUserAdapter adapter = App.get().getConfig().getAdapter();
		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override public void onRefresh() {
				adapter.restart(true);
			}
		});
		recyclerView.setLayoutManager(App.get().getConfig().getLayoutManager());
		recyclerView.setAdapter(adapter);

		adapter.setLimit(10);
		adapter.setFiller(new InfinityFiller<User>() {
			@Override public void onLoad(final int limit, final int offset, final InfinityFiller.Callback<User> callback) {
				App.get().getConfig().getDataObservable(limit, offset)
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
		adapter.setEventListener(App.get().getConfig().getEventListener(refresh));
		adapter.start();
	}

	public static Intent getStarterIntent(Context context) {
		return new Intent(context, TestActivity.class);
	}

	// TODO dole error nascrollovat nahoru a dát pull to refresh, scroll dolů
}

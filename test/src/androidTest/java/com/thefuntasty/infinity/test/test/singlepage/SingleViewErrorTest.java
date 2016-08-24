package com.thefuntasty.infinity.test.test.singlepage;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.SwipeRefreshLayout;

import com.thefuntasty.infinity.InfinityEventListener;
import com.thefuntasty.infinity.test.App;
import com.thefuntasty.infinity.test.Config;
import com.thefuntasty.infinity.test.TestActivity;
import com.thefuntasty.infinity.test.User;
import com.thefuntasty.infinity.test.adapter.SingleViewAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class SingleViewErrorTest {

	public final IntentsTestRule<TestActivity> activityRule = new IntentsTestRule<>(TestActivity.class, false, false);
	private App app;

	@Before
	public void before() {
		app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
	}

	@Test
	public void singlePageSingleViewErrorTest() throws InterruptedException {
		Config configMock = spy(Config.class);
		InfinityEventListener eventListener = mock(InfinityEventListener.class);
		SingleViewAdapter adapter = new SingleViewAdapter();
		final IOException exception = new IOException("no host");

		Observable<List<User>> observable = Observable.just(Collections.<User>emptyList())
				.delay(2, TimeUnit.SECONDS)
				.map(new Func1<List<User>, List<User>>() {
					@Override public List<User> call(List<User> users) {
						throw Exceptions.propagate(exception);
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io());

		when(configMock.getEventListener(any(SwipeRefreshLayout.class))).thenReturn(eventListener);
		when(configMock.getAdapter()).thenReturn(adapter);
		when(configMock.getLimit()).thenReturn(10);
		when(configMock.getDataObservable(10, 0)).thenReturn(observable);

		app.setConfig(configMock);
		launchActivity();

		// necessary to make "Loading" item hide
		onView(ViewMatchers.withId(com.thefuntasty.infinity.test.R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(0));
		verify(eventListener).onPreLoadFirst(false);
		verify(eventListener).onFirstUnavailable(any(Throwable.class), eq(false));
		// TODO
//		Thread.sleep(500);
//		onView(withText("Loading")).check(doesNotExist());

	}

	@After
	public void after() {
		Intents.release();
	}

	private void launchActivity() {
		activityRule.launchActivity(TestActivity.getStarterIntent(InstrumentationRegistry.getTargetContext()));
	}
}

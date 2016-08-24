package com.thefuntasty.infinity.test.test.multiplepage;

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
import com.thefuntasty.infinity.test.adapter.MultipleViewsAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MultipleViewTest {

	public final IntentsTestRule<TestActivity> activityRule = new IntentsTestRule<>(TestActivity.class, false, false);
	private App app;

	@Before
	public void before() {
		app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
	}

	@Test
	public void multiplePagesMultipleViewsTest() throws InterruptedException {
		Config configMock = spy(Config.class);
		InfinityEventListener eventListener = mock(InfinityEventListener.class);
		MultipleViewsAdapter adapter = new MultipleViewsAdapter();

		when(configMock.getEventListener(any(SwipeRefreshLayout.class))).thenReturn(eventListener);
		when(configMock.getAdapter()).thenReturn(adapter);

		app.setConfig(configMock);
		launchActivity();

		onView(ViewMatchers.withId(com.thefuntasty.infinity.test.R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(10));
		onView(ViewMatchers.withId(com.thefuntasty.infinity.test.R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(20));
		// necessary to make "Loading" item hide
		onView(ViewMatchers.withId(com.thefuntasty.infinity.test.R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(19));
		onView(withText("Tomas 19R")).check(matches(isDisplayed()));
		onView(withText("Stanislav 18L")).check(matches(isDisplayed()));
		verify(eventListener).onPreLoadFirst(false);
		verify(eventListener).onFirstLoaded(false);
		verify(eventListener, times(2)).onPreLoadNext();
		verify(eventListener, times(2)).onNextLoaded();
		verify(eventListener).onFinished();
		Thread.sleep(500);
		onView(withText("Loading")).check(doesNotExist());

	}

	@After
	public void after() {
		Intents.release();
	}

	private void launchActivity() {
		activityRule.launchActivity(TestActivity.getStarterIntent(InstrumentationRegistry.getTargetContext()));
	}
}

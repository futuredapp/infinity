package com.thefuntasty.infinity.sample;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.SwipeRefreshLayout;

import com.thefuntasty.infinity.InfinityEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TestActivityTest {

	public final IntentsTestRule<TestActivity> activityRule = new IntentsTestRule<>(TestActivity.class, false, false);
	private App app;

	@Before
	public void before() {
		app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
	}

	@Test
	public void test() throws InterruptedException {
		Config configMock = spy(Config.class);
		InfinityEventListener eventListener = mock(InfinityEventListener.class);
		SampleTestUserAdapter adapter = new SampleTestUserAdapter();

		when(configMock.getEventListener(any(SwipeRefreshLayout.class))).thenReturn(eventListener);
		when(configMock.getAdapter()).thenReturn(adapter);

		app.setConfig(configMock);
		launchActivity();

		onView(withText("Im header2")).check(matches(isDisplayed()));
		onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(10));
		onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(23));
		onView(withText("Tomáš 19")).check(matches(isDisplayed()));
		verify(eventListener).onFinished();
		Thread.sleep(5000);
		onView(withText("Loading")).check(doesNotExist());
	}

	private void launchActivity() {
		activityRule.launchActivity(TestActivity.getStarterIntent(InstrumentationRegistry.getTargetContext()));
	}
}

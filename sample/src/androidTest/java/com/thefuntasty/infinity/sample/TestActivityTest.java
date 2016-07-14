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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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

		when(configMock.getEventListener(any(SwipeRefreshLayout.class))).thenReturn(eventListener);

		app.setConfig(configMock);
		launchActivity();

		onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(20));
		Thread.sleep(3000);
		onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(30));
		Thread.sleep(3000);
		verify(eventListener).onFinished();
	}

	private void launchActivity() {
		activityRule.launchActivity(TestActivity.getStarterIntent(InstrumentationRegistry.getTargetContext()));
	}
}

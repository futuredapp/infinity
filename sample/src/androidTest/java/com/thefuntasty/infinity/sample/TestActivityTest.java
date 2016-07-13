package com.thefuntasty.infinity.sample;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TestActivityTest {

	public final IntentsTestRule<TestActivity> activityRule = new IntentsTestRule<>(TestActivity.class, false, false);

	@Before
	public void before() {

	}

	@Test
	public void test() {
		launchActivity();

		onView(withText("Text")).check(matches(isDisplayed()));
	}

	private void launchActivity() {
		activityRule.launchActivity(TestActivity.getStarterIntent(InstrumentationRegistry.getTargetContext()));
	}
}

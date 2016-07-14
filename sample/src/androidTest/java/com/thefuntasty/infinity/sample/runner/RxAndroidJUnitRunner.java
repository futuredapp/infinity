package com.thefuntasty.infinity.sample.runner;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;

import com.thefuntasty.infinity.sample.util.RxIdlingResource;

import rx.plugins.RxJavaPlugins;

/**
 * Runner that registers a Espresso Idling resource that handles waiting for
 * RxJava Observables to finish.
 * WARNING - Using this runner will block the tests if the application uses long-lived hot
 * Observables such us event buses, etc.
 */
public class RxAndroidJUnitRunner extends AndroidJUnitRunner {

	@Override
	public void onCreate(Bundle arguments) {
		RxJavaPlugins.getInstance().registerObservableExecutionHook(RxIdlingResource.get());
		super.onCreate(arguments);
	}
}

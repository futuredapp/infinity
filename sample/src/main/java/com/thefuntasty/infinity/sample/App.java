package com.thefuntasty.infinity.sample;

import android.app.Application;

public class App extends Application {

	private static Config config;
	private static App app;

	@Override public void onCreate() {
		super.onCreate();
		app = this;
	}

	public static Config getConfig() {
		if (config == null) {
			config = new Config(app.getApplicationContext());
		}
		return config;
	}

	public static void setConfig(Config config) {
		App.config = config;
	}
}

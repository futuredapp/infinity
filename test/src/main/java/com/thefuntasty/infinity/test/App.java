package com.thefuntasty.infinity.test;

import android.app.Application;

public class App extends Application {

	private Config config;

	private static App app;

	@Override public void onCreate() {
		super.onCreate();
		app = this;
	}

	public Config getConfig() {
		if (config == null) {
			config = new Config(app.getApplicationContext());
		}
		return config;
	}

	public static App get() {
		return app;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}

package com.thefuntasty.infinity.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataManager {

	private static DataManager dataManager;

	private DataManager() {
	}

	public static DataManager get() {
		if (dataManager == null) {
			dataManager = new DataManager();
		}

		return dataManager;
	}

	private List<Number> getData() {
		final ArrayList<Number> data = new ArrayList<>(20);
		data.add(new Number(0));
		data.add(new Number(1));
		data.add(new Number(2));
		data.add(new Number(3));
		data.add(new Number(4));
		data.add(new Number(5));
		data.add(new Number(6));
		data.add(new Number(7));
		data.add(new Number(8));
		data.add(new Number(9));
		data.add(new Number(10));
		data.add(new Number(11));
		data.add(new Number(12));
		data.add(new Number(13));
		data.add(new Number(14));
		data.add(new Number(15));
		data.add(new Number(16));
		data.add(new Number(17));
		data.add(new Number(18));
		data.add(new Number(19));

		return data;
	}

	public Observable<List<Number>> getDataObservable(final int limit, final int offset) {
		List<Number> data = getData();

		if (offset >= data.size()) {
			return Observable.just(Collections.<Number>emptyList())
					.delay(3, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io());
		}

		Observable<Number> observable = Observable.from(data);
		final Random random = new Random();

		return observable
				.skip(offset)
				.take(limit)
				.buffer(limit)
				.delay(3, TimeUnit.SECONDS)
				.map(new Func1<List<Number>, List<Number>>() {
					@Override public List<Number> call(List<Number> numbers) {
						if (random.nextBoolean()) {
							throw new RuntimeException("Network error");
						}
						return numbers;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io());
	}
}

package com.thefuntasty.infinity.sample;

import java.util.ArrayList;
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
		final ArrayList<Number> data = new ArrayList<>(100);
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
		data.add(new Number(20));
		data.add(new Number(21));
		data.add(new Number(22));
		data.add(new Number(23));
		data.add(new Number(24));
		data.add(new Number(25));
		data.add(new Number(26));
		data.add(new Number(27));
		data.add(new Number(28));
		data.add(new Number(29));
		data.add(new Number(30));
		data.add(new Number(31));
		data.add(new Number(32));
		data.add(new Number(33));
		data.add(new Number(34));
		data.add(new Number(35));
		data.add(new Number(36));
		data.add(new Number(37));
		data.add(new Number(38));
		data.add(new Number(39));
		data.add(new Number(40));
		data.add(new Number(41));
		data.add(new Number(42));
		data.add(new Number(43));
		data.add(new Number(44));
		data.add(new Number(45));
		data.add(new Number(46));
		data.add(new Number(47));
		data.add(new Number(48));
		data.add(new Number(49));

		return data;
	}

	public Observable<List<Number>> getDataObservable(final int limit, final int offset) {
		Observable<Number> observable = Observable.from(getData());

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

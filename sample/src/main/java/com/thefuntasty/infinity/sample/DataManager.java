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
		data.add(new Number(50));
		data.add(new Number(51));
		data.add(new Number(52));
		data.add(new Number(53));
		data.add(new Number(54));
		data.add(new Number(55));
		data.add(new Number(56));
		data.add(new Number(57));
		data.add(new Number(58));
		data.add(new Number(59));
		data.add(new Number(60));
		data.add(new Number(61));
		data.add(new Number(62));
		data.add(new Number(63));
		data.add(new Number(64));
		data.add(new Number(65));
		data.add(new Number(66));
		data.add(new Number(67));
		data.add(new Number(68));
		data.add(new Number(69));
		data.add(new Number(70));
		data.add(new Number(71));
		data.add(new Number(72));
		data.add(new Number(73));
		data.add(new Number(74));
		data.add(new Number(75));
		data.add(new Number(76));
		data.add(new Number(77));
		data.add(new Number(78));
		data.add(new Number(79));
		data.add(new Number(80));
		data.add(new Number(81));
		data.add(new Number(82));
		data.add(new Number(83));
		data.add(new Number(84));
		data.add(new Number(85));
		data.add(new Number(86));
		data.add(new Number(87));
		data.add(new Number(88));
		data.add(new Number(89));
		data.add(new Number(90));
		data.add(new Number(91));
		data.add(new Number(92));
		data.add(new Number(93));
		data.add(new Number(94));
		data.add(new Number(95));
		data.add(new Number(96));
		data.add(new Number(97));
		data.add(new Number(98));
		data.add(new Number(99));

		return data;
	}

	public Observable<List<Number>> getDataObservable(final int limit, final int offset) {
		Observable<Number> observable = Observable.from(getData());

		return observable
				.skip(offset)
				.take(limit)
				.buffer(limit)
				.delay(3, TimeUnit.SECONDS)
				.map(new Func1<List<Number>, List<Number>>() {
					@Override public List<Number> call(List<Number> numbers) {
						Random random = new Random();
						if (random.nextBoolean()) {
							throw new RuntimeException("a");
						}
						return numbers;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io());
	}
}

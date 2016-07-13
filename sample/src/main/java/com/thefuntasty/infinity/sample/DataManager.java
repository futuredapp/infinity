package com.thefuntasty.infinity.sample;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

	private static DataManager dataManager;

	private DataManager() { }

	public static DataManager get() {
		if (dataManager == null) {
			dataManager = new DataManager();
		}

		return dataManager;
	}

	public List<User> getData() {
		final ArrayList<User> data = new ArrayList<>(20);
		data.add(new User("Alice", "0"));
		data.add(new User("Bruno", "1"));
		data.add(new User("Cecil", "2"));
		data.add(new User("David", "3"));
		data.add(new User("Emil", "4"));
		data.add(new User("František", "5"));
		data.add(new User("Gertruda", "6"));
		data.add(new User("Honza", "7"));
		data.add(new User("Ivo", "8"));
		data.add(new User("Jan", "9"));
		data.add(new User("Kamil", "10"));
		data.add(new User("Lukáš", "11"));
		data.add(new User("Martin", "12"));
		data.add(new User("Norbert", "13"));
		data.add(new User("Otto", "14"));
		data.add(new User("Petr", "15"));
		data.add(new User("Quido", "16"));
		data.add(new User("Radek", "17"));
		data.add(new User("Stanislav", "18"));
		data.add(new User("Tomáš", "19"));

		return data;
	}
}

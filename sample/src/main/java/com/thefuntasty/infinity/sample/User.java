package com.thefuntasty.infinity.sample;

public class User {
	public String name;
	public String surname;

	public User(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}

	@Override public String toString() {
		return name + " " + surname;
	}
}

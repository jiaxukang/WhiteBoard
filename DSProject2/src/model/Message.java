package model;

import java.io.Serializable;

public class Message implements Serializable {
	private final String name;
	private final String text;

	public Message(String userName, String text) {
		this.name = userName;
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

}

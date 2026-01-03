package com.fumbbl.ffb;

public enum KeywordChoiceMode implements INamedObject {

	GETTING_EVEN("gettingEven", "Select player type to get even with for", "Getting Even", "Waiting for coach to choose player type.");

	private final String name, header, title, message;

	KeywordChoiceMode(String pName, String header, String title, String message) {
		name = pName;
		this.header = header;
		this.title = title;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getDialogHeader(String playerName) {
		return header + " " + playerName;
	}

	public String getStatusTitle() {
		return title;
	}

	public String getStatusMessage() {
		return message;
	}

}

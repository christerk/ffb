package com.fumbbl.ffb;

public enum PositionChoiceMode implements INamedObject {

	RAISE_DEAD("raiseDead", "Select position for raised player", "Raise Dead",
		"Waiting for coach to choose position.");

	private final String name, header, title, message;

	PositionChoiceMode(String pName, String header, String title, String message) {
		name = pName;
		this.header = header;
		this.title = title;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getDialogHeader() {
		return header;
	}

	public String getStatusTitle() {
		return title;
	}

	public String getStatusMessage() {
		return message;
	}

}

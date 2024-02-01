package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public enum SkillChoiceMode implements INamedObject {

	INTENSIVE_TRAINING("intensiveTraining", "Select a primary skill for ", "Intensive Training", "Waiting for coach to choose Skill."),
	WISDOM_OF_THE_WHITE_DWARF("wisdomOfTheWhiteDwarf", "Select a skill for", "Wisdom of the White Dwarf", "Waiting for coach to choose Skill.");

	private final String name, header, title, message;

	SkillChoiceMode(String pName, String header, String title, String message) {
		name = pName;
		this.header = header;
		this.title = title;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getDialogHeader(String playerName) {
		return header + playerName;
	}

	public String getStatusTitle() {
		return title;
	}

	public String getStatusMessage() {
		return message;
	}

}

package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public enum SkillChoiceMode implements INamedObject {

	INTENSIVE_TRAINING("intensiveTraining");

	private final String name;

	SkillChoiceMode(String pName) {
		name = pName;
	}

	public String getName() {
		return name;
	}

	public String getDialogHeader(String playerName) {
		StringBuilder header = new StringBuilder();
		switch (this) {
			case INTENSIVE_TRAINING:
				header.append("Select a primary skill for ").append(playerName);
				break;
			default:
				break;
		}
		return header.toString();
	}

	public String getStatusTitle() {
		StringBuilder title = new StringBuilder();
		switch (this) {
			case INTENSIVE_TRAINING:
				title.append("Intensive Training");
				break;
			default:
				break;
		}
		return title.toString();
	}

	public String getStatusMessage() {
		StringBuilder message = new StringBuilder();
		switch (this) {
			case INTENSIVE_TRAINING:
				message.append("Waiting for coach to choose Skill.");
				break;
			default:
				break;
		}
		return message.toString();
	}

}

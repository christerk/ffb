package com.balancedbytes.games.ffb.mechanics;

public class Wording {
	private String noun;
	private String verb;
	private String inflection;
	private String playerCharacterization;

	public Wording(String noun, String verb, String inflection, String playerCharacterization) {
		this.noun = noun;
		this.verb = verb;
		this.inflection = inflection;
		this.playerCharacterization = playerCharacterization;
	}

	public String getNoun() {
		return noun;
	}

	public String getVerb() {
		return verb;
	}

	public String getInflection() {
		return inflection;
	}

	public String getPlayerCharacterization() {
		return playerCharacterization;
	}
}

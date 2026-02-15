package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.skill.Skill;

public class PrayerDialogSelection {
	private final String playerId;
	private final Skill skill;

	public PrayerDialogSelection(String playerId, Skill skill) {
		this.playerId = playerId;
		this.skill = skill;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Skill getSkill() {
		return skill;
	}
}

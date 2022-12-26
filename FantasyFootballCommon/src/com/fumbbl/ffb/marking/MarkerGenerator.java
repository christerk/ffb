package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Arrays;
import java.util.HashSet;

public class MarkerGenerator {

	public String generate(Player<?> player, AutoMarkingConfig config, boolean playsForMarkingCoach) {
		StringBuilder marking = new StringBuilder();

		HashSet<Skill> skills = new HashSet<>(Arrays.asList(player.getSkills()));
		config.getMarkings().stream()
			.filter(entry -> skills.containsAll(entry.getSkills()))
			.map(AutoMarkingRecord::getMarking)
			.forEach(marking::append);

		return marking.toString();
	}
}

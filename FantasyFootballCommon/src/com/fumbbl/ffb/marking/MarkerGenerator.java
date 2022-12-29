package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MarkerGenerator {

	public String generate(Player<?> player, AutoMarkingConfig config, boolean playsForMarkingCoach) {

		List<Skill> baseSkills = new ArrayList<>(Arrays.asList(player.getPosition().getSkills()));
		List<Skill> gainedSkills = new ArrayList<>(Arrays.asList(player.getSkills()));
		gainedSkills.removeAll(baseSkills);
		List<InjuryAttribute> injuries = Arrays.stream(player.getLastingInjuries()).map(SeriousInjury::getInjuryAttribute).collect(Collectors.toList());

		StringBuilder marking = new StringBuilder();

		config.getMarkings().stream()
			.filter(markingRecord -> appliesTo(markingRecord.getApplyTo(), playsForMarkingCoach))
			.collect(Collectors.groupingBy(markingRecord -> markingRecord.getSkills().isEmpty()))
			.entrySet().stream()
			.sorted((entry1, entry2) -> entry1.getKey() == entry2.getKey() ? 0 : entry1.getKey() ? 1 : -1)
			.forEach(
				entry -> entry.getValue().stream()
					.sorted(Comparator.comparingInt(
							(AutoMarkingRecord record) ->
								record.getSkills().size())
						.thenComparingInt(
							record -> record.getInjuries().size())
						.thenComparing(
							AutoMarkingRecord::getMarking))
					.map(markingRecord -> getMarking(markingRecord, baseSkills, gainedSkills, injuries)).forEach(marking::append));

		return marking.toString();
	}

	private boolean appliesTo(ApplyTo applyTo, boolean playsForMarkingCoach) {
		return playsForMarkingCoach && applyTo.isAppliesToOwn() || !playsForMarkingCoach && applyTo.isAppliesToOpponent();
	}

	private String getMarking(AutoMarkingRecord markingRecord, List<Skill> baseSkills, List<Skill> gainedSkills, List<InjuryAttribute> injuries) {

		List<Skill> skillsToCheck = new ArrayList<>(gainedSkills);

		if (!markingRecord.isGainedOnly()) {
			skillsToCheck.addAll(baseSkills);
		}

		//noinspection SlowListContainsAll
		if (skillsToCheck.containsAll(markingRecord.getSkills()) && injuries.containsAll(markingRecord.getInjuries())) {
			markingRecord.getInjuries().forEach(injuries::remove);

			markingRecord.getSkills().forEach(skillToRemove -> {

				if (!gainedSkills.remove(skillToRemove)) {
					baseSkills.remove(skillToRemove);
				}

			});

			return markingRecord.getMarking();
		}

		return "";
	}
}

package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MarkerGenerator {

	public String generate(Player<?> player, AutoMarkingConfig config, boolean playsForMarkingCoach) {

		List<Skill> baseSkills = new ArrayList<>(Arrays.asList(player.getPosition().getSkills()));
		List<Skill> gainedSkills = new ArrayList<>(Arrays.asList(player.getSkills()));
		gainedSkills.removeAll(baseSkills);
		List<InjuryAttribute> injuries = Arrays.stream(player.getLastingInjuries()).map(SeriousInjury::getInjuryAttribute).collect(Collectors.toList());

		StringBuilder marking = new StringBuilder();

		Set<AutoMarkingRecord> markingsToApply = new HashSet<>();

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
							record -> record.getInjuries().size()).reversed()
						.thenComparing(
							AutoMarkingRecord::getMarking))
					.map(markingRecord -> getMarking(markingRecord, baseSkills, gainedSkills, injuries, markingsToApply)).sorted().forEach(marking::append));

		return marking.toString();
	}

	private boolean appliesTo(ApplyTo applyTo, boolean playsForMarkingCoach) {
		return playsForMarkingCoach && applyTo.isAppliesToOwn() || !playsForMarkingCoach && applyTo.isAppliesToOpponent();
	}

	private String getMarking(AutoMarkingRecord markingRecord, List<Skill> baseSkills, List<Skill> gainedSkills, List<InjuryAttribute> injuries, Set<AutoMarkingRecord> markingsToApply) {

		if (providesNewInformation(markingRecord, markingsToApply)) {

			List<Skill> skillsToCheck = new ArrayList<>(gainedSkills);
			if (!markingRecord.isGainedOnly()) {
				skillsToCheck.addAll(baseSkills);
			}

			if (isSubSetWithDuplicates(markingRecord.getSkills(), skillsToCheck) && isSubSetWithDuplicates(markingRecord.getInjuries(), injuries)) {
				markingsToApply.add(markingRecord);
				return markingRecord.getMarking();
			}
		}
		return "";
	}

	public <T> boolean isSubSetWithDuplicates(List<T> subSet, List<T> superSet) {
		Map<Integer, List<T>> subGroups = subSet.stream().collect(Collectors.groupingBy(Object::hashCode));
		Map<Integer, List<T>> superGroups = superSet.stream().collect(Collectors.groupingBy(Object::hashCode));

		return subGroups.entrySet().stream().allMatch(entry -> {
			List<T> superElements = superGroups.get(entry.getKey());
			return superElements != null && superElements.size() >= entry.getValue().size();
		});
	}

	private boolean providesNewInformation(AutoMarkingRecord markingRecord, Set<AutoMarkingRecord> markingsToApply) {

		Optional<AutoMarkingRecord> superSet = markingsToApply.stream().filter(markingRecord::isSubSetOf).findFirst();

		if (superSet.isPresent()) {
			ApplyTo superApplyTo = superSet.get().getApplyTo();
			return superApplyTo != ApplyTo.BOTH && superApplyTo != markingRecord.getApplyTo() && !markingRecord.isGainedOnly() && superSet.get().isGainedOnly();
		}

		return true;
	}
}

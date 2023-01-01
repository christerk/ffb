package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MarkerGenerator {

	public String generate(Player<?> player, AutoMarkingConfig config, boolean playsForMarkingCoach) {

		List<Skill> baseSkills = new ArrayList<>(Arrays.asList(player.getPosition().getSkills()));
		List<Skill> gainedSkills = new ArrayList<>(Arrays.asList(player.getSkills()));
		gainedSkills.removeAll(baseSkills);
		List<InjuryAttribute> injuries = Arrays.stream(player.getLastingInjuries()).map(SeriousInjury::getInjuryAttribute).collect(Collectors.toList());

		removeNegatingPairs(gainedSkills, injuries);

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
						.thenComparing((record1, record2) -> {
							if (record1.getApplyTo() == record2.getApplyTo()) {
								return 0;
							}
							if (record1.getApplyTo() == ApplyTo.BOTH) {
								return -1;
							}

							if (record2.getApplyTo() == ApplyTo.BOTH) {
								return 1;
							}

							if (record1.getApplyTo() == ApplyTo.OWN) {
								return -1;
							}

							return 1;
						})
						.thenComparing(AutoMarkingRecord::isGainedOnly)
						.thenComparing((o1, o2) -> o1.isApplyRepeatedly() == o2.isApplyRepeatedly() ? 0 : o1.isApplyRepeatedly() ? -1 : 1)
						.thenComparing(
							AutoMarkingRecord::getMarking))
					.map(markingRecord -> getMarking(markingRecord, baseSkills, gainedSkills, injuries, markingsToApply)).sorted().forEach(marking::append));

		return marking.toString();
	}

	private boolean appliesTo(ApplyTo applyTo, boolean playsForMarkingCoach) {
		return playsForMarkingCoach && applyTo.isAppliesToOwn() || !playsForMarkingCoach && applyTo.isAppliesToOpponent();
	}

	private String getMarking(AutoMarkingRecord markingRecord, List<Skill> baseSkills, List<Skill> gainedSkills, List<InjuryAttribute> injuries, Set<AutoMarkingRecord> markingsToApply) {

		if (markingsToApply.stream().noneMatch(markingRecord::isSubSetOf)) {

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

	private void removeNegatingPairs(List<Skill> skills, List<InjuryAttribute> injuries) {

		new HashSet<>(skills).stream().map(skill -> new Pair<>(skill, InjuryAttribute.forSkill(skill)))
			.filter(pair -> pair.getValue() != null)
			.forEach(
				pair -> injuries.stream().filter(injury -> injury == pair.getValue()).findFirst().ifPresent(injury -> {
					injuries.remove(injury);
					skills.remove(pair.getKey());
				})
			);
	}

	private <T> boolean isSubSetWithDuplicates(List<T> subSet, List<T> superSet) {
		Map<Integer, List<T>> subGroups = subSet.stream().collect(Collectors.groupingBy(Object::hashCode));
		Map<Integer, List<T>> superGroups = superSet.stream().collect(Collectors.groupingBy(Object::hashCode));

		return subGroups.entrySet().stream().allMatch(entry -> {
			List<T> superElements = superGroups.get(entry.getKey());
			return superElements != null && superElements.size() >= entry.getValue().size();
		});
	}
}

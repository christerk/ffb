package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.StringTool;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MarkerGenerator {

	public String generate(Player<?> player, AutoMarkingConfig config, boolean playsForMarkingCoach) {

		List<Skill> baseSkills = new ArrayList<>(Arrays.asList(player.getPosition().getSkills()));
		List<Skill> gainedSkills = new ArrayList<>(player.getSkillsIncludingTemporaryOnes());
		gainedSkills.removeAll(baseSkills);
		List<InjuryAttribute> injuries = Arrays.stream(player.getLastingInjuries()).map(SeriousInjury::getInjuryAttribute).collect(Collectors.toList());

		removeNegatingPairs(gainedSkills, injuries);

		Set<AutoMarkingRecord> markingsToApply = new HashSet<>();

		String separator = config.getSeparator();

		String finalSeparator = separator == null ? "" : separator;
		return config.getMarkings().stream()
			.filter(markingRecord -> appliesTo(markingRecord.getApplyTo(), playsForMarkingCoach))
			.collect(Collectors.groupingBy(markingRecord -> markingRecord.getSkills().isEmpty()))
			.entrySet().stream()
			.sorted((entry1, entry2) -> entry1.getKey() == entry2.getKey() ? 0 : entry1.getKey() ? 1 : -1)
			.flatMap(
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
					.map(markingRecord -> getMarking(markingRecord, baseSkills, gainedSkills, injuries, markingsToApply, finalSeparator))
					.sorted()).filter(StringTool::isProvided).collect(Collectors.joining(finalSeparator));


	}

	private boolean appliesTo(ApplyTo applyTo, boolean playsForMarkingCoach) {
		return playsForMarkingCoach && applyTo.isAppliesToOwn() || !playsForMarkingCoach && applyTo.isAppliesToOpponent();
	}

	private String getMarking(AutoMarkingRecord markingRecord, List<Skill> baseSkills, List<Skill> gainedSkills,
														List<InjuryAttribute> injuries, Set<AutoMarkingRecord> markingsToApply, String separator) {

		List<String> marking = new ArrayList<>();

		if (markingsToApply.stream().noneMatch(markingRecord::isSubSetOf)) {

			List<Skill> skillsToCheck = new ArrayList<>(gainedSkills);
			if (!markingRecord.isGainedOnly()) {
				skillsToCheck.addAll(baseSkills);
			}

			int matches = findMin(isSubSetWithDuplicates(markingRecord.getSkills(), skillsToCheck), isSubSetWithDuplicates(markingRecord.getInjuries(), injuries));

			if (!markingRecord.isApplyRepeatedly()) {
				matches = Math.min(1, matches);
			}

			for (int counter = 0; counter < matches; counter++) {
				markingsToApply.add(markingRecord);
				marking.add(markingRecord.getMarking());
			}
		}
		return String.join(separator, marking);
	}

	private int findMin(int first, int second) {
		int result = Math.min(first, second);

		return result == Integer.MAX_VALUE ? 0 : result;
	}

	private void removeNegatingPairs(List<Skill> skills, List<InjuryAttribute> injuries) {

		new HashSet<>(skills).stream().filter(Objects::nonNull).map(skill -> new Pair<>(skill, InjuryAttribute.forSkill(skill)))
			.filter(pair -> pair.getValue() != null)
			.forEach(
				pair -> injuries.stream().filter(injury -> injury == pair.getValue()).findFirst().ifPresent(injury -> {
					injuries.remove(injury);
					skills.remove(pair.getKey());
				})
			);
	}

	private <T> int isSubSetWithDuplicates(List<T> subSet, List<T> superSet) {

		if (subSet.isEmpty()) {
			return Integer.MAX_VALUE;
		}

		Map<Integer, List<T>> subGroups = subSet.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(Object::hashCode));
		Map<Integer, List<T>> superGroups = superSet.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(Object::hashCode));

		return subGroups.entrySet().stream().map(entry -> {
			List<T> superElements = superGroups.get(entry.getKey());
			if (superElements == null || superElements.isEmpty()) {
				return 0;
			}
			return superElements.size() / entry.getValue().size();
		}).min(Comparator.naturalOrder()).orElse(0);
	}
}

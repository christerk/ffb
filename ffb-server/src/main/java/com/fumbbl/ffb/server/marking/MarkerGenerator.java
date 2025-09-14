package com.fumbbl.ffb.server.marking;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.marking.SortMode;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MarkerGenerator {

	public String generate(Game game, Player<?> player, AutoMarkingConfig config, boolean playsForMarkingCoach) {

		List<Skill> baseSkills = new ArrayList<>(Arrays.asList(player.getPosition().getSkills()));
		List<Skill> gainedSkills = player.getSkillsIncludingTemporaryOnes().stream().filter(skill -> skill.getCategory() != SkillCategory.STAT_INCREASE).collect(Collectors.toList());
		gainedSkills.removeAll(baseSkills);
		List<InjuryAttribute> injuriesAttributes = new ArrayList<>();

		for (PlayerStatKey key : PlayerStatKey.values()) {
			int statDiff = statDiff(game, key, player);

			if (statDiff > 0) {
				SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
				Skill statIncrease = factory.forStatKey(key);
				for (int i = 0; i < statDiff; i++) {
					gainedSkills.add(statIncrease);
				}
			} else if (statDiff < 0) {
				InjuryAttribute injury = InjuryAttribute.forStatKey(key);
				for (int i = 0; i < Math.abs(statDiff); i++) {
					injuriesAttributes.add(injury);
				}
			}
		}

		List<SeriousInjury> injuries = new ArrayList<>();
		injuries.add(game.getGameResult().getPlayerResult(player).getSeriousInjury());
		injuries.add(game.getGameResult().getPlayerResult(player).getSeriousInjuryDecay());
		injuries.addAll(Arrays.asList(player.getLastingInjuries()));
		injuriesAttributes.addAll(injuries.stream().filter(Objects::nonNull).map(SeriousInjury::getInjuryAttribute).filter(inj -> inj == InjuryAttribute.NI).collect(Collectors.toList()));

		List<AutoMarkingRecord> recordsToApply = new ArrayList<>();

		String separator = config.getSeparator();

		String finalSeparator = separator == null ? "" : separator;
		List<AutoMarkingRecord> records = config.getMarkings().stream()
			.filter(markingRecord -> appliesTo(markingRecord.getApplyTo(), playsForMarkingCoach)).collect(Collectors.toList());

		if (config.getSortMode() == SortMode.NONE) {
			records.forEach(markingRecord -> populateMarkingRecords(markingRecord, baseSkills, gainedSkills, injuriesAttributes, recordsToApply));
			return recordsToApply.stream().map(AutoMarkingRecord::getMarking).filter(StringTool::isProvided).collect(Collectors.joining(finalSeparator));
		} else {
			populateAndSortRecords(records, baseSkills, gainedSkills, injuriesAttributes, recordsToApply);

			return recordsToApply.stream()
				.sorted(Comparator.comparingInt((AutoMarkingRecord record) -> record.isInjuryOnly() ? 1 : 0).thenComparing(AutoMarkingRecord::getMarking))
				.map(AutoMarkingRecord::getMarking).filter(StringTool::isProvided).collect(Collectors.joining(finalSeparator));
		}

	}

	private void populateAndSortRecords(List<AutoMarkingRecord> records, List<Skill> baseSkills, List<Skill> gainedSkills, List<InjuryAttribute> injuriesAttributes, List<AutoMarkingRecord> recordsToApply) {
		records.stream()
			.collect(Collectors.groupingBy(AutoMarkingRecord::isInjuryOnly))
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
					.forEach(markingRecord -> populateMarkingRecords(markingRecord, baseSkills, gainedSkills, injuriesAttributes, recordsToApply)));

	}

	private int statDiff(Game game, PlayerStatKey key, Player<?> player) {
		StatsMechanic mechanic = (StatsMechanic) game.getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.STAT.name());

		switch (key) {
			case MA:
				return player.getMovementWithModifiers(game) - player.getPosition().getMovement();
			case ST:
				return player.getStrengthWithModifiers(game) - player.getPosition().getStrength();
			case AG:
				boolean higherIsBetter = mechanic.improvementIncreasesValue();
				if (higherIsBetter) {
					return player.getAgilityWithModifiers(game) - player.getPosition().getAgility();
				}
				return player.getPosition().getAgility() - player.getAgilityWithModifiers(game);
			case PA:
				if (mechanic.drawPassing()) {
					return player.getPosition().getPassing() - player.getPassingWithModifiers(game);
				}
				return 0;
			case AV:
				return player.getArmourWithModifiers(game) - player.getPosition().getArmour();
			default:
				return 0;
		}
	}

	private boolean appliesTo(ApplyTo applyTo, boolean playsForMarkingCoach) {
		return playsForMarkingCoach && applyTo.isAppliesToOwn() || !playsForMarkingCoach && applyTo.isAppliesToOpponent();
	}

	private void populateMarkingRecords(AutoMarkingRecord markingRecord, List<Skill> baseSkills, List<Skill> gainedSkills,
																			List<InjuryAttribute> injuries, List<AutoMarkingRecord> recordsToApply) {

		if (recordsToApply.stream().noneMatch(markingRecord::isSubSetOf)) {

			List<Skill> skillsToCheck = new ArrayList<>(gainedSkills);
			if (!markingRecord.isGainedOnly()) {
				skillsToCheck.addAll(baseSkills);
			}

			int matches = findMin(isSubSetWithDuplicates(markingRecord.getSkills(), skillsToCheck), isSubSetWithDuplicates(markingRecord.getInjuries(), injuries));

			if (!markingRecord.isApplyRepeatedly()) {
				matches = Math.min(1, matches);
			}

			if (matches > 0) {
				recordsToApply.removeIf(record -> record.isSubSetOf(markingRecord));
			}

			for (int counter = 0; counter < matches; counter++) {
				recordsToApply.add(markingRecord);
			}
		}
	}

	private int findMin(int first, int second) {
		int result = Math.min(first, second);

		return result == Integer.MAX_VALUE ? 0 : result;
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

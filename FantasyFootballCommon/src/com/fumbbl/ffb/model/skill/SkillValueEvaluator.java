package com.fumbbl.ffb.model.skill;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.util.StringTool;

public interface SkillValueEvaluator {
	Set<SkillDisplayInfo> info(Skill skill, Player<?> player);
	Integer intValue(Set<String> tempValues);
	Set<String> values(Skill skill, Player<?> player);

	SkillValueEvaluator DEFAULT = new SkillValueEvaluator() {
		@Override
		public Set<SkillDisplayInfo> info(Skill skill, Player<?> player) {
			SkillDisplayInfo.Category category;
			if (player.getPosition().hasSkill(skill)) {
				category = SkillDisplayInfo.Category.ROSTER;
			} else if (player.hasSkillExcludingTemporaryOnes(skill)) {
				category = SkillDisplayInfo.Category.PLAYER;
			} else {
				category = SkillDisplayInfo.Category.TEMPORARY;
			}

			return Collections.singleton(new SkillDisplayInfo(skill.getName(), category, skill));
		}

		@Override
		public Integer intValue(Set<String> tempValues) {
			return null;
		}

		@Override
		public Set<String> values(Skill skill, Player<?> player) {
			return Collections.emptySet();
		}
	};

	SkillValueEvaluator MODIFIER = new IntegerEvaluator() {
		@Override
		protected Optional<Integer> getRelevantValue(Set<Integer> values) {
			return values.stream().max(Integer::compareTo);
		}

		@Override
		protected String format(Skill skill, int intValue) {
			return skill.getName() + " (+" + intValue + ")";
		}
	};

	SkillValueEvaluator ROLL = new IntegerEvaluator() {
		@Override
		protected Optional<Integer> getRelevantValue(Set<Integer> values) {
			return values.stream().min(Integer::compareTo);
		}

		@Override
		protected String format(Skill skill, int intValue) {
			return skill.getName() + " (" + intValue + "+)";
		}
	};

	String ANIMOSITY_TO_ALL = "all";
	SkillValueEvaluator ANIMOSITY = new SkillValueEvaluator() {

		@Override
		public Set<SkillDisplayInfo> info(Skill skill, Player<?> player) {
			Roster roster = player.getPosition().getRoster();
			Set<String> skillValues = split(Optional.ofNullable(player.getSkillValueExcludingTemporaryOnes(skill)).orElse(ANIMOSITY_TO_ALL));
			Set<String> tempSkillValues = split(player.temporarySkillValues(skill).toArray(new String[0]));
			tempSkillValues.removeAll(skillValues);
			return Stream.concat(
				skillValues.stream().map(value -> new SkillDisplayInfo(format(value, roster), SkillDisplayInfo.Category.ROSTER, skill)),
				tempSkillValues.stream().map(value -> new SkillDisplayInfo(format(value, roster), SkillDisplayInfo.Category.TEMPORARY, skill))
			).collect(Collectors.toSet());
		}

		@Override
		public Integer intValue(Set<String> tempValues) {
			return null;
		}

		@Override
		public Set<String> values(Skill skill, Player<?> player) {
			Set<String> values = player.temporarySkillValues(skill);
			values.add(Optional.ofNullable(player.getSkillValueExcludingTemporaryOnes(skill)).orElse(ANIMOSITY_TO_ALL));
			return split(values.toArray(new String[0]));
		}

		private String format(String value, Roster roster) {
			return "Animosity (" + map(value, roster) + ")";
		}

		private String map(String key, Roster roster) {
			if (key.equalsIgnoreCase(ANIMOSITY_TO_ALL)) {
				return "all team-mates";
			}

			Optional<? extends Position> position = Arrays.stream(roster.getPositions())
				.filter(pos -> pos.getId().equalsIgnoreCase(key)).findFirst();
			if (position.isPresent()) {
				return StringTool.isProvided(position.get().getDisplayName()) ? position.get().getDisplayName() : position.get().getName();
			}

			return key;
		}

		private Set<String> split(String... values) {
			return Arrays.stream(values).flatMap(value -> Arrays.stream(value.split(";"))).collect(Collectors.toSet());
		}
	};

	abstract class IntegerEvaluator implements SkillValueEvaluator {
		@Override
		public Set<SkillDisplayInfo> info(Skill skill, Player<?> player) {
			int intValue = player.getSkillIntValue(skill);

			SkillDisplayInfo.Category category;

			Set<Integer> skillValues = map(player.getSkillValueExcludingTemporaryOnes(skill));
			if (player.hasSkillExcludingTemporaryOnes(skill) &&
				((skillValues.isEmpty() && intValue == skill.getDefaultSkillValue()) || skillValues.contains(intValue))) {
				category = player.getPosition().hasSkill(skill) ? SkillDisplayInfo.Category.ROSTER : SkillDisplayInfo.Category.PLAYER;
			} else {
				category = SkillDisplayInfo.Category.TEMPORARY;
			}

			return Collections.singleton(new SkillDisplayInfo(format(skill, intValue), category, skill));
		}

		@Override
		public Integer intValue(Set<String> values) {
			return getRelevantValue(map(values.toArray(new String[0]))).orElse(null);
		}

		@Override
		public Set<String> values(Skill skill, Player<?> player) {
			return Collections.emptySet();
		}

		private Set<Integer> map(String... values) {
			return Arrays.stream(values).filter(value -> StringTool.isProvided(value) && StringTool.isNumber(value))
				.map(Integer::valueOf).collect(Collectors.toSet());
		}

		protected abstract Optional<Integer> getRelevantValue(Set<Integer> values);

		protected abstract String format(Skill skill, int intValue);
	}
}

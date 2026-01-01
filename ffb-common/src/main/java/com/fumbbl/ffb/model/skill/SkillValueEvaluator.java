package com.fumbbl.ffb.model.skill;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.StringTool;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

			String name = skill.getName();
			if (intValue < 1) {
				return name;
			}
			return name + " (" + intValue + "+)";
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

			if (intValue == 0) {
				return Collections.singleton(new SkillDisplayInfo(skill.getName(), category, skill));
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

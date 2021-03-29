package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.StringTool;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SkillValueEvaluator {
	SkillDisplayInfo info(Skill skill, Player<?> player);
	Integer intValue(List<String> tempValues);

	SkillValueEvaluator DEFAULT = new SkillValueEvaluator() {
		@Override
		public SkillDisplayInfo info(Skill skill, Player<?> player) {
			SkillDisplayInfo.Category category;
			if (player.getPosition().hasSkill(skill)) {
				category = SkillDisplayInfo.Category.ROSTER;
			} else if (player.hasSkillExcludingTemporaryOnes(skill)) {
				category = SkillDisplayInfo.Category.PLAYER;
			} else {
				category = SkillDisplayInfo.Category.TEMPORARY;
			}

			return new SkillDisplayInfo(skill.getName(), category, skill);
		}

		@Override
		public Integer intValue(List<String> tempValues) {
			return null;
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

	abstract class IntegerEvaluator implements SkillValueEvaluator {
		@Override
		public SkillDisplayInfo info(Skill skill, Player<?> player) {
			int intValue = player.getSkillIntValue(skill);

			SkillDisplayInfo.Category category;
			if (map(player.getSkillValueExcludingTemporaryOnes(skill)).contains(intValue) || player.hasSkillExcludingTemporaryOnes(skill)) {
				category = player.getPosition().hasSkill(skill) ? SkillDisplayInfo.Category.ROSTER : SkillDisplayInfo.Category.PLAYER;
			} else {
				category = SkillDisplayInfo.Category.TEMPORARY;
			}

			return new SkillDisplayInfo(format(skill, intValue), category, skill);
		}

		@Override
		public Integer intValue(List<String> values) {
			return getRelevantValue(map(values.toArray(new String[0]))).orElse(null);
		}

		private Set<Integer> map(String... values) {
			return Arrays.stream(values).filter(value -> StringTool.isProvided(value) && StringTool.isNumber(value))
				.map(Integer::valueOf).collect(Collectors.toSet());
		}

		protected abstract Optional<Integer> getRelevantValue(Set<Integer> values);

		protected abstract String format(Skill skill, int intValue);
	}
}

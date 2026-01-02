package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;
import com.fumbbl.ffb.util.StringTool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RulesCollection(RulesCollection.Rules.BB2025)
public class Hatred extends Skill {

	private final Evaluator evaluator;

	public Hatred() {
		super("Hatred", SkillCategory.TRAIT);
		evaluator = new Evaluator();
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleSkull);
		registerProperty(NamedProperties.canBeGainedByGettingEven);
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return evaluator;
	}

	private static class Evaluator implements SkillValueEvaluator {

		@Override
		public Set<SkillDisplayInfo> info(Skill skill, Player<?> player) {
			Set<String> skillValues = new HashSet<>();
			String skillValue = player.getSkillValueExcludingTemporaryOnes(skill);
			if (StringTool.isProvided(skillValue)) {
				skillValues.addAll(map(skillValue));
			}
			Set<String> tempSkillValues = map(player.temporarySkillValues(skill).toArray(new String[0]));
			tempSkillValues.removeAll(skillValues);
			return Stream.concat(
				skillValues.stream()
					.map(value -> new SkillDisplayInfo(sanitize(skill, value), SkillDisplayInfo.Category.ROSTER, skill)),
				tempSkillValues.stream()
					.map(value -> new SkillDisplayInfo(sanitize(skill, value), SkillDisplayInfo.Category.TEMPORARY, skill))
			).collect(Collectors.toSet());
		}

		@Override
		public Integer intValue(Set<String> tempValues) {
			return null;
		}

		@Override
		public Set<String> values(Skill skill, Player<?> player) {
			Set<String> values = player.temporarySkillValues(skill);
			String skillValue = player.getSkillValueExcludingTemporaryOnes(skill);
			if (StringTool.isProvided(skillValue)) {
				values.add(skillValue);
			}
			return map(values.toArray(new String[0]));
		}

		private String sanitize(Skill skill, String value) {
			return skill.getName() + " (" + value + ")";
		}

		private Set<String> map(String... values) {
			return Arrays.stream(values).flatMap(value -> Arrays.stream(value.split(";")))
				.map(value -> Keyword.forName(value).getName()).collect(Collectors.toSet());
		}
	}
}

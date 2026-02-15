package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.AnimosityValueEvaluator;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A player with this skill does not like players from his team that are a
 * different race than he is and will often refuse to play with them despite the
 * coach's orders. If this player at the end of his Hand-off or Pass Action
 * attempts to hand-of for pass the ball to a team-mate that is not the same
 * race as the Animosity player, roll a D6. On a 2+, the pass / hand-off is
 * carried out as normal. On a 1, the player refuses to try to give the ball to
 * any team-mate except one of his own race. The coach may choose to change the
 * target of the pass/hand-off to another team-mate of the same race as the
 * Animosity player, however no more movement is allowed for the Animosity
 * player, so the current Action may be lost for the turn.
 */
@RulesCollection(Rules.BB2025)
public class Animosity extends Skill {

	private final Evaluator evaluator;

	public Animosity() {
		super("Animosity", SkillCategory.TRAIT);
		evaluator = new Evaluator();
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.hasToRollToPassBallOn);
	}

	@Override
	public AnimosityValueEvaluator evaluator() {
		return evaluator;
	}

	private static class Evaluator implements AnimosityValueEvaluator {

		@Override
		public Set<SkillDisplayInfo> info(Skill skill, Player<?> player) {
			Set<String> skillValues =
				map(Optional.ofNullable(player.getSkillValueExcludingTemporaryOnes(skill)).orElse(allValue()));
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
			values.add(Optional.ofNullable(player.getSkillValueExcludingTemporaryOnes(skill)).orElse(allValue()));
			return map(values.toArray(new String[0]));
		}

		private String sanitize(Skill skill, String value) {
			if (value.equalsIgnoreCase(allValue())) {
				return skill.getName();
			}
			return skill.getName() + " (" + value + ")";
		}

		private Set<String> map(String... values) {
			return Arrays.stream(values).flatMap(value -> Arrays.stream(value.split(";")))
				.map(value -> Keyword.forName(value).getName()).collect(Collectors.toSet());
		}

		@Override
		public String allValue() {
			return "all";
		}
	}
}
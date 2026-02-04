package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;
import com.fumbbl.ffb.util.StringTool;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Swift Twins must be hired as a pair. 
 * Lucien: if Lucien performs a Block Action against an opposition player who is also 
 * marked by Valen, Lucien may reroll a single Block Dice.
 * Valen: If Valen performs a Pass Action that targets a square containing Lucien, 
 * then Valen suffers no modifiers to the Pass Test fot the range of the Pass Action.
 */

@RulesCollection(Rules.BB2025)
public class WorkingInTandem extends Skill {
	private final Evaluator evaluator;
	public static final String VARIANT_BLOCK = "block";
	public static final String VARIANT_PASS = "pass";

	public WorkingInTandem() {
		super("Working in Tandem", SkillCategory.TRAIT);
		evaluator = new Evaluator();
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleBlockDieWhenPartnerIsMarking);
		registerRerollSource(ReRolledActions.SINGLE_BLOCK_DIE, ReRollSources.WORKING_IN_TANDEM);

		registerProperty(NamedProperties.canPassToPartnerWithNoModifiers);
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return evaluator;
	}

	private static class Evaluator implements SkillValueEvaluator {
		@Override
		public Set<SkillDisplayInfo> info(Skill skill, Player<?> player) {
			Set<String> values = values(skill, player);
			if (values.isEmpty()) {
				return Collections.singleton(new SkillDisplayInfo(skill.getName(), SkillDisplayInfo.Category.ROSTER, skill));
			}
			return values.stream()
				.map(v -> new SkillDisplayInfo(skill.getName() + " (" + v + ")", SkillDisplayInfo.Category.ROSTER, skill))
				.collect(Collectors.toSet());
		}

		@Override
		public Integer intValue(Set<String> tempValues) {
			return null;
		}

		@Override
		public Set<String> values(Skill skill, Player<?> player) {
			Set<String> vals = new HashSet<>(player.temporarySkillValues(skill));
			String main = player.getSkillValueExcludingTemporaryOnes(skill);
			if (StringTool.isProvided(main)) {
				vals.addAll(Arrays.asList(main.split(";")));
			}
			return vals;
		}
	}
}

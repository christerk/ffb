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
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

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

	public WorkingInTandem() {
		super("Working in Tandem", SkillCategory.TRAIT, SkillUsageType.SPECIAL);
		evaluator = new Evaluator();
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleBlockDieWhenPartnerIsMarking);
		registerRerollSource(ReRolledActions.SINGLE_BLOCK_DIE, ReRollSources.WORKING_IN_TANDEM);

		registerProperty(NamedProperties.canPassToPartnerWithNoModifiers);
		registerModifier(new PassModifier(getName(), "No range mod when passing to partner", 0, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				Player<?> partner = context.getGame().getFieldModel().getPlayer(context.getTarget());
				return isPassVariant(context.getPlayer())
					&& partner != null
					&& UtilCards.hasSkill(partner, WorkingInTandem.this)
					&& isBlockVariant(partner)
					&& super.appliesToContext(skill, context);
			}
		});
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return evaluator;
	}

	public boolean isBlockVariant(Player<?> player) {
		return "block".equalsIgnoreCase(player.getSkillValueExcludingTemporaryOnes(this));
	}

	public boolean isPassVariant(Player<?> player) {
		return "pass".equalsIgnoreCase(player.getSkillValueExcludingTemporaryOnes(this));
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

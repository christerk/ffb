package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.server.util.ReRollDialogParameterFactory;
import com.fumbbl.ffb.server.util.ReRollRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds a dialog offering optional roll modifiers in addition to the available re-rolls.
 */
public class ReRollModifierChoiceDialogParameterFactory implements ReRollDialogParameterFactory {

	private static final List<ReRollProperty> TEAM_RE_ROLL_PROPERTIES = Collections.unmodifiableList(
		Arrays.asList(ReRollProperty.TRR, ReRollProperty.MASCOT, ReRollProperty.BRILLIANT_COACHING,
			ReRollProperty.PUMP_UP_THE_CROWD, ReRollProperty.SHOW_STAR, ReRollProperty.LONER));

	private final int roll;
	private final List<ModifierChoiceOption> modifierOptions;
	private final boolean reRollAllowed;

	public ReRollModifierChoiceDialogParameterFactory(int roll, List<ModifierChoiceOption> modifierOptions,
																										boolean reRollAllowed) {
		this.roll = roll;
		this.modifierOptions = modifierOptions == null ? new ArrayList<>() : new ArrayList<>(modifierOptions);
		this.reRollAllowed = reRollAllowed;
	}

	@Override
	public IDialogParameter create(ReRollRequest request, ReRollOptions reRollOptions) {
		List<ReRollProperty> properties = new ArrayList<>();
		Skill reRollSkill = null;

		if (reRollAllowed) {
			reRollSkill = reRollOptions.getReRollSkill();
			properties.addAll(reRollOptions.getProperties());
			if (usableEveryTurn(reRollSkill)) {
				properties = properties.stream().filter(property -> !TEAM_RE_ROLL_PROPERTIES.contains(property))
					.collect(Collectors.toList());
			}
		}

		boolean canReRoll = reRollSkill != null || properties.stream().anyMatch(ReRollProperty::isActualReRoll);
		if (modifierOptions.isEmpty() && !canReRoll) {
			return null;
		}

		return new DialogReRollModifierChoiceParameter(request.getPlayer().getId(), request.getReRolledAction(),
			request.getMinimumRoll(), roll, modifierOptions, properties, request.isFumble(), reRollSkill,
			request.getMenuProperty(), request.getDefaultValueKey(), request.getMessages());
	}

	private boolean usableEveryTurn(Skill reRollSkill) {
		if (reRollSkill == null) {
			return false;
		}
		SkillUsageType usageType = reRollSkill.getSkillUsageType();
		return usageType == SkillUsageType.REGULAR || usageType == SkillUsageType.ONCE_PER_TURN;
	}
}

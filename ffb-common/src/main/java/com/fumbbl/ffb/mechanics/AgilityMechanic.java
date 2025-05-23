package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.modifiers.ModifierContext;
import com.fumbbl.ffb.modifiers.PickupModifier;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.report.ReportSkillRoll;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Set;

public abstract class AgilityMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.AGILITY;
	}

	public abstract int minimumRollJumpUp(Player<?> pPlayer, Set<JumpUpModifier> modifiers);

	public abstract int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers);

	public abstract int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers, StatBasedRollModifier statBasedRollModifier);

	public abstract int minimumRollPickup(Player<?> pPlayer, Set<PickupModifier> pPickupModifiers);

	public abstract int minimumRollInterception(Player<?> pPlayer, Set<InterceptionModifier> pInterceptionModifiers);

	public abstract int minimumRollJump(Player<?> pPlayer, Set<JumpModifier> pJumpModifiers);

	public abstract int minimumRollHypnoticGaze(Player<?> pPlayer, Set<GazeModifier> pGazeModifiers);

	public abstract int minimumRollCatch(Player<?> pPlayer, Set<CatchModifier> pCatchModifiers);

	public abstract int minimumRollRightStuff(Player<?> pPlayer, Set<RightStuffModifier> pRightStuffModifiers);

	public abstract int minimumRollSafeThrow(Player<?> pPlayer);

	public abstract String formatDodgeResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatJumpResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatJumpUpResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatSafeThrowResult(Player<?> player);

	public abstract String formatRightStuffResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatCatchResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatInterceptionResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatHypnoticGazeResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatPickupResult(ReportSkillRoll report, Player<?> player);

	public abstract Wording interceptionWording(boolean easyIntercept);

	protected String formatRollModifiers(RollModifier<? extends ModifierContext>[] pRollModifiers) {
		StringBuilder modifiers = new StringBuilder();
		if (ArrayTool.isProvided(pRollModifiers)) {
			for (RollModifier<? extends ModifierContext> rollModifier : pRollModifiers) {
				if (rollModifier.getModifier() > 0) {
					modifiers.append(" - ");
				} else {
					modifiers.append(" + ");
				}
				if (!rollModifier.isModifierIncluded()) {
					modifiers.append(Math.abs(rollModifier.getModifier())).append(" ");
				}
				modifiers.append(rollModifier.getReportString());
			}
		}
		return modifiers.toString();
	}
}

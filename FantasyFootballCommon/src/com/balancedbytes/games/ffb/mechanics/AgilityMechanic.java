package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.modifiers.DodgeModifier;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;

import java.util.Set;

public abstract class AgilityMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.AGILITY;
	}

	public abstract int minimumRollJumpUp(Player<?> pPlayer);

	public abstract int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers);

	public abstract int minimumRollPickup(Player<?> pPlayer, Set<PickupModifier> pPickupModifiers);

	public abstract int minimumRollInterception(Player<?> pPlayer, Set<InterceptionModifier> pInterceptionModifiers);

	public abstract int minimumRollLeap(Player<?> pPlayer, Set<LeapModifier> pLeapModifiers);

	public abstract int minimumRollHypnoticGaze(Player<?> pPlayer, Set<GazeModifier> pGazeModifiers);

	public abstract int minimumRollCatch(Player<?> pPlayer, Set<CatchModifier> pCatchModifiers);

	public abstract int minimumRollRightStuff(Player<?> pPlayer, Set<RightStuffModifier> pRightStuffModifiers);

	public abstract int minimumRollSafeThrow(Player<?> pPlayer);

	public abstract String formatDodgeResult(ReportSkillRoll report, ActingPlayer player);

	public abstract String formatLeapResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatJumpUpResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatSafeThrowResult(Player<?> player);

	public abstract String formatRightStuffResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatCatchResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatInterceptionResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatHypnoticGazeResult(ReportSkillRoll report, Player<?> player);

	public abstract String formatPickupResult(ReportSkillRoll report, Player<?> player);

	public abstract Wording interceptionWording();

	protected String formatRollModifiers(IRollModifier[] pRollModifiers) {
		StringBuilder modifiers = new StringBuilder();
		if (ArrayTool.isProvided(pRollModifiers)) {
			for (IRollModifier rollModifier : pRollModifiers) {
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

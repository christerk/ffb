package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.DodgeModifiers;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class AgilityMechanic extends com.balancedbytes.games.ffb.mechanics.AgilityMechanic {

	private int getAgilityRollBase(int agility) {
		return 7 - Math.min(agility, 6);
	}

	@Override
	public int minimumRollJumpUp(Player<?> pPlayer) {
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) - 2);
	}

	@Override
	public int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers) {
		int modifierTotal = 0;
		for (DodgeModifier dodgeModifier : pDodgeModifiers) {
			modifierTotal += dodgeModifier.getModifier();
		}
		int statistic = pDodgeModifiers.contains(DodgeModifiers.BREAK_TACKLE) ? UtilCards.getPlayerStrength(pGame, pPlayer)
			: pPlayer.getAgility();
		return Math.max(2, getAgilityRollBase(statistic) - 1 + modifierTotal);
	}

	@Override
	public int minimumRollPickup(Player<?> pPlayer, Set<PickupModifier> pPickupModifiers) {
		int modifierTotal = 0;
		for (PickupModifier pickupModifier : pPickupModifiers) {
			modifierTotal += pickupModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) - 1 + modifierTotal);
	}

	@Override
	public int minimumRollInterception(Player<?> pPlayer, Set<InterceptionModifier> pInterceptionModifiers) {
		int modifierTotal = 0;
		for (InterceptionModifier interceptionModifier : pInterceptionModifiers) {
			modifierTotal += interceptionModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + 2 + modifierTotal);
	}

	@Override
	public int minimumRollLeap(Player<?> pPlayer, Set<LeapModifier> pLeapModifiers) {
		int modifierTotal = 0;
		for (LeapModifier leapModifier : pLeapModifiers) {
			modifierTotal += leapModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
	}

	@Override
	public int minimumRollHypnoticGaze(Player<?> pPlayer, Set<GazeModifier> pGazeModifiers) {
		int modifierTotal = 0;
		for (GazeModifier gazeModifier : pGazeModifiers) {
			modifierTotal += gazeModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
	}

	@Override
	public int minimumRollCatch(Player<?> pPlayer, Set<CatchModifier> pCatchModifiers) {
		int modifierTotal = 0;
		for (CatchModifier catchModifier : pCatchModifiers) {
			modifierTotal += catchModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
	}

	@Override
	public int minimumRollRightStuff(Player<?> pPlayer, Set<RightStuffModifier> pRightStuffModifiers) {
		int modifierTotal = 0;
		for (RightStuffModifier rightStuffModifier : pRightStuffModifiers) {
			modifierTotal += rightStuffModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()) + modifierTotal);
	}

	@Override
	public int minimumRollSafeThrow(Player<?> pPlayer) {
		return Math.max(2, getAgilityRollBase(pPlayer.getAgility()));
	}

	@Override
	public String formatDodgeResult(ReportSkillRoll report, ActingPlayer player) {
		StringBuilder neededRoll = new StringBuilder();
		if (report.hasRollModifier(DodgeModifiers.BREAK_TACKLE)) {
			neededRoll.append(" using Break Tackle (ST ").append(Math.min(6, player.getStrength()));
		} else {
			neededRoll.append(" (AG ").append(Math.min(6, player.getPlayer().getAgility()));
		}
		neededRoll.append(" + 1 Dodge").append(formatRollModifiers(report.getRollModifiers())).append(" + Roll > 6).");
		return neededRoll.toString();
	}

	@Override
	public String formatLeapResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatJumpUpResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatSafeThrowResult(Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) + " + Roll > 6).";
	}

	@Override
	public String formatRightStuffResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatCatchResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatInterceptionResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) + " - 2 Interception" +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatHypnoticGazeResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatPickupResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgility()) + " + 1 Pickup" +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	private String formatRollModifiers(IRollModifier[] pRollModifiers) {
		StringBuilder modifiers = new StringBuilder();
		if (ArrayTool.isProvided(pRollModifiers)) {
			for (IRollModifier rollModifier : pRollModifiers) {
				if (rollModifier.getModifier() != 0) {
					if (rollModifier.getModifier() > 0) {
						modifiers.append(" - ");
					} else {
						modifiers.append(" + ");
					}
					if (!rollModifier.isModifierIncluded()) {
						modifiers.append(Math.abs(rollModifier.getModifier())).append(" ");
					}
					modifiers.append(rollModifier.getName());
				}
			}
		}
		return modifiers.toString();
	}
}

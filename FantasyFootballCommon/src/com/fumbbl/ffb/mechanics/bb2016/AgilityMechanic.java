package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.Wording;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.modifiers.PickupModifier;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.report.ReportSkillRoll;

import java.util.Arrays;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class AgilityMechanic extends com.fumbbl.ffb.mechanics.AgilityMechanic {

	private int getAgilityRollBase(int agility) {
		return 7 - Math.min(agility, 6);
	}

	@Override
	public int minimumRollJumpUp(Player<?> pPlayer, Set<JumpUpModifier> modifiers) {
		int modifierTotal = 0;
		for (JumpUpModifier modifier : modifiers) {
			modifierTotal += modifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) + modifierTotal);
	}

	@Override
	public int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers) {
		int modifierTotal = 0;
		for (DodgeModifier dodgeModifier : pDodgeModifiers) {
			modifierTotal += dodgeModifier.getModifier();
		}
		int statistic = pDodgeModifiers.stream().anyMatch(DodgeModifier::isUseStrength) ? pPlayer.getStrengthWithModifiers()
			: pPlayer.getAgilityWithModifiers();
		return Math.max(2, getAgilityRollBase(statistic) - 1 + modifierTotal);
	}

	@Override
	public int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers, StatBasedRollModifier statBasedRollModifier) {
		return minimumRollDodge(pGame, pPlayer, pDodgeModifiers);
	}

	@Override
	public int minimumRollPickup(Player<?> pPlayer, Set<PickupModifier> pPickupModifiers) {
		int modifierTotal = 0;
		for (PickupModifier pickupModifier : pPickupModifiers) {
			modifierTotal += pickupModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) - 1 + modifierTotal);
	}

	@Override
	public int minimumRollInterception(Player<?> pPlayer, Set<InterceptionModifier> pInterceptionModifiers) {
		int modifierTotal = 0;
		for (InterceptionModifier interceptionModifier : pInterceptionModifiers) {
			modifierTotal += interceptionModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) + 2 + modifierTotal);
	}

	@Override
	public int minimumRollJump(Player<?> pPlayer, Set<JumpModifier> pJumpModifiers) {
		int modifierTotal = 0;
		for (JumpModifier jumpModifier : pJumpModifiers) {
			modifierTotal += jumpModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) + modifierTotal);
	}

	@Override
	public int minimumRollHypnoticGaze(Player<?> pPlayer, Set<GazeModifier> pGazeModifiers) {
		int modifierTotal = 0;
		for (GazeModifier gazeModifier : pGazeModifiers) {
			modifierTotal += gazeModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) + modifierTotal);
	}

	@Override
	public int minimumRollCatch(Player<?> pPlayer, Set<CatchModifier> pCatchModifiers) {
		int modifierTotal = 0;
		for (CatchModifier catchModifier : pCatchModifiers) {
			modifierTotal += catchModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) + modifierTotal);
	}

	@Override
	public int minimumRollRightStuff(Player<?> pPlayer, Set<RightStuffModifier> pRightStuffModifiers) {
		int modifierTotal = 0;
		for (RightStuffModifier rightStuffModifier : pRightStuffModifiers) {
			modifierTotal += rightStuffModifier.getModifier();
		}
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()) + modifierTotal);
	}

	@Override
	public int minimumRollSafeThrow(Player<?> pPlayer) {
		return Math.max(2, getAgilityRollBase(pPlayer.getAgilityWithModifiers()));
	}

	private boolean usedStrength(ReportSkillRoll report) {
		return Arrays.stream(report.getRollModifiers()).anyMatch(modifier -> modifier instanceof  DodgeModifier && ((DodgeModifier)modifier).isUseStrength());
	}

	@Override
	public String formatDodgeResult(ReportSkillRoll report, ActingPlayer player) {
		StringBuilder neededRoll = new StringBuilder();
		if (usedStrength(report)) {
			neededRoll.append(" using Break Tackle (ST ").append(Math.min(6, player.getPlayer().getStrengthWithModifiers()));
		} else {
			neededRoll.append(" (AG ").append(Math.min(6, player.getPlayer().getAgilityWithModifiers()));
		}
		neededRoll.append(" + 1 Dodge").append(formatRollModifiers(report.getRollModifiers())).append(" + Roll > 6).");
		return neededRoll.toString();
	}

	@Override
	public String formatJumpResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatJumpUpResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatSafeThrowResult(Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatRightStuffResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatCatchResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatInterceptionResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) + " - 2 Interception" +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatHypnoticGazeResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public String formatPickupResult(ReportSkillRoll report, Player<?> player) {
		return " (AG " + Math.min(6, player.getAgilityWithModifiers()) + " + 1 Pickup" +
			formatRollModifiers(report.getRollModifiers()) + " + Roll > 6).";
	}

	@Override
	public Wording interceptionWording(boolean ignored) {
		return new Wording("Interception", "intercept", "intercepts", "interceptor");
	}
}

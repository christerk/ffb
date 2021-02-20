package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.mechanics.Wording;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class AgilityMechanic extends com.balancedbytes.games.ffb.mechanics.AgilityMechanic {

	@Override
	public int minimumRollJumpUp(Player<?> pPlayer) {
		return minimumRoll(pPlayer.getAgility(), Collections.emptySet());
	}

	@Override
	public int minimumRollDodge(Game pGame, Player<?> pPlayer, Set<DodgeModifier> pDodgeModifiers) {
		return minimumRoll(pPlayer.getAgility(), pDodgeModifiers);
	}

	@Override
	public int minimumRollPickup(Player<?> pPlayer, Set<PickupModifier> pPickupModifiers) {
		return minimumRoll(pPlayer.getAgility(), pPickupModifiers);
	}

	@Override
	public int minimumRollInterception(Player<?> pPlayer, Set<InterceptionModifier> pInterceptionModifiers) {
		return minimumRoll(pPlayer.getAgility(), pInterceptionModifiers);
	}

	@Override
	public int minimumRollLeap(Player<?> pPlayer, Set<LeapModifier> pLeapModifiers) {
		return minimumRoll(pPlayer.getAgility(), pLeapModifiers);
	}

	@Override
	public int minimumRollHypnoticGaze(Player<?> pPlayer, Set<GazeModifier> pGazeModifiers) {
		return minimumRoll(pPlayer.getAgility(), pGazeModifiers);
	}

	@Override
	public int minimumRollCatch(Player<?> pPlayer, Set<CatchModifier> pCatchModifiers) {
		return minimumRoll(pPlayer.getAgility(), pCatchModifiers);
	}

	@Override
	public int minimumRollRightStuff(Player<?> pPlayer, Set<RightStuffModifier> pRightStuffModifiers) {
		return minimumRoll(pPlayer.getAgility(), pRightStuffModifiers);
	}

	@Override
	public int minimumRollSafeThrow(Player<?> pPlayer) {
		return minimumRoll(pPlayer.getAgility(), Collections.emptySet());
	}

	@Override
	public String formatDodgeResult(ReportSkillRoll report, ActingPlayer player) {
		return formatResult(player.getPlayer().getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatLeapResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatJumpUpResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatSafeThrowResult(Player<?> player) {
		return formatResult(player.getAgility(), new IRollModifier[0]);
	}

	@Override
	public String formatRightStuffResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatCatchResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatInterceptionResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatHypnoticGazeResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public String formatPickupResult(ReportSkillRoll report, Player<?> player) {
		return formatResult(player.getAgility(), report.getRollModifiers());
	}

	@Override
	public Wording interceptionWording() {
		return new Wording("Interference", "deflect", "deflects", "interfering player");
	}

	private int minimumRoll(int agility, Set<? extends IRollModifier> modifiers) {
		return agility + modifiers.stream().mapToInt(IRollModifier::getModifier).sum();
	}

	private String formatResult(int agility, IRollModifier[] modifiers) {
		return " (Roll" + formatRollModifiers(modifiers) + " >= " + Math.max(2, agility) + "+)";
	}
}

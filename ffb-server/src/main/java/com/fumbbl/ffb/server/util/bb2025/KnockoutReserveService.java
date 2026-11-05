package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.bb2025.ReportBretonnianPastriesRoll;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;

import java.util.Optional;

/**
 * Offers and resolves inducements with {@link Usage#KNOCKOUT_RESERVE} (Bretonnian Pastries): once a player is
 * Knocked Out and the apothecary was not used, the coach of the player's team may use the inducement to move the
 * player to the Reserves box on a successful roll.
 */
public class KnockoutReserveService {

	/**
	 * @return the knockout reserve inducement of the given set that still has uses left
	 */
	public Optional<InducementType> availableInducement(InducementSet inducementSet) {
		return inducementSet.getInducementMapping().keySet().stream()
			.filter(type -> type.hasUsage(Usage.KNOCKOUT_RESERVE) && inducementSet.hasUsesLeft(type))
			.findFirst();
	}

	/**
	 * @return true if the injury is a knock out that the apothecary did not treat
	 */
	public boolean isEligible(InjuryResult injuryResult) {
		InjuryContext injuryContext = injuryResult.injuryContext();
		ApothecaryStatus apothecaryStatus = injuryContext.getApothecaryStatus();
		return injuryContext.isKnockedOut() && ((apothecaryStatus == ApothecaryStatus.NO_APOTHECARY)
			|| (apothecaryStatus == ApothecaryStatus.DO_NOT_USE_APOTHECARY));
	}

	/**
	 * Shows the use inducement dialog to the coach of the injured player and waits for the answer.
	 */
	public void offer(IStep step, InjuryResult injuryResult, Player<?> injuredPlayer, InducementType inducementType) {
		UtilServerDialog.showDialog(step.getGameState(), new DialogUseInducementParameter(injuredPlayer.getTeam().getId(),
			new InducementType[]{inducementType}, injuredPlayer.getId()), true);
		injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_PASTRIES_USE);
		step.getResult().setNextAction(StepAction.CONTINUE);
	}

	/**
	 * Handles the coach's answer. If the inducement is used it is consumed and rolled for; on a successful roll the
	 * injury is changed to {@link PlayerState#RESERVE}.
	 *
	 * @param inducementType the inducement sent by the client, null if declined
	 * @return true if the roll was successful and the player goes to the Reserves box
	 */
	public boolean use(IStep step, InjuryResult injuryResult, Player<?> injuredPlayer, InducementType inducementType,
										 InducementSet inducementSet) {
		InjuryContext injuryContext = injuryResult.injuryContext();
		injuryContext.setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_PASTRIES);
		if ((inducementType == null) || !inducementType.hasUsage(Usage.KNOCKOUT_RESERVE)
			|| !UtilServerInducementUse.useInducement(inducementType, 1, inducementSet)) {
			return false;
		}
		String teamId = injuredPlayer.getTeam().getId();
		step.getResult().addReport(new ReportInducement(teamId, inducementType, 1));
		int roll = step.getGameState().getDiceRoller().rollDice(6);
		boolean successful = roll >= ReportBretonnianPastriesRoll.MINIMUM_ROLL;
		step.getResult().addReport(new ReportBretonnianPastriesRoll(teamId, injuredPlayer.getId(), roll, successful));
		if (successful) {
			injuryContext.setInjury(new PlayerState(PlayerState.RESERVE));
		}
		injuryContext.setApothecaryStatus(ApothecaryStatus.USE_PASTRIES);
		return successful;
	}
}

package com.fumbbl.ffb.server.step.bb2020.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogArgueTheCallParameter;
import com.fumbbl.ffb.dialog.DialogBriberyAndCorruptionParameter;
import com.fumbbl.ffb.dialog.DialogBribesParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2020.BriberyAndCorruptionAction;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandArgueTheCall;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportBribesRoll;
import com.fumbbl.ffb.report.bb2020.ReportArgueTheCallRoll;
import com.fumbbl.ffb.report.bb2020.ReportBriberyAndCorruptionReRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.util.StringTool;

import java.util.Optional;

/**
 * Step in foul sequence to handle bribes.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * <p>
 * Sets stepParameter FOULER_HAS_BALL for all steps on the stack. Sets
 * stepParameter ARGUE_THE_CALL_SUCCESSFUL for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBribes extends AbstractStepWithReRoll {

	private String fGotoLabelOnEnd;
	private Boolean fArgueTheCallChoice;
	private Boolean fArgueTheCallSuccessful;
	private Boolean fBribesChoice;
	private Boolean fBribeSuccessful;

	public StepBribes(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BRIBES;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
					fGotoLabelOnEnd = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pReceivedCommand.getId()) {
				case CLIENT_ARGUE_THE_CALL:
					ClientCommandArgueTheCall argueTheCallCommand = (ClientCommandArgueTheCall) pReceivedCommand.getCommand();
					fArgueTheCallChoice = argueTheCallCommand.hasPlayerId(actingPlayer.getPlayerId());
					fArgueTheCallSuccessful = null;
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_INDUCEMENT:
					ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pReceivedCommand.getCommand();
					if (inducementCommand.getInducementType().hasUsage(Usage.AVOID_BAN)) {
						fBribesChoice = inducementCommand.hasPlayerId(actingPlayer.getPlayerId());
						fBribeSuccessful = null;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UtilServerDialog.hideDialog(getGameState());
		InducementSet inducementSet = game.isHomePlaying() ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();

		if (fBribesChoice == null) {

			boolean friendsWithTheRef = getGameState().getPrayerState().isFriendsWithRef(game.getActingTeam());


			if (fArgueTheCallChoice == null) {
				boolean foulerHasBall = game.getFieldModel().getBallCoordinate()
					.equals(game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()));
				publishParameter(new StepParameter(StepParameterKey.FOULER_HAS_BALL, foulerHasBall));
				int biasedRefBonus = inducementSet.value(Usage.ADD_TO_ARGUE_ROLL);
				askForArgueTheCall(friendsWithTheRef, biasedRefBonus);
			}

			if (fArgueTheCallChoice != null && fArgueTheCallChoice && (getReRolledAction() != ReRolledActions.ARGUE_THE_CALL || getReRollSource() == ReRollSources.BRIBERY_AND_CORRUPTION)) {

				Optional<InducementType> briberyReRoll = inducementSet.getInducementMapping().keySet().stream().filter(type -> type.hasUsage(Usage.REROLL_ARGUE))
					.findFirst();

				if (getReRollSource() == ReRollSources.BRIBERY_AND_CORRUPTION) {
					if (briberyReRoll.isPresent() && inducementSet.hasUsesLeft(briberyReRoll.get())) {
						useBriberyReRoll(game, inducementSet, briberyReRoll.get());
					}
				}

				if (rollArgue(game, actingPlayer, inducementSet, friendsWithTheRef, briberyReRoll)) {
					return;
				}
			}

			if (getReRollSource() == null && getReRolledAction() == ReRolledActions.ARGUE_THE_CALL) {
				fBribesChoice = false;
				game.getTurnData().setCoachBanned(true);
			}

		}

		if (fArgueTheCallChoice != null && (fArgueTheCallSuccessful == null || !fArgueTheCallSuccessful)) {

			if (fBribesChoice == null) {
				askForBribes();
			}
			if ((fBribesChoice != null) && fBribesChoice && (fBribeSuccessful == null)) {
				Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				inducementSet.getInducementMapping().keySet().stream().filter(type -> type.hasUsage(Usage.AVOID_BAN))
					.findFirst().ifPresent(type -> {
						if (UtilServerInducementUse.useInducement(getGameState(), team, type, 1)) {
							int roll = getGameState().getDiceRoller().rollBribes();
							fBribeSuccessful = DiceInterpreter.getInstance().isBribesSuccessful(roll);
							getResult().addReport(new ReportBribesRoll(actingPlayer.getPlayerId(), fBribeSuccessful, roll));
						}
					});
			}

			if (((fBribeSuccessful != null) && fBribeSuccessful)) {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
				return;
			}
		}

		if ((fBribesChoice != null) && (fArgueTheCallChoice != null)) {
			boolean successful = (fArgueTheCallSuccessful != null) ? fArgueTheCallSuccessful : false;
			publishParameter(new StepParameter(StepParameterKey.ARGUE_THE_CALL_SUCCESSFUL, successful));
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			if (successful) {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private boolean rollArgue(Game game, ActingPlayer actingPlayer, InducementSet inducementSet, boolean friendsWithTheRef, Optional<InducementType> briberyReRoll) {
		int roll = getGameState().getDiceRoller().rollArgueTheCall();
		int modifiedRoll = friendsWithTheRef && roll > 1 ? roll + 1 : roll;

		int biasedRefBonus = inducementSet.value(Usage.ADD_TO_ARGUE_ROLL);
		modifiedRoll += biasedRefBonus;

		fArgueTheCallSuccessful = DiceInterpreter.getInstance().isArgueTheCallSuccessful(modifiedRoll);
		if (fArgueTheCallSuccessful) {
			fBribesChoice = false;
		}
		boolean coachBanned = DiceInterpreter.getInstance().isCoachBanned(modifiedRoll);
		getResult().addReport(
			new ReportArgueTheCallRoll(actingPlayer.getPlayerId(), fArgueTheCallSuccessful, coachBanned, roll, true, friendsWithTheRef, biasedRefBonus));
		boolean couldReRoll = roll == 1 && getReRollSource() != ReRollSources.BRIBERY_AND_CORRUPTION
			&& briberyReRoll.isPresent() && inducementSet.hasUsesLeft(briberyReRoll.get());
		if (couldReRoll && coachBanned) {
			useBriberyReRoll(game, inducementSet, briberyReRoll.get());
			return rollArgue(game, actingPlayer, inducementSet, friendsWithTheRef, Optional.empty());
		} else if (couldReRoll) {
			UtilServerDialog.showDialog(getGameState(), new DialogBriberyAndCorruptionParameter(game.getActingTeam().getId()), false);
			return true;
		} else if (coachBanned) {
			fBribesChoice = false;
			game.getTurnData().setCoachBanned(true);
		}

		return false;
	}

	private void useBriberyReRoll(Game game, InducementSet inducementSet, InducementType briberyReRoll) {
		Inducement inducement = inducementSet.getInducementMapping().get(briberyReRoll);
		inducement.setUses(inducement.getUses() + 1);
		inducementSet.addInducement(inducement);
		getResult().addReport(new ReportBriberyAndCorruptionReRoll(game.getActingTeam().getId(), BriberyAndCorruptionAction.USED));
	}

	private void askForBribes() {
		fBribesChoice = false;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		InducementSet inducementSet = game.getTurnData().getInducementSet();
		if (inducementSet.getInducementMapping().entrySet().stream()
			.anyMatch(entry -> entry.getKey().hasUsage(Usage.AVOID_BAN) && inducementSet.hasUsesLeft(entry.getKey()))) {
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			DialogBribesParameter dialogParameter = new DialogBribesParameter(team.getId(), 1);
			dialogParameter.addPlayerId(actingPlayer.getPlayerId());
			UtilServerDialog.showDialog(getGameState(), dialogParameter, false);
			fBribesChoice = null;
		}
	}

	private void askForArgueTheCall(boolean friendsWithTheRef, int biasedRefBonus) {
		fArgueTheCallChoice = false;
		Game game = getGameState().getGame();
		if (UtilGameOption.isOptionEnabled(game, GameOptionId.ARGUE_THE_CALL) && !game.getTurnData().isCoachBanned()) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			DialogArgueTheCallParameter dialogParameter = new DialogArgueTheCallParameter(team.getId(), true, friendsWithTheRef, biasedRefBonus);
			dialogParameter.addPlayerId(actingPlayer.getPlayerId());
			UtilServerDialog.showDialog(getGameState(), dialogParameter, false);
			fArgueTheCallChoice = null;
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.BRIBES_CHOICE.addTo(jsonObject, fBribesChoice);
		IServerJsonOption.BRIBE_SUCCESSFUL.addTo(jsonObject, fBribeSuccessful);
		return jsonObject;
	}

	@Override
	public StepBribes initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		fBribesChoice = IServerJsonOption.BRIBES_CHOICE.getFrom(source, jsonObject);
		fBribeSuccessful = IServerJsonOption.BRIBE_SUCCESSFUL.getFrom(source, jsonObject);
		return this;
	}

}

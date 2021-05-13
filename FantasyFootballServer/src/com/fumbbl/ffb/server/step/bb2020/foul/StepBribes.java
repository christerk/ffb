package com.fumbbl.ffb.server.step.bb2020.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogArgueTheCallParameter;
import com.fumbbl.ffb.dialog.DialogBribesParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandArgueTheCall;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportArgueTheCallRoll;
import com.fumbbl.ffb.report.ReportBribesRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
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
public class StepBribes extends AbstractStep {

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
					if (inducementCommand.getInducementType().getUsage() == Usage.AVOID_BAN) {
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
		if (fBribesChoice == null) {
			askForBribes();
		}
		if ((fBribesChoice != null) && fBribesChoice && (fBribeSuccessful == null)) {
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			InducementSet inducementSet = game.isHomePlaying() ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
			inducementSet.getInducementMapping().keySet().stream().filter(type -> type.getUsage() == Usage.AVOID_BAN)
				.findFirst().ifPresent(type -> {
				if (UtilServerInducementUse.useInducement(getGameState(), team, type, 1)) {
					int roll = getGameState().getDiceRoller().rollBribes();
					fBribeSuccessful = DiceInterpreter.getInstance().isBribesSuccessful(roll);
					getResult().addReport(new ReportBribesRoll(actingPlayer.getPlayerId(), fBribeSuccessful, roll));
					if (!fBribeSuccessful) {
						askForBribes();
					}
				}
			});
		}
		if (((fBribeSuccessful != null) && fBribeSuccessful)) {
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
			return;
		}
		if ((fBribesChoice != null) && (fArgueTheCallChoice == null)) {
			boolean foulerHasBall = game.getFieldModel().getBallCoordinate()
				.equals(game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()));
			publishParameter(new StepParameter(StepParameterKey.FOULER_HAS_BALL, foulerHasBall));
			askForArgueTheCall();
		}
		if ((fBribesChoice != null) && (fArgueTheCallChoice != null) && fArgueTheCallChoice) {
			int roll = getGameState().getDiceRoller().rollArgueTheCall();
			fArgueTheCallSuccessful = DiceInterpreter.getInstance().isArgueTheCallSuccessful(roll);
			boolean coachBanned = DiceInterpreter.getInstance().isCoachBanned(roll);
			getResult().addReport(
				new ReportArgueTheCallRoll(actingPlayer.getPlayerId(), fArgueTheCallSuccessful, coachBanned, roll, true));
			if (coachBanned) {
				game.getTurnData().setCoachBanned(true);
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

	private void askForBribes() {
		fBribesChoice = false;
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		InducementSet inducementSet = game.getTurnData().getInducementSet();
		if (inducementSet.getInducementMapping().entrySet().stream()
			.anyMatch(entry -> entry.getKey().getUsage() == Usage.AVOID_BAN && inducementSet.hasUsesLeft(entry.getKey()))) {
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			DialogBribesParameter dialogParameter = new DialogBribesParameter(team.getId(), 1);
			dialogParameter.addPlayerId(actingPlayer.getPlayerId());
			UtilServerDialog.showDialog(getGameState(), dialogParameter, false);
			fBribesChoice = null;
		}
	}

	private void askForArgueTheCall() {
		fArgueTheCallChoice = false;
		Game game = getGameState().getGame();
		if (UtilGameOption.isOptionEnabled(game, GameOptionId.ARGUE_THE_CALL) && !game.getTurnData().isCoachBanned()) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			DialogArgueTheCallParameter dialogParameter = new DialogArgueTheCallParameter(team.getId(), true);
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
	public StepBribes initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		fBribesChoice = IServerJsonOption.BRIBES_CHOICE.getFrom(game, jsonObject);
		fBribeSuccessful = IServerJsonOption.BRIBE_SUCCESSFUL.getFrom(game, jsonObject);
		return this;
	}

}

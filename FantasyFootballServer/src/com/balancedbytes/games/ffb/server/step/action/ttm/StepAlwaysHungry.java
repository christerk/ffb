package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in ttm sequence to handle skill ALWAYS_HUNGRY. Failure or success refer
 * to the escape roll of a picked up player. Continues with next step if
 * ALWAYS_HUNGRY roll fails (no attempt to eat player).
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * 
 * @author Kalimar
 */
public final class StepAlwaysHungry extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private String fGotoLabelOnSuccess;
	private String fThrownPlayerId;

	public StepAlwaysHungry(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.ALWAYS_HUNGRY;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				// mandatory
				case GOTO_LABEL_ON_SUCCESS:
					fGotoLabelOnSuccess = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
		if (fGotoLabelOnSuccess == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case THROWN_PLAYER_ID:
				fThrownPlayerId = (String) pParameter.getValue();
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if (thrownPlayer == null) {
			return;
		}
		boolean doAlwaysHungry = UtilCards.hasUnusedSkill(game, actingPlayer, SkillConstants.ALWAYS_HUNGRY);
		boolean doEscape = UtilCards.hasSkill(game, actingPlayer, SkillConstants.ALWAYS_HUNGRY) && !doAlwaysHungry;
		if (doAlwaysHungry) {
			if (ReRolledActions.ALWAYS_HUNGRY == getReRolledAction()) {
				if ((getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					doEscape = true;
					doAlwaysHungry = false;
				}
			}
			if (doAlwaysHungry) {
				int roll = getGameState().getDiceRoller().rollSkill();
				boolean successful = DiceInterpreter.getInstance().isAlwaysHungrySuccessful(roll);
				boolean reRolled = ((getReRolledAction() == ReRolledActions.ALWAYS_HUNGRY) && (getReRollSource() != null));
				getResult().addReport(new ReportSkillRoll(ReportId.ALWAYS_HUNGRY_ROLL, actingPlayer.getPlayerId(), successful,
						roll, 2, reRolled));
				if (successful) {
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					doEscape = true;
					if (getReRolledAction() != ReRolledActions.ALWAYS_HUNGRY) {
						setReRolledAction(ReRolledActions.ALWAYS_HUNGRY);
						if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
								ReRolledActions.ALWAYS_HUNGRY, 2, false)) {
							doEscape = false;
						}
					}
				}
			}
		}
		if (doEscape) {
			actingPlayer.markSkillUsed(SkillConstants.ALWAYS_HUNGRY);
			ReRollSource reRollSource = null;
			if (ReRolledActions.ESCAPE == getReRolledAction()) {
				if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), thrownPlayer)) {
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
					return;
				}
			}
			if (doEscape) {
				int roll = getGameState().getDiceRoller().rollSkill();
				boolean successful = DiceInterpreter.getInstance().isEscapeFromAlwaysHungrySuccessful(roll);
				getResult().addReport(
						new ReportSkillRoll(ReportId.ESCAPE_ROLL, fThrownPlayerId, successful, roll, 2, (reRollSource != null)));
				if (successful) {
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnSuccess);
				} else {
					if (getReRolledAction() != ReRolledActions.ESCAPE) {
						setReRolledAction(ReRolledActions.ESCAPE);
						if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), thrownPlayer, ReRolledActions.ESCAPE, 2,
								false)) {
							return;
						}
					}
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				}
			}
		}
		if (!doAlwaysHungry && !doEscape) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		return jsonObject;
	}

	@Override
	public StepAlwaysHungry initFrom(Game game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(game, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}

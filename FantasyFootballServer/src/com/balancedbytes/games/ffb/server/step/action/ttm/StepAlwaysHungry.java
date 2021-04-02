package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.report.ReportAlwaysHungryRoll;
import com.balancedbytes.games.ffb.report.ReportEscapeRoll;
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
@RulesCollection(RulesCollection.Rules.COMMON)
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
			if (pParameter.getKey() == StepParameterKey.THROWN_PLAYER_ID) {
				fThrownPlayerId = (String) pParameter.getValue();
				return true;
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
		Player<?> thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if (thrownPlayer == null) {
			return;
		}
		boolean doAlwaysHungry = UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.mightEatPlayerToThrow);
		boolean doEscape = UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.mightEatPlayerToThrow) && !doAlwaysHungry;
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
				getResult().addReport(new ReportAlwaysHungryRoll(actingPlayer.getPlayerId(), successful,
						roll, 2, reRolled, null));
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
			Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.mightEatPlayerToThrow);
			actingPlayer.markSkillUsed(skill);
			if (ReRolledActions.ESCAPE == getReRolledAction()) {
				if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), thrownPlayer)) {
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
					return;
				}
			}
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean successful = DiceInterpreter.getInstance().isEscapeFromAlwaysHungrySuccessful(roll);
			getResult().addReport(
					new ReportEscapeRoll(fThrownPlayerId, successful, roll, 2, false, null));
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
	public StepAlwaysHungry initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}

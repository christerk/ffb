package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportAlwaysHungryRoll;
import com.fumbbl.ffb.report.ReportEscapeRoll;
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
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.UtilCards;

/**
 * Step in ttm sequence to handle skill ALWAYS_HUNGRY. Failure or success refer
 * to the escape roll of a picked up player. Continues with next step if
 * ALWAYS_HUNGRY roll fails (no attempt to eat player).
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * <p>
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepAlwaysHungry extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private String fGotoLabelOnSuccess;
	private String fThrownPlayerId;
	private boolean isKicked;

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
					case IS_KICKED_PLAYER:
						isKicked = (boolean) parameter.getValue();
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
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.THROWN_PLAYER_ID) {
				fThrownPlayerId = (String) parameter.getValue();
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
			if (isKicked) {
				game.getTurnData().setKtmUsed(true);
			} else {
				game.getTurnData().setPassUsed(true);
			}
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
				publishParameter(StepParameter.from(StepParameterKey.PASS_RESULT, PassResult.FUMBLE));
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
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, isKicked);
		return jsonObject;
	}

	@Override
	public StepAlwaysHungry initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		Boolean isKickedBoolean = IServerJsonOption.IS_KICKED_PLAYER.getFrom(source, jsonObject);
		isKicked = isKickedBoolean != null && isKickedBoolean;
		return this;
	}

}

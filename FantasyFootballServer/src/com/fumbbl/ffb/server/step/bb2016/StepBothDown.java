package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;

/**
 * Step in block sequence to handle both down block result.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepBothDown extends AbstractStep {

	private PlayerState fOldDefenderState;

	public StepBothDown(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BOTH_DOWN;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case OLD_DEFENDER_STATE:
				fOldDefenderState = (PlayerState) parameter.getValue();
				return true;
			default:
				break;
			}
		}
		return false;
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
		PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
		if (!game.getDefender().hasSkillProperty(NamedProperties.preventFallOnBothDown)) {
			game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
		} else {
			game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
		}
		if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventFallOnBothDown)) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), attackerState.changeBase(PlayerState.FALLING));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
		return jsonObject;
	}

	@Override
	public StepBothDown initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		return this;
	}

}

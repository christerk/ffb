package com.fumbbl.ffb.server.step.bb2020;

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
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.action.block.UtilBlockSequence;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

/**
 * Step in the block sequence to handle skill BALL_AND_CHAIN.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_PUSHBACK.
 * <p>
 * Expects stepParameter OLD_DEFENDER_STATE_ID to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter STARTING_PUSHBACK_SQUARE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlockBallAndChain extends AbstractStep {

	private String fGotoLabelOnPushback;
	private PlayerState fOldDefenderState;
	private boolean endTurn;

	public StepBlockBallAndChain(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLOCK_BALL_AND_CHAIN;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_PUSHBACK) {
					fGotoLabelOnPushback = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnPushback)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_PUSHBACK + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case OLD_DEFENDER_STATE:
					fOldDefenderState = (PlayerState) parameter.getValue();
					return true;
				case END_TURN:
					endTurn = parameter.getValue() != null && (boolean) parameter.getValue();
					break;
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

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.movesRandomly) && endTurn) {
			publishParameters(UtilBlockSequence.initPushback(this));
			PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
			game.getFieldModel().setPlayerState(game.getActingPlayer().getPlayer(), playerState.changeBase(PlayerState.FALLING));

			if (fOldDefenderState.getBase() == PlayerState.PRONE || fOldDefenderState.getBase() == PlayerState.STUNNED) {
				game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState.changeBase(PlayerState.HIT_ON_GROUND));
			}

			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
		} else if (UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.movesRandomly) && (fOldDefenderState != null)
			&& fOldDefenderState.isProneOrStunned()) {
			publishParameters(UtilBlockSequence.initPushback(this));
			game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState.changeBase(PlayerState.HIT_ON_GROUND));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.addTo(jsonObject, fGotoLabelOnPushback);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
		return jsonObject;
	}

	@Override
	public StepBlockBallAndChain initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnPushback = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(source, jsonObject);
		fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		return this;
	}

}
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
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_PUSHBACK.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter STARTING_PUSHBACK_SQUARE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepBlockBallAndChain extends AbstractStep {

	private String fGotoLabelOnPushback;
	private PlayerState fOldDefenderState;

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
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_PUSHBACK:
					fGotoLabelOnPushback = (String) parameter.getValue();
					break;
				default:
					break;
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
		if (UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.movesRandomly) && (fOldDefenderState != null)
				&& fOldDefenderState.isProne()) {
			publishParameters(UtilBlockSequence.initPushback(this));
			game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState.changeBase(PlayerState.FALLING));
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
	public StepBlockBallAndChain initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnPushback = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(source, jsonObject);
		fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		return this;
	}

}
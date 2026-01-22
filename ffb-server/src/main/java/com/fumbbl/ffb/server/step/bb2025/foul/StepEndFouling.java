package com.fumbbl.ffb.server.step.bb2025.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.bb2020.Move;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Final step of the foul sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepEndFouling extends AbstractStep {

	private boolean fEndTurn;
	private boolean fEndPlayerAction, checkForgo;
	private PlayerAction bloodlustAction;

	public StepEndFouling(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_FOULING;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case END_PLAYER_ACTION:
					fEndPlayerAction = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case END_TURN:
					fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case BLOOD_LUST_ACTION:
					bloodlustAction = (PlayerAction) parameter.getValue();
					consume(parameter);
					return true;
				case CHECK_FORGO:
					checkForgo = parameter.getValue() != null && (boolean) parameter.getValue();
					consume(parameter);
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
		Player<?> player = actingPlayer.getPlayer();
		boolean isOnPitch = FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player));
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		if (actingPlayer.isSufferingBloodLust() && bloodlustAction != null) {
			UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), bloodlustAction, false);
			((Move) factory.forName(SequenceGenerator.Type.Move.name())).pushSequence(new Move.SequenceParams(getGameState()));
			actingPlayer.setHasFouled(false);
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if (!fEndTurn && isOnPitch && player.hasSkillProperty(NamedProperties.canMoveAfterFoul) && UtilPlayer.isNextMovePossible(game, false)) {
			((Select) factory.forName(SequenceGenerator.Type.Select.name()))
				.pushSequence(new Select.SequenceParams(getGameState(), true));
			UtilServerSteps.changePlayerAction(this, player.getId(),
				PlayerAction.MOVE, false);
			actingPlayer.setStandingUp(false);
		} else {
			((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
				.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn, checkForgo));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.PLAYER_ACTION.addTo(jsonObject, bloodlustAction);
		IServerJsonOption.CHECK_FORGO.addTo(jsonObject, checkForgo);
		return jsonObject;
	}

	@Override
	public StepEndFouling initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		bloodlustAction = (PlayerAction) IServerJsonOption.PLAYER_ACTION.getFrom(source, jsonObject);
		checkForgo = toPrimitive(IServerJsonOption.CHECK_FORGO.getFrom(source, jsonObject));
		return this;
	}

}

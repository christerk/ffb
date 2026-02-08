package com.fumbbl.ffb.server.step.bb2025.punt;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Move;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepEndPunt extends AbstractStep {

	private String catcherId;
	private String ballSnatcherId;
	private boolean endTurn;

	public StepEndPunt(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_PUNT;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case CATCHER_ID:
					catcherId = (String) parameter.getValue();
					consume(parameter);
					return true;
				case END_TURN:
					endTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					return true;
				case PLAYER_ID:
					ballSnatcherId = (String) parameter.getValue();
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
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		game.getFieldModel().setRangeRuler(null);
		game.getFieldModel().setOutOfBounds(false);
		ActingPlayer actingPlayer = game.getActingPlayer();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endGenerator = ((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()));
		Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());


		Player<?> catcher = game.getPlayerById(catcherId);

		boolean ballWasSnatched = StringTool.isProvided(ballSnatcherId);
		if (ballWasSnatched) {
			catcher = game.getPlayerById(catcherId);
		} else {
			catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
		}
		//TODO check if we need the other conditions
		endTurn |= (UtilServerSteps.checkTouchdown(getGameState()) /*|| (catcher == null) ||
				UtilPlayer.findOtherTeam(game, game.getThrower()).hasPlayer(catcher)*/);
		endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, endTurn));

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, ballSnatcherId);
		return jsonObject;
	}

	@Override
	public StepEndPunt initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		catcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		ballSnatcherId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}

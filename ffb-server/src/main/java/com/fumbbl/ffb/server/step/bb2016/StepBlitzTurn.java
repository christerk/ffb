package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.mechanic.SetupMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerTimer;

/**
 * Step in kickoff sequence to handle blitz kickoff result.
 * <p>
 * Expects stepParameter END_TURN to be set by a preceding step. (parameter is
 * consumed on TurnMode.BLITZ)
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepBlitzTurn extends AbstractStep {

	private boolean fEndTurn;

	public StepBlitzTurn(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLITZ_TURN;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		Game game = getGameState().getGame();
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.END_TURN) {
				fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				if (game.getTurnMode() == TurnMode.BLITZ) {
					consume(parameter);
				}
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

		if (game.getTurnMode() == TurnMode.BLITZ) {

			if (fEndTurn) {
				game.setTurnMode(TurnMode.KICKOFF);
			}

		} else {

			game.setTurnMode(TurnMode.BLITZ);
			Team blitzingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();

			MechanicsFactory mechanicsFactory = game.getFactory(FactoryType.Factory.MECHANIC);
			SetupMechanic mechanic = (SetupMechanic) mechanicsFactory.forName(Mechanic.Type.SETUP.name());
			mechanic.pinPlayersInTacklezones(getGameState(), blitzingTeam);
			long currentTimeMillis = System.currentTimeMillis();
			if (game.isTurnTimeEnabled()) {
				UtilServerTimer.stopTurnTimer(getGameState(), currentTimeMillis);
				game.setTurnTime(0);
				UtilServerTimer.startTurnTimer(getGameState(), currentTimeMillis);
			}
			game.startTurn();
			UtilServerGame.updatePlayerStateDependentProperties(this);
			// insert select sequence into kickoff sequence after this step
			getGameState().pushCurrentStepOnStack();
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			((Select)factory.forName(SequenceGenerator.Type.Select.name()))
				.pushSequence(new Select.SequenceParams(getGameState(), true));

		}

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		return jsonObject;
	}

	@Override
	public StepBlitzTurn initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		return this;
	}

}

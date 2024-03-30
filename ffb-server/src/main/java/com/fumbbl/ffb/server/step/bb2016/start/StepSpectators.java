package com.fumbbl.ffb.server.step.bb2016.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.report.bb2016.ReportSpectators;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Kickoff;

/**
 * Step in start game sequence to roll spectators.
 *
 * Updates persistence. Pushes kickoffSequence on stack when finishing.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepSpectators extends AbstractStep {

	public StepSpectators(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.SPECTATORS;
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
		getResult().addReport(rollSpectators());
		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		((Kickoff)factory.forName(SequenceGenerator.Type.Kickoff.name()))
			.pushSequence(new Kickoff.SequenceParams(getGameState(), true));
		GameCache gameCache = getGameState().getServer().getGameCache();
		gameCache.queueDbUpdate(getGameState(), true);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private ReportSpectators rollSpectators() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		TeamResult teamResultHome = gameResult.getTeamResultHome();
		TeamResult teamResultAway = gameResult.getTeamResultAway();
		int[] fanRollHome = getGameState().getDiceRoller().rollSpectators();
		teamResultHome.setSpectators((fanRollHome[0] + fanRollHome[1] + game.getTeamHome().getFanFactor()) * 1000);
		int[] fanRollAway = getGameState().getDiceRoller().rollSpectators();
		teamResultAway.setSpectators((fanRollAway[0] + fanRollAway[1] + game.getTeamAway().getFanFactor()) * 1000);
		if (teamResultHome.getSpectators() >= (2 * teamResultAway.getSpectators())) {
			teamResultHome.setFame(2);
		} else if (teamResultHome.getSpectators() > teamResultAway.getSpectators()) {
			teamResultHome.setFame(1);
		} else {
			teamResultHome.setFame(0);
		}
		if (teamResultAway.getSpectators() >= (2 * teamResultHome.getSpectators())) {
			teamResultAway.setFame(2);
		} else if (teamResultAway.getSpectators() > teamResultHome.getSpectators()) {
			teamResultAway.setFame(1);
		} else {
			teamResultAway.setFame(0);
		}
		return new ReportSpectators(fanRollHome, teamResultHome.getSpectators(), teamResultHome.getFame(), fanRollAway,
				teamResultAway.getSpectators(), teamResultAway.getFame());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepSpectators initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}

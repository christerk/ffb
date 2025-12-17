package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.mechanic.StateMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Inducement;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;

/**
 * Step to init the kickoff sequence.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepInitKickoff extends AbstractStep {

	public StepInitKickoff(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_KICKOFF;
	}

	@Override
	public void start() {
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
		if (game.getTurnMode() == TurnMode.START_GAME) {
			MechanicsFactory mechanicsFactory = game.getFactory(FactoryType.Factory.MECHANIC);
			StateMechanic stateMechanic = (StateMechanic) mechanicsFactory.forName(Mechanic.Type.STATE.name());

			UtilServerDialog.hideDialog(getGameState());
			stateMechanic.startHalf(this, 1);
			game.setTurnMode(TurnMode.SETUP);
			game.startTurn();
			UtilServerGame.prepareForSetup(game);
		}
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		((Inducement)factory.forName(SequenceGenerator.Type.Inducement.name()))
			.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.BEFORE_SETUP,
				!game.isHomePlaying()));
		((Inducement)factory.forName(SequenceGenerator.Type.Inducement.name()))
			.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.BEFORE_SETUP,
				game.isHomePlaying()));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public StepInitKickoff initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}

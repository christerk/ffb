package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.generator.Move;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class StepResetToMove extends AbstractStep {

	private PlayerAction action;

	public StepResetToMove(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.RESET_TO_MOVE;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.RESET_PLAYER_ACTION) {
				action = (PlayerAction) parameter.getValue();
				consume(parameter);
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (action != null) {
			GameState gameState = getGameState();
			gameState.getStepStack().clear();
			Game game = gameState.getGame();

			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
			moveGenerator.pushSequence(new Move.SequenceParams(gameState));

			UtilServerSteps.changePlayerAction(this, game.getActingPlayer().getPlayerId(), action, game.getActingPlayer().isJumping());

			game.setDefenderId(null);
		}
	}
}

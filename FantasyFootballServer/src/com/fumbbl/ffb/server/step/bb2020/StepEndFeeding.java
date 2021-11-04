package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Pass;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.EndTurn;
import com.fumbbl.ffb.server.step.generator.common.Inducement;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Final step of the feed sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndFeeding extends AbstractStep {

	private boolean fEndPlayerAction;
	private boolean fEndTurn;

	public StepEndFeeding(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_FEEDING;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case END_PLAYER_ACTION:
					fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					consume(pParameter);
					return true;
				case END_TURN:
					fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					consume(pParameter);
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
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		game.setDefenderId(null);
		if (fEndTurn) {
			if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
				((EndTurn) factory.forName(SequenceGenerator.Type.EndTurn.name()))
					.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
			} else {
				UtilServerSteps.changePlayerAction(this, null, null, false);
				if (game.getTurnMode() == TurnMode.REGULAR) {
					((Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()))
						.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.END_OF_OPPONENT_TURN,
							!game.isHomePlaying()));
					((Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()))
						.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.END_OF_OWN_TURN,
							game.isHomePlaying()));
				} else if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
					SequenceGenerator.SequenceParams endTurnParams = new SequenceGenerator.SequenceParams(getGameState());
					((EndTurn) factory.forName(SequenceGenerator.Type.EndTurn.name())).pushSequence(endTurnParams);
				}
			}
		} else if (!fEndPlayerAction && (game.getThrowerAction() != null) && game.getThrowerAction().isPassing()) {
			((Pass) factory.forName(SequenceGenerator.Type.Pass.name()))
				.pushSequence(new Pass.SequenceParams(getGameState(), game.getPassCoordinate()));
		} else if ((game.getTurnMode() == TurnMode.KICKOFF_RETURN) || (game.getTurnMode() == TurnMode.PASS_BLOCK)) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
		} else {
			game.setPassCoordinate(null);
			UtilServerSteps.changePlayerAction(this, null, null, false);
			((Select) factory.forName(SequenceGenerator.Type.Select.name()))
				.pushSequence(new Select.SequenceParams(getGameState(), false));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		return jsonObject;
	}

	@Override
	public StepEndFeeding initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		return this;
	}

}

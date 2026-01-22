package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
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
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndFuriousOutburst extends AbstractStep {

	private boolean endTurn;


	public StepEndFuriousOutburst(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.END_FURIOUS_OUTBURST;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case END_TURN:
					endTurn = toPrimitive((Boolean) parameter.getValue());
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();

		String selectedPlayerId = fieldModel.getTargetSelectionState().getSelectedPlayerId();
		if (StringTool.isProvided(selectedPlayerId)) {
			Player<?> target = game.getPlayerById(selectedPlayerId);
			fieldModel.setPlayerState(target, fieldModel.getPlayerState(target).changeSelectedStabTarget(false));
		}
		fieldModel.setTargetSelectionState(null);


		if (actingPlayer.hasActed()) {
			actingPlayer.markSkillUsed(NamedProperties.canTeleportBeforeAndAfterAvRollAttack);
			game.getTurnData().setBlitzUsed(true);
		}

		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndPlayerAction endPlayerAction = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
		endPlayerAction.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, endTurn));


		getResult().setNextAction(StepAction.NEXT_STEP);

	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);

		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		return this;
	}

}

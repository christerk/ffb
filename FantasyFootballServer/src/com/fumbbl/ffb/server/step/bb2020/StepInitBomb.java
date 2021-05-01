package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportBombExplodesAfterCatch;
import com.fumbbl.ffb.report.ReportBombOutOfBounds;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.SpecialEffect.SequenceParams;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Initialization step of the pass sequence. May push SpecialEffect sequences
 * onto the stack.
 *
 * Needs to be initialized with stepParameter CATCHER_ID. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_END. May be initialized with
 * stepParameter OLD_TURN_MODE. Needs to be initialized with stepParameter
 * PASS_FUMBLE.
 *
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * END_PLAYER_ACTION for all steps on the stack. Sets stepParameter
 * OLD_TURN_MODE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepInitBomb extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fCatcherId;
	private boolean fPassFumble;
	private FieldCoordinate fBombCoordinate;
	private boolean fBombOutOfBounds;

	public StepInitBomb(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.INIT_BOMB;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// optional
				case CATCHER_ID:
					fCatcherId = (String) parameter.getValue();
					break;
				// mandatory
				case GOTO_LABEL_ON_END:
					fGotoLabelOnEnd = (String) parameter.getValue();
					break;
				// mandatory
				case PASS_FUMBLE:
					fPassFumble = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				case BOMB_OUT_OF_BOUNDS:
					fBombOutOfBounds = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					consume(parameter);
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case BOMB_OUT_OF_BOUNDS:
				fBombOutOfBounds = pParameter.getValue() != null ? (Boolean) pParameter.getValue() : false;
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
		game.getFieldModel().setRangeRuler(null);
		if (fPassFumble) {
			fCatcherId = null;
		}
		if (fBombOutOfBounds) {
			fCatcherId = null;
		}

		if (fCatcherId != null) {
			int roll = getGameState().getDiceRoller().rollDice(6);
			boolean explodes = roll >= 4;
			getResult().addReport(new ReportBombExplodesAfterCatch(fCatcherId, explodes, roll));
			if (explodes) {
				fCatcherId = null;
			}
		}

		if (fCatcherId == null) {
			fBombCoordinate = game.getFieldModel().getBombCoordinate();
			if (fBombCoordinate == null) {
				getResult().addReport(new ReportBombOutOfBounds());
			} else {
				game.getFieldModel().setBombCoordinate(null);
				getResult().setAnimation(new Animation(AnimationType.BOMB_EXLOSION, fBombCoordinate));
				UtilServerGame.syncGameModel(this);
				game.getFieldModel().add(new BloodSpot(fBombCoordinate, new PlayerState(PlayerState.HIT_BY_BOMB)));
				List<Player<?>> affectedPlayers = new ArrayList<>();
				FieldCoordinate[] targetCoordinates = game.getFieldModel().findAdjacentCoordinates(fBombCoordinate,
						FieldCoordinateBounds.FIELD, 1, true);
				for (int i = targetCoordinates.length - 1; i >= 0; i--) {
					Player<?> player = game.getFieldModel().getPlayer(targetCoordinates[i]);
					if (player != null) {
						affectedPlayers.add(player);
					}
				}
				if (affectedPlayers.size() > 0) {
					SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
					com.fumbbl.ffb.server.step.generator.SpecialEffect generator =
						(com.fumbbl.ffb.server.step.generator.SpecialEffect) factory.forName(SequenceGenerator.Type.SpecialEffect.name());

					affectedPlayers.stream().map(player -> {
						boolean rollForEffect = !fBombCoordinate.equals(game.getFieldModel().getPlayerCoordinate(player));
						return new SequenceParams(getGameState(), SpecialEffect.BOMB,
							player.getId(), rollForEffect);
					}).forEach(generator::pushSequence);
				}
				publishParameter(new StepParameter(StepParameterKey.BOMB_EXPLODED, true));
			}
			leaveStep(null);
		} else {
			leaveStep(fGotoLabelOnEnd);
		}
	}

	private void leaveStep(String pGotoLabel) {
		publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, fCatcherId));
		if (StringTool.isProvided(pGotoLabel)) {
			getResult().setNextAction(StepAction.GOTO_LABEL, pGotoLabel);
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, fPassFumble);
		IServerJsonOption.BOMB_COORDINATE.addTo(jsonObject, fBombCoordinate);
		return jsonObject;
	}

	@Override
	public StepInitBomb initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		fPassFumble = IServerJsonOption.PASS_FUMBLE.getFrom(game, jsonObject);
		fBombCoordinate = IServerJsonOption.BOMB_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}

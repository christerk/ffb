package com.fumbbl.ffb.server.step.action.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportScatterPlayer;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step in the move sequence to handle skill BALL_AND_CHAIN.
 *
 * Needs to be initialized with stepParameter DISPATCH_TO_LABEL. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_END. May be initialized with
 * stepParameter GAZE_VICTIM_ID. May be initialized with stepParameter
 * MOVE_STACK.
 *
 * Expects stepParameter MOVE_STACK to be set by a preceding step.
 *
 * Sets stepParameter COORDINATE_FROM for all steps on the stack. Sets
 * stepParameter COORDINATE_TO for all steps on the stack. Sets stepParameter
 * DISPATCH_PLAYER_ACTION for all steps on the stack. Sets stepParameter
 * END_TURN for all steps on the stack. Sets stepParameter END_PLAYER_ACTION for
 * all steps on the stack. Sets stepParameter MOVE_STACK for all steps on the
 * stack.
 *
 * May replace rest of move sequence with inducement sequence.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepMoveBallAndChain extends AbstractStep {

	private String fGotoLabelOnEnd;
	private String fGotoLabelOnFallDown;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;

	public StepMoveBallAndChain(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MOVE_BALL_AND_CHAIN;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_END:
					fGotoLabelOnEnd = (String) parameter.getValue();
					break;
				// mandatory
				case GOTO_LABEL_ON_FALL_DOWN:
					fGotoLabelOnFallDown = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
		if (!StringTool.isProvided(fGotoLabelOnFallDown)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FALL_DOWN + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case COORDINATE_FROM:
				fCoordinateFrom = (FieldCoordinate) pParameter.getValue();
				return true;
			case COORDINATE_TO:
				fCoordinateTo = (FieldCoordinate) pParameter.getValue();
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.movesRandomly)) {
			Direction playerScatter = null;
			int scatterRoll = getGameState().getDiceRoller().rollThrowInDirection();
			if (fCoordinateFrom.getX() < fCoordinateTo.getX()) {
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
			} else if (fCoordinateFrom.getX() > fCoordinateTo.getX()) {
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
			} else if (fCoordinateFrom.getY() < fCoordinateTo.getY()) {
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
			} else { // coordinateFrom.getY() > coordinateTo.getY()
				playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
			}
			fCoordinateTo = UtilServerCatchScatterThrowIn.findScatterCoordinate(fCoordinateFrom, playerScatter, 1);
			getResult().addReport(new ReportScatterPlayer(fCoordinateFrom, fCoordinateTo, new Direction[] { playerScatter },
					new int[] { scatterRoll }));
			if (!FieldCoordinateBounds.FIELD.isInBounds(fCoordinateTo)) {
				publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeCrowdPush()));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFallDown);
				return;
			}
			publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, fCoordinateTo));
			Player<?> blockDefender = game.getFieldModel().getPlayer(fCoordinateTo);
			if (blockDefender != null) {
				actingPlayer.setCurrentMove(actingPlayer.getCurrentMove() + 1);
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game));
				publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, blockDefender.getId()));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
				return;
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_FALL_DOWN.addTo(jsonObject, fGotoLabelOnFallDown);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		return jsonObject;
	}

	@Override
	public StepMoveBallAndChain initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		fGotoLabelOnFallDown = IServerJsonOption.GOTO_LABEL_ON_FALL_DOWN.getFrom(game, jsonObject);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(game, jsonObject);
		return this;
	}

}

package com.balancedbytes.games.ffb.server.step.action.move;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportScatterPlayer;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the move sequence to handle skill BALL_AND_CHAIN.
 * 
 * Needs to be initialized with stepParameter DISPATCH_TO_LABEL.
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * May be initialized with stepParameter GAZE_VICTIM_ID.
 * May be initialized with stepParameter MOVE_STACK.
 * 
 * Expects stepParameter MOVE_STACK to be set by a preceding step.
 * 
 * Sets stepParameter COORDINATE_FROM for all steps on the stack.
 * Sets stepParameter COORDINATE_TO for all steps on the stack.
 * Sets stepParameter DISPATCH_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter MOVE_STACK for all steps on the stack.
 * 
 * May replace rest of move sequence with inducement sequence.
 * 
 * @author Kalimar
 */
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
    if (UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)) {
    	Direction playerScatter = null;
    	int scatterRoll = getGameState().getDiceRoller().rollThrowInDirection();
    	if (fCoordinateFrom.getX() < fCoordinateTo.getX()) {
    		playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
    	} else if (fCoordinateFrom.getX() > fCoordinateTo.getX()) {
    		playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
    	} else if (fCoordinateFrom.getY() < fCoordinateTo.getY()) {
    		playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
    	} else {  // coordinateFrom.getY() > coordinateTo.getY()
    		playerScatter = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
    	}
    	fCoordinateTo = UtilServerCatchScatterThrowIn.findScatterCoordinate(fCoordinateFrom, playerScatter, 1);
    	getResult().addReport(new ReportScatterPlayer(fCoordinateFrom, fCoordinateTo, new Direction[] { playerScatter }, new int[] { scatterRoll }));
    	if (!FieldCoordinateBounds.FIELD.isInBounds(fCoordinateTo)) {
    		publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.CROWDPUSH));
    		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFallDown);
    		return;
    	}
  		publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, fCoordinateTo));
  		Player blockDefender = game.getFieldModel().getPlayer(fCoordinateTo);
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
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fGotoLabelOnFallDown = pByteArray.getString();
  	return byteArraySerializationVersion;
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
  public StepMoveBallAndChain initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fGotoLabelOnFallDown = IServerJsonOption.GOTO_LABEL_ON_FALL_DOWN.getFrom(jsonObject);
    fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(jsonObject);
    return this;
  }
  
}

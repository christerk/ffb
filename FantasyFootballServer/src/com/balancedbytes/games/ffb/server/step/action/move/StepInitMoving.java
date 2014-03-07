package com.balancedbytes.games.ffb.server.step.action.move;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFoul;
import com.balancedbytes.games.ffb.net.commands.ClientCommandGaze;
import com.balancedbytes.games.ffb.net.commands.ClientCommandHandOver;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPass;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
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
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the move sequence.
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
public class StepInitMoving extends AbstractStep {
	
  private String fGotoLabelOnEnd;
  private FieldCoordinate[] fMoveStack;
  private String fGazeVictimId;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
		
	public StepInitMoving(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_MOVING;
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
					// optional
  				case GAZE_VICTIM_ID:
  					fGazeVictimId = (String) parameter.getValue();
  					break;
					// optional
  				case MOVE_STACK:
  					fMoveStack = (FieldCoordinate[]) parameter.getValue();
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
				case MOVE_STACK:
					fMoveStack = (FieldCoordinate[]) pParameter.getValue();
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
    if ((pReceivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
      Game game = getGameState().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      switch (pReceivedCommand.getId()) {
        case CLIENT_MOVE:
          ClientCommandMove moveCommand = (ClientCommandMove) pReceivedCommand.getCommand();
          boolean homePlayer = UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand);
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), moveCommand) && UtilServerPlayerMove.isValidMove(getGameState(), moveCommand, homePlayer) && !ArrayTool.isProvided(fMoveStack)) {
            publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, UtilServerPlayerMove.fetchMoveStack(getGameState(), moveCommand, homePlayer)));
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_BLOCK:
          ClientCommandBlock blockCommand = (ClientCommandBlock) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), blockCommand) && (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE) && !actingPlayer.hasBlocked()) {
            commandStatus = dispatchPlayerAction(PlayerAction.BLITZ);
          }
          break;
        case CLIENT_FOUL:
          ClientCommandFoul foulCommand = (ClientCommandFoul) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), foulCommand) && (actingPlayer.getPlayerAction() == PlayerAction.FOUL_MOVE) && !actingPlayer.hasFouled()) {
            commandStatus = dispatchPlayerAction(PlayerAction.FOUL);
          }
          break;
        case CLIENT_HAND_OVER:
          ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand) && ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER_MOVE) || (actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER))) {
            commandStatus = dispatchPlayerAction(PlayerAction.HAND_OVER);
          }
          break;
        case CLIENT_PASS:
          ClientCommandPass passCommand = (ClientCommandPass) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand)) {
            if (((actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) || (actingPlayer.getPlayerAction() == PlayerAction.PASS))) {
              commandStatus = dispatchPlayerAction(PlayerAction.PASS);
            }
            if (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_PASS) {
              commandStatus = dispatchPlayerAction(PlayerAction.HAIL_MARY_PASS);
            }
          }
          break;
        case CLIENT_THROW_TEAM_MATE:
          ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand) && (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE)) {
            commandStatus = dispatchPlayerAction(PlayerAction.THROW_TEAM_MATE);
          }
          break;
        case CLIENT_GAZE:
          ClientCommandGaze gazeCommand = (ClientCommandGaze) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), gazeCommand)) {
            fGazeVictimId = gazeCommand.getVictimId();
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_ACTING_PLAYER:
          ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
          if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
            UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(), actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isLeaping());
          } else {
            fEndPlayerAction = true;
          }
          commandStatus = StepCommandStatus.EXECUTE_STEP;
          break;
        case CLIENT_END_TURN:
          if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
            fEndTurn = true;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        default:
          break;
      }
    }
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }
	
	private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fEndTurn) {
    	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (fEndPlayerAction) {
    	publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (StringTool.isProvided(fGazeVictimId)) {
      game.setDefenderId(fGazeVictimId);
      actingPlayer.setPlayerAction(PlayerAction.GAZE);
      getResult().setNextAction(StepAction.NEXT_STEP);
  	} else {
	    if (ArrayTool.isProvided(fMoveStack)) {
	    	FieldCoordinate coordinateTo = fMoveStack[0];
	    	FieldCoordinate[] newMoveStack = new FieldCoordinate[0];
	    	if (fMoveStack.length > 1) {
	    		newMoveStack = new FieldCoordinate[fMoveStack.length - 1];
	    		for (int i = 0; i < newMoveStack.length; i++) {
	    			newMoveStack[i] = fMoveStack[i + 1];
	    		}
	    	}
	    	publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, newMoveStack));
	      if (FieldCoordinateBounds.FIELD.isInBounds(coordinateTo)) {
	        FieldCoordinate coordinateFrom = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
	        publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, coordinateFrom));
	      	publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, coordinateTo));
	        MoveSquare moveSquare = game.getFieldModel().getMoveSquare(coordinateTo);
	        actingPlayer.setDodging((moveSquare != null) && moveSquare.isDodging() && !actingPlayer.isLeaping());
	        actingPlayer.setGoingForIt((moveSquare != null) && moveSquare.isGoingForIt());
	        actingPlayer.setHasMoved(true);
	        game.getTurnData().setTurnStarted(true);
	        switch (actingPlayer.getPlayerAction()) {
	          case BLITZ_MOVE:
	            game.getTurnData().setBlitzUsed(true);
	            break;
	          case FOUL_MOVE:
	            game.getTurnData().setFoulUsed(true);
	            break;
	          case HAND_OVER_MOVE:
	            game.getTurnData().setHandOverUsed(true);
	            break;
	          case PASS_MOVE:
	          case THROW_TEAM_MATE_MOVE:
	            game.getTurnData().setPassUsed(true);
	            break;
            default:
            	break;
	        }
	        game.setConcessionPossible(false);
	        getResult().setNextAction(StepAction.NEXT_STEP);
	      }
	    }
  	}
  }
  
  private StepCommandStatus dispatchPlayerAction(PlayerAction pPlayerAction) {
  	publishParameter(new StepParameter(StepParameterKey.DISPATCH_PLAYER_ACTION, pPlayerAction));
  	getResult().setNextAction(StepAction.GOTO_LABEL_AND_REPEAT, fGotoLabelOnEnd);
  	return StepCommandStatus.SKIP_STEP;
  }
  
  // ByteArray serialization
    
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fGazeVictimId = pByteArray.getString();
  	fMoveStack = new FieldCoordinate[pByteArray.getByte()];
  	for (int i = 0; i < fMoveStack.length; i++) {
  		fMoveStack[i] = pByteArray.getFieldCoordinate();
  	}
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.MOVE_STACK.addTo(jsonObject, fMoveStack);
    IServerJsonOption.GAZE_VICTIM_ID.addTo(jsonObject, fGazeVictimId);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    return jsonObject;
  }
  
  @Override
  public StepInitMoving initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fMoveStack = IServerJsonOption.MOVE_STACK.getFrom(jsonObject);
    fGazeVictimId = IServerJsonOption.GAZE_VICTIM_ID.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    return this;
  }
  
}

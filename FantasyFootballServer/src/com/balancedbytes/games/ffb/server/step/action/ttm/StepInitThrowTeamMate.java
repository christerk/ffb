package com.balancedbytes.games.ffb.server.step.action.ttm;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;

/**
 * Step to init the throw team mate sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * May be initialized with stepParameter TARGET_COORDINATE.
 * May be initialized with stepParameter THROWN_PLAYER_ID.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter THROWN_PLAYER_ID for all steps on the stack.
 * Sets stepParameter THROWN_PLAYER_STATE for all steps on the stack.
 *
 * @author Kalimar
 */
public final class StepInitThrowTeamMate extends AbstractStep {
	
  private String fGotoLabelOnEnd;
  private String fThrownPlayerId;
  private FieldCoordinate fTargetCoordinate;
  private boolean fEndTurn;
  private boolean fEndPlayerAction;
	
	public StepInitThrowTeamMate(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_THROW_TEAM_MATE;
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
  				case TARGET_COORDINATE:
  					fTargetCoordinate = (FieldCoordinate) parameter.getValue();
  					break;
 					// optional
  				case THROWN_PLAYER_ID:
  					fThrownPlayerId = (String) parameter.getValue();
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
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if ((pNetCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) && UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), pNetCommand)) {
			Game game = getGameState().getGame();
			switch (pNetCommand.getId()) {
	      case CLIENT_THROW_TEAM_MATE:
	        ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pNetCommand;
	        if (UtilSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand)) {
  	        if (throwTeamMateCommand.getTargetCoordinate() != null) {
  	          if (game.isHomePlaying()) {
  	          	fTargetCoordinate = throwTeamMateCommand.getTargetCoordinate();
  	          } else {
  	          	fTargetCoordinate = throwTeamMateCommand.getTargetCoordinate().transform();
  	          }
  	        } else {
  	          fThrownPlayerId = throwTeamMateCommand.getThrownPlayerId();
  	        }
  	        commandStatus = StepCommandStatus.EXECUTE_STEP;
	        }
	        break;
        case CLIENT_ACTING_PLAYER:
          ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pNetCommand;
          if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
          	UtilSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(), actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isLeaping());
          } else {
          	fEndPlayerAction = true;
          }
	        commandStatus = StepCommandStatus.EXECUTE_STEP;
	        break;
        case CLIENT_END_TURN:
        	if (UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), pNetCommand)) {
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
    } else {
      if (StringTool.isProvided(fThrownPlayerId)) {
      	if (fTargetCoordinate != null) {
		    	game.setPassCoordinate(fTargetCoordinate);
		      game.getFieldModel().setRangeRuler(UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), game.getPassCoordinate(), true));
		      if (game.getFieldModel().getRangeRuler() != null) {
		      	getResult().setNextAction(StepAction.NEXT_STEP);
		      }
			  } else {
		      game.setDefenderId(fThrownPlayerId);
		      PlayerState thrownPlayerState = game.getFieldModel().getPlayerState(game.getDefender());
		      publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, fThrownPlayerId));
		      publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_STATE, thrownPlayerState));
	        FieldCoordinate thrownPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
	        publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, thrownPlayerCoordinate));
	        boolean thrownPlayerHasBall = thrownPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate()) && !game.getFieldModel().isBallMoving();
	        publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, thrownPlayerHasBall));
		      game.getFieldModel().setPlayerState(game.getDefender(), thrownPlayerState.changeBase(PlayerState.PICKED_UP));
		      UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.THROW_TEAM_MATE, false);
		    }
      }
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addFieldCoordinate(fTargetCoordinate);
  	pByteList.addString(fThrownPlayerId);
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addBoolean(fEndPlayerAction);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fTargetCoordinate = pByteArray.getFieldCoordinate();
  	fThrownPlayerId = pByteArray.getString();
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}

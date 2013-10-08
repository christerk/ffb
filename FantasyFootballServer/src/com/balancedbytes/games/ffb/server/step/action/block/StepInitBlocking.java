package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
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
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step to init the block sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * May be initialized with stepParameter BLOCK_DEFENDER_ID.
 * May be initialized with stepParameter USING_STAB.
 * 
 * Sets stepParameter DEFENDER_POSITION for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter OLD_DEFENDER_STATE for all steps on the stack.
 * Sets stepParameter USING_STAB for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepInitBlocking extends AbstractStep {
	
	private String fGotoLabelOnEnd;
  private String fBlockDefenderId;
  private boolean fUsingStab;
  private String fMultiBlockDefenderId;
  private boolean fEndTurn;
  private boolean fEndPlayerAction;
		
	public StepInitBlocking(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_BLOCKING;
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
  				case BLOCK_DEFENDER_ID:
  					fBlockDefenderId = (String) parameter.getValue();
  					break;
					// optional
  				case USING_STAB:
						fUsingStab = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
  					break;
					// optional
  				case MULTI_BLOCK_DEFENDER_ID:
  					fMultiBlockDefenderId = (String) parameter.getValue();
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
			switch (pNetCommand.getId()) {
			  case CLIENT_BLOCK:
			    ClientCommandBlock blockCommand = (ClientCommandBlock) pNetCommand;
	        if (UtilSteps.checkCommandWithActingPlayer(getGameState(), blockCommand)) {
  			    if ((fMultiBlockDefenderId == null) || !fMultiBlockDefenderId.equals(blockCommand.getDefenderId())) { 
  			    	fBlockDefenderId = blockCommand.getDefenderId();
  				    fUsingStab = blockCommand.isUsingStab();
  				    commandStatus = StepCommandStatus.EXECUTE_STEP;
  			    }
	        }
			    break;
        case CLIENT_END_TURN:
        	if (UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), pNetCommand)) {
        		fEndTurn = true;
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
    } else if (actingPlayer.isSufferingBloodLust() && (actingPlayer.getPlayerAction() == PlayerAction.MOVE)) {
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else {
	    Player defender = game.getPlayerById(fBlockDefenderId);
	    if (defender != null) {
	      game.setDefenderId(defender.getId());
	      actingPlayer.setStrength(UtilCards.getPlayerStrength(game, actingPlayer.getPlayer()));
	      PlayerState oldDefenderState = game.getFieldModel().getPlayerState(defender);
	      publishParameter(new StepParameter(StepParameterKey.OLD_DEFENDER_STATE, oldDefenderState));
	    	publishParameter(new StepParameter(StepParameterKey.DEFENDER_POSITION, game.getFieldModel().getPlayerCoordinate(game.getDefender())));
	      publishParameter(new StepParameter(StepParameterKey.USING_STAB, fUsingStab));
	      game.getFieldModel().setPlayerState(defender, oldDefenderState.changeBase(PlayerState.BLOCKED));
	      if (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE) {
	      	UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.BLITZ, actingPlayer.isLeaping());
	      }
	      getResult().setNextAction(StepAction.NEXT_STEP);
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
  	pByteList.addString(fBlockDefenderId);
  	pByteList.addBoolean(fUsingStab);
  	pByteList.addString(fMultiBlockDefenderId);
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addBoolean(fEndPlayerAction);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fBlockDefenderId = pByteArray.getString();
  	fUsingStab = pByteArray.getBoolean();
  	fMultiBlockDefenderId = pByteArray.getString();
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
}

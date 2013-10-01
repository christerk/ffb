package com.balancedbytes.games.ffb.server.step.action.select;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlock;
import com.balancedbytes.games.ffb.net.commands.ClientCommandEndTurn;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFoul;
import com.balancedbytes.games.ffb.net.commands.ClientCommandGaze;
import com.balancedbytes.games.ffb.net.commands.ClientCommandHandOver;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPass;
import com.balancedbytes.games.ffb.net.commands.ClientCommandThrowTeamMate;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerConstant;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilPlayerMove;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBlock;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * Step to init the select sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * Needs to be initialized with stepParameter UPDATE_PERSISTENCE.
 *
 * Sets stepParameter BLOCK_DEFENDER_ID for all steps on the stack.
 * Sets stepParameter DISPATCH_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter FOUL_DEFENDER_ID for all steps on the stack.
 * Sets stepParameter GAZE_VICTIM_ID for all steps on the stack.
 * Sets stepParameter HAIL_MARY_PASS for all steps on the stack.
 * Sets stepParameter MOVE_STACK for all steps on the stack.
 * Sets stepParameter TARGET_COORDINATE for all steps on the stack.
 * Sets stepParameter USING_STAB for all steps on the stack.
 *
 * @author Kalimar
 */
public final class StepInitSelecting extends AbstractStep {
	
	protected String fGotoLabelOnEnd;
	protected PlayerAction fDispatchPlayerAction;
	protected boolean fEndTurn;
	protected boolean fEndPlayerAction;
	
	private transient boolean fUpdatePersistence;
	
	public StepInitSelecting(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_SELECTING;
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
  				case UPDATE_PERSISTENCE:
  					fUpdatePersistence = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if ((pNetCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) && UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), pNetCommand)) {
			Game game = getGameState().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pNetCommand.getId()) {
	      case CLIENT_ACTING_PLAYER:
	        ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pNetCommand;
	        if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
	        	UtilSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(), actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isLeaping());
	        } else {
	        	fEndPlayerAction = true;
	        }
          commandStatus = StepCommandStatus.EXECUTE_STEP;
	        break;
	      case CLIENT_MOVE:
	      	ClientCommandMove moveCommand = (ClientCommandMove) pNetCommand;
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), moveCommand) && UtilPlayerMove.isValidMove(getGameState(), moveCommand)) {
  	      	publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, UtilPlayerMove.fetchMoveStack(getGameState(), moveCommand)));
  	      	fDispatchPlayerAction = PlayerAction.MOVE;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	        break;
	      case CLIENT_FOUL:
	      	ClientCommandFoul foulCommand = (ClientCommandFoul) pNetCommand;
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), foulCommand) && !game.getTurnData().isFoulUsed()) {
  	      	publishParameter(new StepParameter(StepParameterKey.FOUL_DEFENDER_ID, foulCommand.getDefenderId()));
  	        UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.FOUL, false);
  	      	fDispatchPlayerAction = PlayerAction.FOUL;
  	      	commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	        break;
	      case CLIENT_BLOCK:
	    		ClientCommandBlock blockCommand = (ClientCommandBlock) pNetCommand;
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), blockCommand)) {
  	      	publishParameter(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, blockCommand.getDefenderId()));
  	      	publishParameter(new StepParameter(StepParameterKey.USING_STAB, blockCommand.isUsingStab()));
  	      	fDispatchPlayerAction = PlayerAction.BLOCK;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	        break;
	      case CLIENT_GAZE:
	      	ClientCommandGaze gazeCommand = (ClientCommandGaze) pNetCommand;
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), gazeCommand)) {
  	      	publishParameter(new StepParameter(StepParameterKey.GAZE_VICTIM_ID, gazeCommand.getVictimId()));
  	        UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.GAZE, false);
  	      	fDispatchPlayerAction = PlayerAction.GAZE;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	        break;
	      case CLIENT_PASS:
	      	ClientCommandPass passCommand = (ClientCommandPass) pNetCommand;
	      	boolean passAllowed = !game.getTurnData().isPassUsed() || ((actingPlayer.getPlayer() != null) && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB) || (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB)));
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), passCommand) && passAllowed) {
  	      	if (passCommand.getTargetCoordinate() != null) {
  	      		if (game.isHomePlaying()) {
  	      			publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, passCommand.getTargetCoordinate()));
  	      		} else {
  	      			publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, passCommand.getTargetCoordinate().transform()));
  	      		}
  	      	}
  	      	if ((actingPlayer.getPlayer() != null) && ((actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_PASS) || (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB) || (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
  		      	fDispatchPlayerAction = actingPlayer.getPlayerAction();
  	      	} else {
  		        UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.PASS, false);
  		      	fDispatchPlayerAction = PlayerAction.PASS;
  	      	}
            commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	      	break;
	      case CLIENT_HAND_OVER:
	      	ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pNetCommand;
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand) && !game.getTurnData().isHandOverUsed()) {
	      		Player catcher = game.getPlayerById(handOverCommand.getCatcherId());
	      		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
      			publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, catcherCoordinate));
  	        UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.HAND_OVER, false);
  	      	fDispatchPlayerAction = PlayerAction.HAND_OVER;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	      	break;
	      case CLIENT_THROW_TEAM_MATE:
	      	ClientCommandThrowTeamMate throwTeamMateCommand = (ClientCommandThrowTeamMate) pNetCommand;
	      	if (UtilSteps.checkCommandWithActingPlayer(getGameState(), throwTeamMateCommand) && !game.getTurnData().isPassUsed()) {
  	      	if (throwTeamMateCommand.getTargetCoordinate() != null) {
  	      		if (game.isHomePlaying()) {
  	      			publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, throwTeamMateCommand.getTargetCoordinate()));
  	      		} else {
  	      			publishParameter(new StepParameter(StepParameterKey.TARGET_COORDINATE, throwTeamMateCommand.getTargetCoordinate().transform()));
  	      		}
  	      	}
  	      	publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, throwTeamMateCommand.getThrownPlayerId()));
  	        UtilSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.THROW_TEAM_MATE, false);
  	      	fDispatchPlayerAction = PlayerAction.THROW_TEAM_MATE;
            commandStatus = StepCommandStatus.EXECUTE_STEP;
	      	}
	        break;
        case CLIENT_END_TURN:
        	ClientCommandEndTurn endTurnCommand = (ClientCommandEndTurn) pNetCommand;
        	if (UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), endTurnCommand)) {
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
	
	@Override
	public void start() {
  	if (fUpdatePersistence) {
  		fUpdatePersistence = false;
  		GameCache gameCache = getGameState().getServer().getGameCache();
  		gameCache.queueDbUpdate(getGameState());
  	}
	}

  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (game.isTimeoutEnforced() || fEndTurn) {
  		publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (fEndPlayerAction) {
  		publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (fDispatchPlayerAction != null) {
    	if (StringTool.isProvided(actingPlayer.getPlayerId()) && (actingPlayer.getPlayerAction() != null)) {
      	publishParameter(new StepParameter(StepParameterKey.DISPATCH_PLAYER_ACTION, fDispatchPlayerAction));
      	if (actingPlayer.isStandingUp()) {
      		prepareStandingUp();
        	getResult().setNextAction(StepAction.NEXT_STEP);
      	} else {
      		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
      	}
    	}
    } else {
    	prepareStandingUp();
      if ((actingPlayer.getPlayerAction() == PlayerAction.REMOVE_CONFUSION) || (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP) || (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ)) {
      	getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }
  }
  
  private void prepareStandingUp() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
  	if ((actingPlayer.getPlayer() != null) && (actingPlayer.getPlayerAction() != null)) {
      if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) || (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE) || (actingPlayer.getPlayerAction() == PlayerAction.BLOCK) || (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)) {
        UtilBlock.updateDiceDecorations(game);
      }
      if (actingPlayer.getPlayerAction().isMoving()) {
        if (actingPlayer.isStandingUp() && !UtilCards.hasSkill(game, actingPlayer, Skill.JUMP_UP)) {
          actingPlayer.setCurrentMove(Math.min(IServerConstant.MINIMUM_MOVE_TO_STAND_UP, actingPlayer.getPlayer().getMovement()));
          actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game));  // auto go-for-it
        }
        UtilPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
      }
  	}
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addByte((byte) ((fDispatchPlayerAction != null) ? fDispatchPlayerAction.getId() : 0));
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addBoolean(fEndPlayerAction);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fDispatchPlayerAction = PlayerAction.fromId(pByteArray.getByte());
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}

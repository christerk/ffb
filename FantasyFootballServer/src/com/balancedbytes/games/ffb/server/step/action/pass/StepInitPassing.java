package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandHandOver;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPass;
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
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Initialization step of the pass sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * May be initialized with stepParameter CATCHER_ID.
 * May be initialized with stepParameter HAIL_MARY_PASS.
 * May be initialized with stepParameter TARGET_COORDINATE.
 * 
 * Sets stepParameter CATCHER_ID for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter TARGET_COORDINATE for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepInitPassing extends AbstractStep {
	
  private String fGotoLabelOnEnd;
  private String fCatcherId;
  private boolean fEndTurn;
  private boolean fEndPlayerAction;
	
	public StepInitPassing(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_PASSING;
	}
		
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
    	Game game = getGameState().getGame();
    	ActingPlayer actingPlayer = game.getActingPlayer();
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_END:
  					fGotoLabelOnEnd = (String) parameter.getValue();
  					break;
					// optional
  				case TARGET_COORDINATE:
  					if (parameter.getValue() != null) {
	  					FieldCoordinate targetCoordinate = (FieldCoordinate) parameter.getValue();
  	          game.setPassCoordinate(targetCoordinate);
    	        Player catcher = game.getFieldModel().getPlayer(game.getPassCoordinate());
    	        fCatcherId = ((catcher != null) ? catcher.getId() : null);
    	        if ((game.getDefender() != null) && (game.getDefenderAction() == PlayerAction.DUMP_OFF)) {
    	        	game.setThrowerId(game.getDefenderId());
    	        	game.setThrowerAction(game.getDefenderAction());
    	        } else {
    						game.setThrowerId(actingPlayer.getPlayerId());
    						game.setThrowerAction(actingPlayer.getPlayerAction());
    	        }
  	        }
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if ((pReceivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) && (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand) || (game.getTurnMode() == TurnMode.DUMP_OFF))) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_PASS:
          ClientCommandPass passCommand = (ClientCommandPass) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), passCommand) || (game.getTurnMode() == TurnMode.DUMP_OFF)) {
            if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
              game.setPassCoordinate(passCommand.getTargetCoordinate());
            } else {
              game.setPassCoordinate(passCommand.getTargetCoordinate().transform());
            }
            Player catcher = game.getFieldModel().getPlayer(game.getPassCoordinate());
            fCatcherId = ((catcher != null) ? catcher.getId() : null);
            if ((game.getDefender() != null) && (game.getDefenderAction() == PlayerAction.DUMP_OFF)) {
              game.setThrowerId(game.getDefenderId());
              game.setThrowerAction(game.getDefenderAction());
            } else {
              game.setThrowerId(actingPlayer.getPlayerId());
              game.setThrowerAction(actingPlayer.getPlayerAction());
            }
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        case CLIENT_HAND_OVER:
          ClientCommandHandOver handOverCommand = (ClientCommandHandOver) pReceivedCommand.getCommand();
          if (UtilServerSteps.checkCommandWithActingPlayer(getGameState(), handOverCommand)) {
            fCatcherId = handOverCommand.getCatcherId();
            game.setThrowerId(actingPlayer.getPlayerId());
            game.setThrowerAction(PlayerAction.HAND_OVER);
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
    if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
    	return;
    }
    Player catcher = game.getPlayerById(fCatcherId);
    if (catcher != null) {
    	publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, catcher.getId()));
    }
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fEndTurn) {
    	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if (fEndPlayerAction) {
    	publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else if ((game.getThrower() == actingPlayer.getPlayer()) && actingPlayer.isSufferingBloodLust() && !actingPlayer.hasFed()) {
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    } else {
	    FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
      if ((PlayerAction.HAND_OVER == game.getThrowerAction()) && (game.getThrower() == actingPlayer.getPlayer()) && (catcher != null)) {
        actingPlayer.setHasPassed(true);
        game.setConcessionPossible(false);
        game.getTurnData().setHandOverUsed(true);
        game.getTurnData().setTurnStarted(true);
        getResult().setNextAction(StepAction.NEXT_STEP);
        return;
      }
      if ((game.getPassCoordinate() != null) && (game.getThrower() == actingPlayer.getPlayer()) && ((PlayerAction.THROW_BOMB == game.getThrowerAction()) && (UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false) != null)) || (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())) {
        actingPlayer.setHasPassed(true);
        game.getTurnData().setTurnStarted(true);
        game.setConcessionPossible(false);
        if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
        	game.getFieldModel().setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
        }
        getResult().setNextAction(StepAction.NEXT_STEP);
        return;
      }
      if ((game.getPassCoordinate() != null) && (game.getThrower() == actingPlayer.getPlayer()) && ((PlayerAction.PASS == game.getThrowerAction()) && (UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false) != null)) || (PlayerAction.HAIL_MARY_PASS == game.getThrowerAction())) {
        actingPlayer.setHasPassed(true);
        game.getTurnData().setTurnStarted(true);
        game.setConcessionPossible(false);
        game.getTurnData().setPassUsed(true);
        if (PlayerAction.PASS == game.getThrowerAction()) {
        	game.getFieldModel().setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
        }
        getResult().setNextAction(StepAction.NEXT_STEP);
        return;
      }
      if ((game.getPassCoordinate() != null) && (((PlayerAction.THROW_BOMB == game.getThrowerAction()) || (PlayerAction.DUMP_OFF == game.getThrowerAction())) && (UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false) != null)) || (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())) {
      	if (game.getThrower() == actingPlayer.getPlayer()) {
      		actingPlayer.setHasPassed(true);
      	}
      	if (PlayerAction.HAIL_MARY_BOMB != game.getThrowerAction()) {
        	game.getFieldModel().setRangeRuler(UtilRangeRuler.createRangeRuler(game, game.getThrower(), game.getPassCoordinate(), false));
        }
        getResult().setNextAction(StepAction.NEXT_STEP);
        return;
	    }
    }
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    return jsonObject;
  }
  
  @Override
  public StepInitPassing initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    return this;
  }
  
}

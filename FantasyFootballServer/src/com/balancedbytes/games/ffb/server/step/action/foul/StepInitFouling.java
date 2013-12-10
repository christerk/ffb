package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandActingPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandFoul;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
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
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the foul sequence.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * May be initialized with stepParameter FOUL_DEFENDER_ID.
 *
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 *
 * @author Kalimar
 */
public class StepInitFouling extends AbstractStep {
	
	private String fGotoLabelOnEnd;
	private String fFoulDefenderId;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	
	public StepInitFouling(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_FOULING;
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
  				case FOUL_DEFENDER_ID:
  					fFoulDefenderId = (String) parameter.getValue();
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
	      case CLIENT_FOUL:
	        ClientCommandFoul foulCommand = (ClientCommandFoul) pNetCommand;
	        if (UtilSteps.checkCommandWithActingPlayer(getGameState(), foulCommand)) {
  	        fFoulDefenderId = foulCommand.getDefenderId();
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
    	Player foulDefender = game.getPlayerById(fFoulDefenderId);
    	if ((actingPlayer.getPlayer() != null) && !actingPlayer.hasFouled() && (foulDefender != null) && !UtilCards.hasCard(game, foulDefender, Card.GOOD_OLD_MAGIC_CODPIECE)) {
	    	game.setDefenderId(fFoulDefenderId);
        actingPlayer.setHasFouled(true);
	      game.getTurnData().setTurnStarted(true);
	      game.setConcessionPossible(false);
	      PlayerResult playerResult = game.getGameResult().getPlayerResult(actingPlayer.getPlayer());
	      playerResult.setFouls(playerResult.getFouls() + 1);
	      game.getTurnData().setFoulUsed(true);
	    	getResult().setNextAction(StepAction.NEXT_STEP);
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
  	pByteList.addString(fFoulDefenderId);
  	pByteList.addBoolean(fEndTurn);
  	pByteList.addBoolean(fEndPlayerAction);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fFoulDefenderId = pByteArray.getString();
  	fEndTurn = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.FOUL_DEFENDER_ID.addTo(jsonObject, fFoulDefenderId);
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    return jsonObject;
  }
  
  @Override
  public StepInitFouling initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fFoulDefenderId = IServerJsonOption.FOUL_DEFENDER_ID.getFrom(jsonObject);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(jsonObject);
    return this;
  }

}

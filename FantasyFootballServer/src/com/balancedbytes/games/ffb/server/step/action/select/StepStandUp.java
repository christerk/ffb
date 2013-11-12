package com.balancedbytes.games.ffb.server.step.action.select;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportStandUpRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerConstant;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in select sequence to stand up a prone player.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 *  
 * @author Kalimar
 */
public final class StepStandUp extends AbstractStepWithReRoll {
	
	protected String fGotoLabelOnFailure;
	
	public StepStandUp(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.STAND_UP;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnFailure)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    game.getTurnData().setTurnStarted(true);
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    if ((actingPlayer.isStandingUp() && !actingPlayer.hasMoved()) || (ReRolledAction.STAND_UP == getReRolledAction())) {
      actingPlayer.setHasMoved(true);
      game.setConcessionPossible(false);
      boolean rollStandUp = (actingPlayer.getPlayer().getMovement() < IServerConstant.MINIMUM_MOVE_TO_STAND_UP);
      if (rollStandUp) {
        if (ReRolledAction.STAND_UP == getReRolledAction()) {
          if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
            rollStandUp = false;
          }
        }
        if (rollStandUp) {
          int roll = getGameState().getDiceRoller().rollSkill();
          boolean successful = DiceInterpreter.getInstance().isStandUpSuccessful(roll);
          boolean reRolled = ((getReRolledAction() == ReRolledAction.STAND_UP) && (getReRollSource() != null));
          getResult().addReport(new ReportStandUpRoll(actingPlayer.getPlayerId(), successful, roll, reRolled));
          if (successful) {
            actingPlayer.setStandingUp(false);
            if (playerState.isRooted()) {
            	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
            } else {
            	getResult().setNextAction(StepAction.NEXT_STEP);
            }
          } else {
            if ((getReRolledAction() == ReRolledAction.STAND_UP) || !UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.STAND_UP, 4, false)) {
              rollStandUp = false;
              switch (actingPlayer.getPlayerAction()) {
                case BLITZ:
                case BLITZ_MOVE:
                  game.getTurnData().setBlitzUsed(true);
                  break;
                case PASS:
                case PASS_MOVE:
                case THROW_TEAM_MATE:
                case THROW_TEAM_MATE_MOVE:
                  game.getTurnData().setPassUsed(true);
                  break;
                case HAND_OVER:
                case HAND_OVER_MOVE:
                  game.getTurnData().setHandOverUsed(true);
                  break;
                case FOUL:
                case FOUL_MOVE:
                  game.getTurnData().setFoulUsed(true);
                  break;
                default:
                	break;
              }
            }
          }
        }
        if (!rollStandUp) {
          game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.PRONE).changeActive(false));
          publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        }
      } else {
      	getResult().setNextAction(StepAction.NEXT_STEP);
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = toJsonValueTemp();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  public StepStandUp initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
  	
}

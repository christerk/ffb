package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogTouchbackParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTouchback;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in kickoff sequence to handle touchback.
 * 
 * Expects stepParameter TOUCHBACK to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepTouchback extends AbstractStep {
	
	protected boolean fTouchback;
	protected FieldCoordinate fTouchbackCoordinate;
	
	public StepTouchback(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.TOUCHBACK;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
	      case CLIENT_TOUCHBACK:
	        ClientCommandTouchback touchbackCommand = (ClientCommandTouchback) pNetCommand;
	        if (UtilSteps.checkCommandIsFromHomePlayer(getGameState(), pNetCommand)) {
	        	fTouchbackCoordinate = touchbackCommand.getBallCoordinate();
	        } else {
	        	fTouchbackCoordinate = touchbackCommand.getBallCoordinate().transform();
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
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case TOUCHBACK:
					fTouchback = (Boolean) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}

  private void executeStep() {
  	
    boolean doNextStep = true;
    Game game = getGameState().getGame();
    
    if (fTouchback) {
      
      if (fTouchbackCoordinate == null) {
        game.getFieldModel().setBallCoordinate(null);
        game.setTurnMode(TurnMode.TOUCHBACK);
        game.setDialogParameter(new DialogTouchbackParameter());
        doNextStep = false;
      } else {
        UtilDialog.hideDialog(getGameState());
        game.getFieldModel().setBallCoordinate(fTouchbackCoordinate);
        Player player = game.getFieldModel().getPlayer(fTouchbackCoordinate);
        PlayerState playerState = game.getFieldModel().getPlayerState(player);
        if ((player != null) && !UtilCards.hasSkill(game, player, Skill.NO_HANDS) && playerState.hasTacklezones()) {
        	game.getFieldModel().setBallMoving(false);
          getResult().setSound(Sound.CATCH);
        } else {
          publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_KICKOFF));
        }
        game.setTurnMode(TurnMode.REGULAR);
      }

    }
    
    if (doNextStep) {
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  	
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fTouchback);
  	pByteList.addFieldCoordinate(fTouchbackCoordinate);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fTouchback = pByteArray.getBoolean();
  	fTouchbackCoordinate = pByteArray.getFieldCoordinate();
  	return byteArraySerializationVersion;
  }

}

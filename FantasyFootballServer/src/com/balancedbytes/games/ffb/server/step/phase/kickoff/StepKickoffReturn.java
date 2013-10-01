package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogKickoffReturnParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * Step in kickoff sequence to handle KICKOFF_RETURN skill.
 * 
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 *   (parameter is consumed on TurnMode.KICKOFF_RETURN)
 * Expects stepParameter END_TURN to be set by a preceding step.
 *   (parameter is consumed on TurnMode.KICKOFF_RETURN)
 * Expects stepParameter TOUCHBACK to be set by a preceding step.
 * 
 * May push new select sequence on the stack.
 * 
 * @author Kalimar
 */
public final class StepKickoffReturn extends AbstractStep {
	
  protected boolean fTouchback;
  protected boolean fEndPlayerAction;
  protected boolean fEndTurn;
  
	public StepKickoffReturn(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.KICKOFF_RETURN;
	}
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
    Game game = getGameState().getGame();
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case END_PLAYER_ACTION:
					fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
						pParameter.consume();
					}
					return true;
				case END_TURN:
					fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
						pParameter.consume();
					}
					return true;
				case TOUCHBACK:
					fTouchback = (Boolean) pParameter.getValue();
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

  private void executeStep() {

    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    
    if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
    	
    	if (fEndPlayerAction && !actingPlayer.hasActed()) {
        UtilSteps.changePlayerAction(this, null, null, false);
        getGameState().pushCurrentStepOnStack();
        SequenceGenerator.getInstance().pushSelectSequence(getGameState(), false);
        
    	} else {
    		
      	if (fEndPlayerAction || fEndTurn) {
          UtilSteps.changePlayerAction(this, null, null, false);
  	    	game.setHomePlaying(!game.isHomePlaying());
  	      game.setTurnMode(TurnMode.KICKOFF);
  	      UtilPlayer.refreshPlayersForTurnStart(game);
  	      game.getFieldModel().clearTrackNumbers();
      	}
      	
    	}
    	
    
    } else {
    
      Team kickoffReturnTeam = game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
      Team otherTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
      Player kickoffReturnPlayer = null;
      List<Player> passivePlayers = new ArrayList<Player>();
      for (Player player : kickoffReturnTeam.getPlayers()) {
        FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
        if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate()) {
          if (UtilCards.hasSkill(game, player, Skill.KICK_OFF_RETURN)) {
            FieldCoordinateBounds losBounds = game.isHomePlaying() ? FieldCoordinateBounds.LOS_AWAY : FieldCoordinateBounds.LOS_HOME;
            if (losBounds.isInBounds(playerCoordinate)) {
              passivePlayers.add(player);
            } else {
              if (UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, playerCoordinate, false).length > 0) {
                passivePlayers.add(player);
              } else {
                kickoffReturnPlayer = player;
              }
            }
          } else {
            passivePlayers.add(player);
          }
        }
      }
      
      if ((kickoffReturnPlayer != null) && !fTouchback) {
      	
        for (Player player : passivePlayers) {
          PlayerState playerState = game.getFieldModel().getPlayerState(player);
          game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
        }
        game.setHomePlaying(!game.isHomePlaying());
        game.setTurnMode(TurnMode.KICKOFF_RETURN);
        UtilDialog.showDialog(getGameState(), new DialogKickoffReturnParameter());
        
        getGameState().pushCurrentStepOnStack();
        SequenceGenerator.getInstance().pushSelectSequence(getGameState(), false);
        
      }

    }

    getResult().setNextAction(StepAction.NEXT_STEP);

  }
    
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fTouchback);
  	pByteList.addBoolean(fEndPlayerAction);
  	pByteList.addBoolean(fEndTurn);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fTouchback = pByteArray.getBoolean();
  	fEndPlayerAction = pByteArray.getBoolean();
  	fEndTurn = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}

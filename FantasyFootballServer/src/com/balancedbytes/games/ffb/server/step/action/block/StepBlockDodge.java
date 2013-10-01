package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilPushback;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in block sequence to handle skill DODGE.
 *
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepBlockDodge extends AbstractStep {
	
	private Boolean fUsingDodge;
	private PlayerState fOldDefenderState;
	
	public StepBlockDodge(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BLOCK_DODGE;
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
				case CLIENT_USE_SKILL:
			    ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pNetCommand;
			    if (Skill.DODGE == useSkillCommand.getSkill()) {
			    	fUsingDodge = useSkillCommand.isSkillUsed();
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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case OLD_DEFENDER_STATE:
					fOldDefenderState = (PlayerState) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
		
  private void executeStep() {
    findDodgeChoice();
    Game game = getGameState().getGame();
    if (fUsingDodge == null) {
      UtilDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), Skill.DODGE, 0));
    } else {
      getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.DODGE, fUsingDodge, SkillUse.AVOID_FALLING));
      if (fUsingDodge) {
        game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
      } else {
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
        game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
      }
      publishParameters(UtilBlockSequence.initPushback(this));
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  private void findDodgeChoice() {
    
    // ask for dodge only when:
    // 1: The push is a potential chainpush, the three "opposite" squares are
    //    occupied.
    // 2: It is the first turn after kickoff and a defending player has the
    //    potential to be pushed over the middle-line into the attackers half
    // 3: There is a possibility that you would be pushed next to the sideline.
    //    Which is you are standing one square away from sideline and the opponent
    //    is pushing from the same row or from the row more infield.

    if (fUsingDodge == null) {
    
      boolean chainPush = false;
      boolean sidelinePush = false;
      boolean attackerHalfPush = false;
      Game game = getGameState().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
     
      Player attacker = actingPlayer.getPlayer();
      FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(attacker);
      FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());    
      PushbackSquare startingSquare = UtilPushback.findStartingSquare(attackerCoordinate, defenderCoordinate, game.isHomePlaying());
      
      PushbackSquare[] regularPushbackSquares = UtilPushback.findPushbackSquares(game, startingSquare, PushbackMode.REGULAR);
      if (ArrayTool.isProvided(regularPushbackSquares)) {
        for (PushbackSquare pushbackSquare : regularPushbackSquares) {
          FieldCoordinate coordinate = pushbackSquare.getCoordinate();
          if (game.getFieldModel().getPlayer(coordinate) != null) {
            chainPush = true;
          }
        }
      }
  
      PushbackSquare[] grabPushbackSquares = regularPushbackSquares;
      if ((actingPlayer.getPlayerAction() == PlayerAction.BLOCK) && UtilCards.hasSkill(game, attacker, Skill.GRAB) && !UtilCards.hasSkill(game, game.getDefender(), Skill.SIDE_STEP)) {
        grabPushbackSquares = UtilPushback.findPushbackSquares(game, startingSquare, PushbackMode.GRAB);
      }
      if (ArrayTool.isProvided(regularPushbackSquares)) {
        for (PushbackSquare pushbackSquare : grabPushbackSquares) {
          FieldCoordinate coordinate = pushbackSquare.getCoordinate();
          if (FieldCoordinateBounds.SIDELINE_LOWER.isInBounds(coordinate) || FieldCoordinateBounds.SIDELINE_UPPER.isInBounds(coordinate) || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(coordinate) || FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(coordinate)) {
            sidelinePush = true;
          }
          if ((game.getTeamHome().hasPlayer(attacker) && FieldCoordinateBounds.HALF_HOME.isInBounds(coordinate) && game.getTurnDataHome().isFirstTurnAfterKickoff()) || (game.getTeamAway().hasPlayer(attacker) && FieldCoordinateBounds.HALF_AWAY.isInBounds(coordinate) && game.getTurnDataAway().isFirstTurnAfterKickoff())) {
            attackerHalfPush = true;
          }
        }
      }
      
      if (!chainPush && !sidelinePush && !attackerHalfPush) {
      	fUsingDodge = true;
      }
      
    }
    
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fUsingDodge);
  	pByteList.addSmallInt((fOldDefenderState != null) ? fOldDefenderState.getId() : 0);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fUsingDodge = pByteArray.getBoolean();
  	int playerStateId = pByteArray.getSmallInt();
  	fOldDefenderState = (playerStateId > 0) ? new PlayerState(playerStateId) : null;
  	return byteArraySerializationVersion;
  }

}

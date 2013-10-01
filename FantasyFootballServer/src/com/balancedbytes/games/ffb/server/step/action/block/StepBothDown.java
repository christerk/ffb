package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in block sequence to handle both down block result.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepBothDown extends AbstractStep {
	
	private PlayerState fOldDefenderState;
	
	public StepBothDown(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BOTH_DOWN;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
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
    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
    if (!UtilCards.hasSkill(game, game.getDefender(), Skill.BLOCK)) {
      game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
    } else {
      game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
    }
    if (!UtilCards.hasSkill(game, actingPlayer, Skill.BLOCK)) {
      game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), attackerState.changeBase(PlayerState.FALLING));
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addSmallInt((fOldDefenderState != null) ? fOldDefenderState.getId() : 0);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	int playerStateId = pByteArray.getSmallInt();
  	fOldDefenderState = (playerStateId > 0) ? new PlayerState(playerStateId) : null;
  	return byteArraySerializationVersion;
  }

}

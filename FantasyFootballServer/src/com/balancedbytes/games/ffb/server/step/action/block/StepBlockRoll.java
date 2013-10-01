package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogBlockRollParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockChoice;
import com.balancedbytes.games.ffb.report.ReportBlock;
import com.balancedbytes.games.ffb.report.ReportBlockRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilBlock;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in block sequence to handle the block roll.
 * 
 * Sets stepParameter BLOCK_DICE_INDEX for all steps on the stack.
 * Sets stepParameter BLOCK_RESULT for all steps on the stack.
 * Sets stepParameter BLOCK_ROLL for all steps on the stack.
 * Sets stepParameter NR_OF_BLOCK_DICE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepBlockRoll extends AbstractStepWithReRoll {
	
  private int fNrOfBlockDice;
  private int[] fBlockRoll;
  private int fBlockDiceIndex;
  private BlockResult fBlockResult;
	
	public StepBlockRoll(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BLOCK_ROLL;
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
				case CLIENT_BLOCK_CHOICE:
			    ClientCommandBlockChoice blockChoiceCommand = (ClientCommandBlockChoice) pNetCommand;
			    fBlockDiceIndex = blockChoiceCommand.getDiceIndex();
			    fBlockResult = BlockResult.fromRoll(fBlockRoll[fBlockDiceIndex]);
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
    if (fBlockResult == null) {
      boolean doRoll = true;
      if (ReRolledAction.BLOCK == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doRoll = false;
          showBlockRollDialog(doRoll);
        }
      }
      if (doRoll) {
        game.getFieldModel().clearDiceDecorations();
        fNrOfBlockDice = UtilBlock.findNrOfBlockDice(game, actingPlayer.getPlayer(), actingPlayer.getStrength(), game.getDefender(), (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK));
        fBlockRoll = getGameState().getDiceRoller().rollBlockDice(fNrOfBlockDice);
        getResult().addReport(new ReportBlock(game.getDefenderId()));
        getResult().setSound(Sound.BLOCK);
        showBlockRollDialog(doRoll);
      }
    } else {
      publishParameter(new StepParameter(StepParameterKey.NR_OF_BLOCK_DICE, fNrOfBlockDice));
      publishParameter(new StepParameter(StepParameterKey.BLOCK_ROLL, fBlockRoll));
	    publishParameter(new StepParameter(StepParameterKey.BLOCK_DICE_INDEX, fBlockDiceIndex));
	    publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, fBlockResult));
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private void showBlockRollDialog(boolean pDoRoll) {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean teamReRollOption = (getReRollSource() == null) && !game.getTurnData().isReRollUsed() && (game.getTurnData().getReRolls() > 0);
    boolean proReRollOption = (getReRollSource() == null) && UtilCards.hasUnusedSkill(game, actingPlayer, Skill.PRO);
    String teamId = game.isHomePlaying() ? game.getTeamHome().getId() : game.getTeamAway().getId();
    if ((fNrOfBlockDice < 0) && (!pDoRoll || (getReRollSource() != null) || (!teamReRollOption && !proReRollOption))) {
      teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
      teamReRollOption = false;
      proReRollOption = false;
    }
    getResult().addReport(new ReportBlockRoll(teamId, fBlockRoll));
    UtilDialog.showDialog(getGameState(), new DialogBlockRollParameter(teamId, fNrOfBlockDice, fBlockRoll, teamReRollOption, proReRollOption));
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addByte((byte) fNrOfBlockDice);
  	pByteList.addByteArray(fBlockRoll);
  	pByteList.addByte((byte) fBlockDiceIndex);
  	pByteList.addByte((byte) ((fBlockResult != null) ? fBlockResult.getId() : 0));
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fNrOfBlockDice = pByteArray.getByte();
  	fBlockRoll = pByteArray.getByteArrayAsIntArray();
  	fBlockDiceIndex = pByteArray.getByte();
  	fBlockResult = BlockResult.fromId(pByteArray.getByte());
  	return byteArraySerializationVersion;
  }

}

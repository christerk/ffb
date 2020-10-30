package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.BlockResultFactory;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.dialog.DialogBlockRollParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockChoice;
import com.balancedbytes.games.ffb.report.ReportBlock;
import com.balancedbytes.games.ffb.report.ReportBlockRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	
  private int fNrOfDice;
  private int[] fBlockRoll;
  private int fDiceIndex;
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_BLOCK_CHOICE:
          ClientCommandBlockChoice blockChoiceCommand = (ClientCommandBlockChoice) pReceivedCommand.getCommand();
          fDiceIndex = blockChoiceCommand.getDiceIndex();
          fBlockResult = new BlockResultFactory().forRoll(fBlockRoll[fDiceIndex]);
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
        if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          doRoll = false;
          showBlockRollDialog(doRoll);
        }
      }
      if (doRoll) {
        game.getFieldModel().clearDiceDecorations();
        fNrOfDice = ServerUtilBlock.findNrOfBlockDice(game, actingPlayer.getPlayer(), actingPlayer.getStrength(), game.getDefender(), (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK));
        fBlockRoll = getGameState().getDiceRoller().rollBlockDice(fNrOfDice);
        getResult().addReport(new ReportBlock(game.getDefenderId()));
        getResult().setSound(SoundId.BLOCK);
        showBlockRollDialog(doRoll);
      }
    } else {
      publishParameter(new StepParameter(StepParameterKey.NR_OF_DICE, fNrOfDice));
      publishParameter(new StepParameter(StepParameterKey.BLOCK_ROLL, fBlockRoll));
	    publishParameter(new StepParameter(StepParameterKey.DICE_INDEX, fDiceIndex));
	    publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, fBlockResult));
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private void showBlockRollDialog(boolean pDoRoll) {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean teamReRollOption = (getReRollSource() == null) && !game.getTurnData().isReRollUsed() && (game.getTurnData().getReRolls() > 0);
    boolean proReRollOption = (getReRollSource() == null) && UtilCards.hasUnusedSkill(game, actingPlayer, ServerSkill.PRO);
    String teamId = game.isHomePlaying() ? game.getTeamHome().getId() : game.getTeamAway().getId();
    if ((fNrOfDice < 0) && (!pDoRoll || (getReRollSource() != null) || (!teamReRollOption && !proReRollOption))) {
      teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
      teamReRollOption = false;
      proReRollOption = false;
    }
    getResult().addReport(new ReportBlockRoll(teamId, fBlockRoll));
    UtilServerDialog.showDialog(getGameState(), new DialogBlockRollParameter(teamId, fNrOfDice, fBlockRoll, teamReRollOption, proReRollOption), (fNrOfDice < 0));
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
    IServerJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
    IServerJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
    IServerJsonOption.BLOCK_RESULT.addTo(jsonObject, fBlockResult);
    return jsonObject;
  }
  
  @Override
  public StepBlockRoll initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fNrOfDice = IServerJsonOption.NR_OF_DICE.getFrom(jsonObject);
    fBlockRoll = IServerJsonOption.BLOCK_ROLL.getFrom(jsonObject);
    fDiceIndex = IServerJsonOption.DICE_INDEX.getFrom(jsonObject);
    fBlockResult = (BlockResult) IServerJsonOption.BLOCK_RESULT.getFrom(jsonObject);
    return this;
  }

}

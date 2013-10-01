package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogDefenderActionParameter;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in block sequence to handle skill DUMP_OFF.
 * 
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepDumpOff extends AbstractStep {
	
	private Boolean fUsingDumpOff;
	private FieldCoordinate fDefenderPosition;
	private TurnMode fOldTurnMode;

	public StepDumpOff(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.DUMP_OFF;
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
			    if (Skill.DUMP_OFF == useSkillCommand.getSkill()) {
			    	fUsingDumpOff = useSkillCommand.isSkillUsed();
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
				case DEFENDER_POSITION:
					fDefenderPosition = (FieldCoordinate) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
  private void executeStep() {
    
  	Game game = getGameState().getGame();
    
    if (game.getTurnMode() == TurnMode.DUMP_OFF) {
    	game.setTurnMode(fOldTurnMode);
      getResult().setNextAction(StepAction.NEXT_STEP);
    	
    } else if (fUsingDumpOff == null) {
      if (UtilCards.hasSkill(game, game.getDefender(), Skill.DUMP_OFF) && (fDefenderPosition != null) && fDefenderPosition.equals(game.getFieldModel().getBallCoordinate())
        && !(game.getFieldModel().getPlayerState(game.getDefender()).isConfused() || game.getFieldModel().getPlayerState(game.getDefender()).isHypnotized())) {
        UtilDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), Skill.DUMP_OFF, 0));
        getResult().setNextAction(StepAction.CONTINUE);
      } else {
      	fUsingDumpOff = false;
        getResult().setNextAction(StepAction.NEXT_STEP);
      }

    } else if (fUsingDumpOff) {
    	fOldTurnMode = game.getTurnMode();
    	game.setTurnMode(TurnMode.DUMP_OFF);
    	game.setThrowerId(game.getDefenderId());
    	game.setThrowerAction(PlayerAction.DUMP_OFF);
    	game.setDefenderAction(PlayerAction.DUMP_OFF);
      UtilDialog.showDialog(getGameState(), new DialogDefenderActionParameter());
    	getGameState().pushCurrentStepOnStack();
      SequenceGenerator.getInstance().pushPassSequence(getGameState());
      getResult().setNextAction(StepAction.NEXT_STEP);
    
    } else {
      getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.DUMP_OFF, false, null));
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
    
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fUsingDumpOff);
  	pByteList.addFieldCoordinate(fDefenderPosition);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fUsingDumpOff = pByteArray.getBoolean();
  	fDefenderPosition = pByteArray.getFieldCoordinate();
  	return byteArraySerializationVersion;
  }

}

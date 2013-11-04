package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogApothecaryChoiceParameter;
import com.balancedbytes.games.ffb.dialog.DialogUseApothecaryParameter;
import com.balancedbytes.games.ffb.dialog.DialogUseIgorParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandApothecaryChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseApothecary;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.report.ReportApothecaryChoice;
import com.balancedbytes.games.ffb.report.ReportApothecaryRoll;
import com.balancedbytes.games.ffb.report.ReportInducement;
import com.balancedbytes.games.ffb.server.ApothecaryStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilInducementUse;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in any sequence to handle the apothecary.
 * Offers different modes (ATTACKER, CROWDPUSH, DEFENDER) to modify the behavior.
 * 
 * Needs to be initialized with stepParameter APOTHECARY_MODE.
 * 
 * Expects stepParameter INJURY_RESULT to be set by a preceding step.
 *   (InjuryResult.getApothecaryMode() must match ApothecaryMode of this step)
 * Expects stepParameter USING_PILING_ON to be set by a preceding step (mode DEFENDER).
 * 
 * @author Kalimar
 */
public class StepApothecary extends AbstractStep {
	
	private ApothecaryMode fApothecaryMode;
	private InjuryResult fInjuryResult;
	private boolean fShowReport;
	
  public StepApothecary(GameState pGameState) {
		super(pGameState);
		fShowReport = true;
	}
  
  public StepId getId() {
  	return StepId.APOTHECARY;
  }
  
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case APOTHECARY_MODE:
  					fApothecaryMode = (ApothecaryMode) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (fApothecaryMode == null) {
			throw new StepException("StepParameter " + StepParameterKey.APOTHECARY_MODE + " is not initialized.");
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
			  case CLIENT_APOTHECARY_CHOICE:
			    ClientCommandApothecaryChoice apothecaryChoiceCommand = (ClientCommandApothecaryChoice) pNetCommand;
			    if ((fInjuryResult != null) && StringTool.isEqual(apothecaryChoiceCommand.getPlayerId(), fInjuryResult.getDefenderId())) {
			      handleApothecaryChoice(apothecaryChoiceCommand.getPlayerState(), apothecaryChoiceCommand.getSeriousInjury());
			      commandStatus = StepCommandStatus.EXECUTE_STEP;
			    }
			    break;
	      case CLIENT_USE_APOTHECARY:
	        ClientCommandUseApothecary useApothecaryCommand = (ClientCommandUseApothecary) pNetCommand;
	        if ((fInjuryResult != null) && StringTool.isEqual(useApothecaryCommand.getPlayerId(), fInjuryResult.getDefenderId())) {
	          fInjuryResult.setApothecaryStatus(useApothecaryCommand.isApothecaryUsed() ? ApothecaryStatus.USE_APOTHECARY : ApothecaryStatus.DO_NOT_USE_APOTHECARY);
			      commandStatus = StepCommandStatus.EXECUTE_STEP;
	        }
	        break;
	      case CLIENT_USE_INDUCEMENT:
	        ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pNetCommand;
	        if (InducementType.IGOR == inducementCommand.getInducementType()) {
	          if ((fInjuryResult != null) && (fInjuryResult.getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_IGOR_USE)) {
	            if (inducementCommand.hasPlayerId(fInjuryResult.getDefenderId())) {
	              fInjuryResult.setApothecaryStatus(ApothecaryStatus.USE_IGOR);
	            } else {
	              fInjuryResult.setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_IGOR);
	            }
				      commandStatus = StepCommandStatus.EXECUTE_STEP;
	          }
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
				case INJURY_RESULT:
					InjuryResult injuryResult = (InjuryResult) pParameter.getValue();
					if ((injuryResult != null) && (fApothecaryMode == injuryResult.getApothecaryMode())) { 
						fInjuryResult = injuryResult;
						return true;
					}
					return false;
				case USING_PILING_ON:
					Boolean usingPilingOn = (Boolean) pParameter.getValue();
					if ((ApothecaryMode.DEFENDER == fApothecaryMode) && (usingPilingOn != null) && !usingPilingOn) {
						fShowReport = false;
						return true;
					}
					return false;
				default:
					break;
			}
		}
		return false;
	}
	
	private void executeStep() {
    if (fInjuryResult == null) {
	  	getResult().setNextAction(StepAction.NEXT_STEP);
    } else {
      boolean doNextStep = true;
      Game game = getGameState().getGame();
      if (fInjuryResult.getApothecaryStatus() != null) {
        switch (fInjuryResult.getApothecaryStatus()) {
          case DO_REQUEST:
            if (fShowReport) {
              fInjuryResult.report(this);
            }
            UtilDialog.showDialog(getGameState(), new DialogUseApothecaryParameter(fInjuryResult.getDefenderId(), fInjuryResult.getPlayerState(), fInjuryResult.getSeriousInjury()));
            fInjuryResult.setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
            doNextStep = false;
            break;
          case USE_APOTHECARY:
            if (rollApothecary()) {
            	fInjuryResult.setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
              doNextStep = false;
            } else {
            	fInjuryResult.setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
            }
            break;
          case DO_NOT_USE_APOTHECARY:
            getResult().addReport(new ReportApothecaryRoll(fInjuryResult.getDefenderId(), null, null, null));
            break;
          case NO_APOTHECARY:
            if (fShowReport) {
            	fInjuryResult.report(this);
            }
            break;
          default:
          	break;
        }
      }
      if (doNextStep) {
        Player player = game.getPlayerById(fInjuryResult.getDefenderId());
        switch (fInjuryResult.getApothecaryStatus()) {
          case DO_NOT_USE_IGOR:
            break;
          case USE_IGOR:
            Team team = game.getTeamHome().hasPlayer(player) ? game.getTeamHome() : game.getTeamAway();
            UtilInducementUse.useInducement(getGameState(), team, InducementType.IGOR, 1);
            getResult().addReport(new ReportInducement(team.getId(), InducementType.IGOR, 0));
            UtilInjury.handleRegeneration(this, player);
            break;
          default:
            fInjuryResult.applyTo(this);
            PlayerState playerState = game.getFieldModel().getPlayerState(player);
            if ((playerState != null) && playerState.isCasualty() && UtilCards.hasSkill(game, player, Skill.REGENERATION) && (fInjuryResult.getInjuryType() != InjuryType.EAT_PLAYER)) {
              if (!UtilInjury.handleRegeneration(this, player)) {
                InducementSet inducementSet = game.getTeamHome().hasPlayer(player) ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
                if (inducementSet.hasUsesLeft(InducementType.IGOR) && player.getPlayerType() != PlayerType.STAR) {
                  game.setDialogParameter(new DialogUseIgorParameter(player.getId()));
                  fInjuryResult.setApothecaryStatus(ApothecaryStatus.WAIT_FOR_IGOR_USE);
                  doNextStep = false;
                }
              }
            }
            break;
        }
      }
      if (doNextStep) {
        UtilInjury.handleRaiseDead(this, fInjuryResult);
  	  	getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }
	}
	
  private boolean rollApothecary() {
    Game game = getGameState().getGame();
    Player defender = game.getPlayerById(fInjuryResult.getDefenderId());
    if (game.getTeamHome().hasPlayer(defender)) {
      game.getTurnDataHome().useApothecary();
    } else {
      game.getTurnDataAway().useApothecary();
    }
    boolean apothecaryChoice = ((fInjuryResult.getPlayerState().getBase() != PlayerState.BADLY_HURT) && (fInjuryResult.getPlayerState().getBase() != PlayerState.KNOCKED_OUT));
    if (apothecaryChoice) {
      InjuryResult newInjuryResult = new InjuryResult();
      newInjuryResult.setDefenderId(fInjuryResult.getDefenderId());
      newInjuryResult.setCasualtyRoll(getGameState().getDiceRoller().rollCasualty());
      newInjuryResult.setInjury(DiceInterpreter.getInstance().interpretRollCasualty(newInjuryResult.getCasualtyRoll()));
      newInjuryResult.setSeriousInjury(DiceInterpreter.getInstance().interpretRollSeriousInjury(newInjuryResult.getCasualtyRoll()));
      apothecaryChoice = (newInjuryResult.getPlayerState().getBase() != PlayerState.BADLY_HURT);
      getResult().addReport(new ReportApothecaryRoll(defender.getId(), newInjuryResult.getCasualtyRoll(), newInjuryResult.getPlayerState(), newInjuryResult.getSeriousInjury()));
      if (apothecaryChoice) {
        UtilDialog.showDialog(getGameState(), new DialogApothecaryChoiceParameter(defender.getId(), fInjuryResult.getPlayerState(), fInjuryResult.getSeriousInjury(), newInjuryResult.getPlayerState(), newInjuryResult.getSeriousInjury()));
      }
    }
    if (!apothecaryChoice) {
      fInjuryResult.setSeriousInjury(null);
      if ((fInjuryResult.getPlayerState().getBase() == PlayerState.KNOCKED_OUT) && (fInjuryResult.getInjuryType() != InjuryType.CROWDPUSH)) {
        fInjuryResult.setInjury(new PlayerState(PlayerState.STUNNED));
      } else {
        fInjuryResult.setInjury(new PlayerState(PlayerState.RESERVE));
      }
      getResult().addReport(new ReportApothecaryChoice(defender.getId(), fInjuryResult.getPlayerState(), null));
    }
    return apothecaryChoice;
  }
  
  private void handleApothecaryChoice(PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    if (fInjuryResult != null) {
      if (pPlayerState.getBase() == PlayerState.BADLY_HURT) {
        fInjuryResult.setInjury(new PlayerState(PlayerState.RESERVE));
        fInjuryResult.setSeriousInjury(null);
      } else {
        fInjuryResult.setInjury(pPlayerState);
        fInjuryResult.setSeriousInjury(pSeriousInjury);
      }
      fInjuryResult.setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
    }
  }

  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addByte((byte) ((fApothecaryMode != null) ? fApothecaryMode.getId() : 0));
  	if (fInjuryResult != null) {
  		pByteList.addBoolean(true);
  		fInjuryResult.addTo(pByteList);
  	} else {
  		pByteList.addBoolean(false);
  	}
  	pByteList.addBoolean(fShowReport);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fApothecaryMode = new ApothecaryModeFactory().forId(pByteArray.getByte());
  	if (pByteArray.getBoolean()) {
  		fInjuryResult = new InjuryResult();
  		fInjuryResult.initFrom(pByteArray);
  	} else {
  		fInjuryResult = null;
  	}
  	fShowReport = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
}

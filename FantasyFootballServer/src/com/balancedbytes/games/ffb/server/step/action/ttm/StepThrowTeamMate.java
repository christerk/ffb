package com.balancedbytes.games.ffb.server.step.action.ttm;

import java.util.Set;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportThrowTeamMateRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;

/**
 * Step in ttm sequence to actual throw the team mate.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * 
 * Pushes new scatterPlayerSequence on the stack.
 * 
 * @author Kalimar
 */
public final class StepThrowTeamMate extends AbstractStepWithReRoll {
	
	protected String fGotoLabelOnFailure;
	protected String fThrownPlayerId;
	protected PlayerState fThrownPlayerState;
	protected boolean fThrownPlayerHasBall;
	
	public StepThrowTeamMate(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.THROW_TEAM_MATE;
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
  	if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
  }

  @Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
					return true;
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) pParameter.getValue();
					return true;
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		if ((pNetCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND)) {
			switch (pNetCommand.getId()) {
			  case CLIENT_USE_SKILL:
			    ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pNetCommand;
			    switch (useSkillCommand.getSkill()) {
			      case PASS:
			        if (useSkillCommand.isSkillUsed()) {
			          setReRollSource(ReRollSource.PASS);
			        } else {
			          setReRollSource(null);
			        }
			        break;
		        default:
		        	break;
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
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    actingPlayer.setHasPassed(true);
    game.setConcessionPossible(false);
    game.getTurnData().setPassUsed(true);
    Player thrower = game.getActingPlayer().getPlayer();
    boolean doRoll = true;
    if (ReRolledAction.THROW_TEAM_MATE == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), thrower)) {
      	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        doRoll = false;
      }
    }
    if (doRoll) {
      FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
      PassingDistance passingDistance = UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), true);
      Set<PassModifier> passModifiers = PassModifier.findPassModifiers(game, thrower, passingDistance, true);
      int minimumRoll = DiceInterpreter.getInstance().minimumRollThrowTeamMate(thrower, passingDistance, passModifiers);
      int roll = getGameState().getDiceRoller().rollSkill();
      boolean successful = !DiceInterpreter.getInstance().isPassFumble(roll, actingPlayer.getPlayer(), passingDistance, passModifiers);
      PassModifier[] passModifierArray = PassModifier.toArray(passModifiers);
      boolean reRolled = ((getReRolledAction() == ReRolledAction.THROW_TEAM_MATE) && (getReRollSource() != null));
      getResult().addReport(new ReportThrowTeamMateRoll(thrower.getId(), fThrownPlayerId, successful, roll, minimumRoll, passingDistance, passModifierArray, reRolled));
      if (successful) {
      	SequenceGenerator.getInstance().pushScatterPlayerSequence(getGameState(), fThrownPlayerId, fThrownPlayerState, fThrownPlayerHasBall, throwerCoordinate, true);
      	getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
        if (getReRolledAction() != ReRolledAction.THROW_TEAM_MATE) {
          setReRolledAction(ReRolledAction.THROW_TEAM_MATE);
          if (UtilCards.hasSkill(game, thrower, Skill.PASS)) {
            UtilDialog.showDialog(getGameState(), new DialogSkillUseParameter(thrower.getId(), Skill.PASS, minimumRoll));
          } else {
            if (!UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.THROW_TEAM_MATE, minimumRoll, false)) {
            	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
            }
          }
        } else {
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        }
      }
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
	@Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  	pByteList.addString(fThrownPlayerId);
  	pByteList.addSmallInt((fThrownPlayerState != null) ? fThrownPlayerState.getId() : 0);
  	pByteList.addBoolean(fThrownPlayerHasBall);
  }

	@Override
	public int initFrom(ByteArray pByteArray) {
		int byteArraySerializationVersion = super.initFrom(pByteArray);
		fGotoLabelOnFailure = pByteArray.getString();
		fThrownPlayerId = pByteArray.getString();
  	int thrownPlayerStateId = pByteArray.getSmallInt();
  	fThrownPlayerState = (thrownPlayerStateId > 0) ? new PlayerState(thrownPlayerStateId) : null;
  	fThrownPlayerHasBall = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
	}
	
}

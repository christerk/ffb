package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogPilingOnParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPilingOn;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in block sequence to drop falling players and handle the skill PILING_ON.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * Sets stepParameter USING_PILING_ON for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepDropFallingPlayers extends AbstractStep {
	
	private InjuryResult fInjuryResultDefender;
	private Boolean fUsingPilingOn;
	private PlayerState fOldDefenderState;
	
	public StepDropFallingPlayers(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.DROP_FALLING_PLAYERS;
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
			    if (Skill.PILING_ON == useSkillCommand.getSkill()) {
			    	fUsingPilingOn = useSkillCommand.isSkillUsed();
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

  private void executeStep() {
    boolean doNextStep = true;
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
    FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    if ((attackerState.getBase() == PlayerState.FALLING) && attackerState.isRooted()) {
      attackerState = attackerState.changeRooted(false);
    }
    if ((defenderState.getBase() == PlayerState.FALLING) && defenderState.isRooted()) {
      defenderState = defenderState.changeRooted(false);
    }
    if (((defenderState.getBase() == PlayerState.FALLING) && (defenderCoordinate != null)) || (fUsingPilingOn != null)) {
      if (fUsingPilingOn != null) {
        boolean reRollInjury = fInjuryResultDefender.isArmorBroken();
        getResult().addReport(new ReportPilingOn(actingPlayer.getPlayerId(), fUsingPilingOn, reRollInjury));
        if (fUsingPilingOn) {
          actingPlayer.markSkillUsed(Skill.PILING_ON);
          publishParameters(UtilInjury.dropPlayer(this, actingPlayer.getPlayer()));
          boolean rolledDouble;
          if (reRollInjury) {
          	fInjuryResultDefender = UtilInjury.handleInjury(this, InjuryType.PILING_ON_INJURY, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, fInjuryResultDefender, ApothecaryMode.DEFENDER);
          	rolledDouble = DiceInterpreter.getInstance().isDouble(fInjuryResultDefender.getInjuryRoll());
          } else {
          	fInjuryResultDefender = UtilInjury.handleInjury(this, InjuryType.PILING_ON_ARMOR, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
          	rolledDouble = DiceInterpreter.getInstance().isDouble(fInjuryResultDefender.getArmorRoll());
          }
          if (rolledDouble && game.getOptions().getOptionValue(GameOption.PILING_ON_TO_KO_ON_DOUBLE).isEnabled()) {
            publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilInjury.handleInjury(this, InjuryType.PILING_ON_KNOCKED_OUT, null, actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER)));
          }
        }
      } else {
        publishParameters(UtilInjury.dropPlayer(this, game.getDefender()));
        if ((fOldDefenderState != null) && fOldDefenderState.isProne()) {
        	fInjuryResultDefender = UtilInjury.handleInjury(this, InjuryType.BLOCK_PRONE, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
        } else {
        	fInjuryResultDefender = UtilInjury.handleInjury(this, InjuryType.BLOCK, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
        }
        if ((attackerState.getBase() != PlayerState.FALLING)
        	&& UtilCards.hasUnusedSkill(game, actingPlayer, Skill.PILING_ON)
          && attackerCoordinate.isAdjacent(defenderCoordinate)
          && !fInjuryResultDefender.isCasualty()
          && !attackerState.isRooted()
          && (!game.getOptions().getOptionValue(GameOption.PILING_ON_INJURY_ONLY).isEnabled() || fInjuryResultDefender.isArmorBroken())
          && (!game.getOptions().getOptionValue(GameOption.PILING_ON_ARMOR_ONLY).isEnabled() || !fInjuryResultDefender.isArmorBroken())
          && (!UtilCards.hasCard(game, game.getDefender(), Card.BELT_OF_INVULNERABILITY) || fInjuryResultDefender.isArmorBroken())
          && !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)
          && !UtilCards.hasCard(game, game.getDefender(), Card.GOOD_OLD_MAGIC_CODPIECE)
        ) {
        	fInjuryResultDefender.report(this);
          UtilDialog.showDialog(getGameState(), new DialogPilingOnParameter(actingPlayer.getPlayerId(), fInjuryResultDefender.isArmorBroken()));
          doNextStep = false;
        }
      }
    }
    if (doNextStep) {
    	if (fInjuryResultDefender != null) {
    		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, fInjuryResultDefender));
    		if (fOldDefenderState != null) {
    			game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
    		}
    	}
    	if (fUsingPilingOn != null) {
    		publishParameter(new StepParameter(StepParameterKey.USING_PILING_ON, fUsingPilingOn));
    	}
    	// end turn if dropping a player of your own team
    	if ((defenderState.getBase() == PlayerState.FALLING) && (game.getDefender().getTeam() == actingPlayer.getPlayer().getTeam()) && (fOldDefenderState != null) && !fOldDefenderState.isProne()) {
      	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
    	}
      if ((attackerState.getBase() == PlayerState.FALLING) && (attackerCoordinate != null)) {
      	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
        publishParameters(UtilInjury.dropPlayer(this, actingPlayer.getPlayer()));
        publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
        	UtilInjury.handleInjury(this, InjuryType.BLOCK, game.getDefender(), actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER)));
      }
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	if (fInjuryResultDefender != null) {
  		pByteList.addBoolean(true);
  		fInjuryResultDefender.addTo(pByteList);
  	} else {
  		pByteList.addBoolean(false);
  	}
  	pByteList.addBoolean(fUsingPilingOn);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	if (pByteArray.getBoolean()) {
  		fInjuryResultDefender = new InjuryResult();
  		fInjuryResultDefender.initFrom(pByteArray);
  	} else {
  		fInjuryResultDefender = null;
  	}
  	fUsingPilingOn = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}

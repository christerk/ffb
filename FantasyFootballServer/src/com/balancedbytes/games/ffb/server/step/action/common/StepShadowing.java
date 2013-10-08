package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilPlayerMove;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * Step in any sequence to handle skill SHADOWING.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step.
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * Expects stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepShadowing extends AbstractStepWithReRoll {
	
	private FieldCoordinate fDefenderPosition;
	private FieldCoordinate fCoordinateFrom;
	private boolean fUsingDivingTackle;
	private Boolean fUsingShadowing;
	
	public StepShadowing(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.SHADOWING;
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
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) pParameter.getValue();
					return true;
				case DEFENDER_POSITION:
					fDefenderPosition = (FieldCoordinate) pParameter.getValue();
					return true;
				case USING_DIVING_TACKLE:
					fUsingDivingTackle = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			switch (pNetCommand.getId()) {
	      case CLIENT_PLAYER_CHOICE:
	        ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pNetCommand;
	        if (PlayerChoiceMode.SHADOWING == playerChoiceCommand.getPlayerChoiceMode()) {
	        	fUsingShadowing = StringTool.isProvided(playerChoiceCommand.getPlayerId());
		        game.setDefenderId(playerChoiceCommand.getPlayerId());
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
  	Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean doNextStep = true;
    boolean doShadowing = (!fUsingDivingTackle && (game.getTurnMode() != TurnMode.KICKOFF_RETURN) && (game.getTurnMode() != TurnMode.PASS_BLOCK));
    if (doShadowing && (fCoordinateFrom != null) && (fUsingShadowing == null)) {
      Player[] shadowers = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, fCoordinateFrom, Skill.SHADOWING, true);
      shadowers = UtilPlayer.filterThrower(game, shadowers);
    	if (game.getTurnMode() == TurnMode.DUMP_OFF) {
    		shadowers = UtilPlayer.filterAttackerAndDefender(game, shadowers);
    	}
      if (ArrayTool.isProvided(shadowers)) {
        String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
        String[] descriptionArray = new String[shadowers.length];
        for (int i = 0; i < shadowers.length; i++) {
          int attributeDiff = shadowers[i].getMovement() - actingPlayer.getPlayer().getMovement();
          StringBuilder description = new StringBuilder();
          if (attributeDiff > 0) {
            description.append("(").append(attributeDiff).append(" MA advantage)");
          }
          if (attributeDiff == 0) {
            description.append("(equal MA)");
          }
          if (attributeDiff < 0) {
            description.append("(").append(Math.abs(attributeDiff)).append(" MA disadavantage)");
          }
          descriptionArray[i] = description.toString();
        } 
        UtilDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.SHADOWING, shadowers, descriptionArray, 1));
        doNextStep = false;
      } else {
      	fUsingShadowing = false;
      }
    }
    if (doShadowing && (fCoordinateFrom != null) && (fUsingShadowing != null)) {
      doNextStep = true;
      if (fUsingShadowing && (game.getDefender() != null)) {
        boolean rollShadowing = true;
        if (ReRolledAction.SHADOWING_ESCAPE == getReRolledAction()) {
          if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
            rollShadowing = false;
          }
        }
        if (rollShadowing) {
          int[] rollEscape = getGameState().getDiceRoller().rollShadowingEscape();
          boolean successful = DiceInterpreter.getInstance().isShadowingEscapeSuccessful(rollEscape, game.getDefender().getMovement(), actingPlayer.getPlayer().getMovement());
          int minimumRoll = DiceInterpreter.getInstance().minimumRollShadowingEscape(game.getDefender().getMovement(), actingPlayer.getPlayer().getMovement());
          boolean reRolled = ((getReRolledAction() == ReRolledAction.SHADOWING_ESCAPE) && (getReRollSource() != null));
          getResult().addReport(new ReportTentaclesShadowingRoll(Skill.SHADOWING, game.getDefenderId(), rollEscape, successful, minimumRoll, reRolled));
          if (successful) {
          	fUsingShadowing = false;
          } else {
            if (getReRolledAction() != ReRolledAction.SHADOWING_ESCAPE) {
              if (UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.SHADOWING_ESCAPE, minimumRoll, false)) {
                doNextStep = false;
              }
            }
          }
        }
      }
      if (doNextStep && fUsingShadowing) {
        game.getFieldModel().updatePlayerAndBallPosition(game.getDefender(), fCoordinateFrom);
        UtilPlayerMove.updateMoveSquares(getGameState(), actingPlayer.isLeaping());
      }
    }
    if (doNextStep) {
    	if (fDefenderPosition != null) {
    		Player defender = game.getFieldModel().getPlayer(fDefenderPosition);
    		game.setDefenderId((defender != null) ? defender.getId() : null);
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
  	pByteList.addFieldCoordinate(fDefenderPosition);
  	pByteList.addFieldCoordinate(fCoordinateFrom);
  	pByteList.addBoolean(fUsingShadowing);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fDefenderPosition = pByteArray.getFieldCoordinate();
  	fCoordinateFrom = pByteArray.getFieldCoordinate();
  	fUsingShadowing = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}

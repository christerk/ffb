package com.balancedbytes.games.ffb.server.step.action.end;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportBiteSpectator;
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
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * Step in any sequence to handle the feeding on another player.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * Needs to be initialized with stepParameter FEEDING_ALLOWED.
 * May be initialized with stepParameter END_TURN.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepInitFeeding extends AbstractStep {
	
	private String fGotoLabelOnEnd;
	private Boolean fFeedOnPlayerChoice;
	private Boolean fFeedingAllowed;
	private boolean fEndTurn;
	
	public StepInitFeeding(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_FEEDING;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_END:
  					fGotoLabelOnEnd = (String) parameter.getValue();
  					break;
  			  // mandatory
  				case FEEDING_ALLOWED:
						fFeedingAllowed = (Boolean) parameter.getValue();
  					break;
					// optional
  				case END_TURN:
						fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
  	}
  	if (fFeedingAllowed == null) {
			throw new StepException("StepParameter " + StepParameterKey.FEEDING_ALLOWED + " is not initialized.");
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
			Game game = getGameState().getGame();
			switch (pNetCommand.getId()) {
	      case CLIENT_PLAYER_CHOICE:
	        ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pNetCommand;
	        if (PlayerChoiceMode.FEED == playerChoiceCommand.getPlayerChoiceMode()) {
	        	fFeedOnPlayerChoice = StringTool.isProvided(playerChoiceCommand.getPlayerId());
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
    UtilDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (actingPlayer.isSufferingBloodLust() && !actingPlayer.hasFed() && !fFeedingAllowed) {
    	fFeedOnPlayerChoice = false;
    }
    if ((actingPlayer.getPlayer() == null) || !actingPlayer.isSufferingBloodLust() || actingPlayer.hasFed()) {
    	publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
    	publishParameter(new StepParameter(StepParameterKey.END_TURN, fEndTurn));
    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
    	return;
    }
    boolean doNextStep = false;
    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    if (playerState.hasTacklezones() && (fFeedOnPlayerChoice == null)) {
      game.setDefenderId(null);
      Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
      Player[] victims = UtilPlayer.findAdjacentPlayersToFeedOn(game, team, playerCoordinate);
      if (ArrayTool.isProvided(victims)) {
        UtilDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(team.getId(), PlayerChoiceMode.FEED, victims, null, 1));
      } else {
      	fFeedOnPlayerChoice = false;
      }
    }
    if (!playerState.hasTacklezones() || (fFeedOnPlayerChoice != null)) {
      if ((fFeedOnPlayerChoice != null) && fFeedOnPlayerChoice && (game.getDefender() != null)) {
        FieldCoordinate feedOnPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
        InjuryResult injuryResultFeeding = UtilInjury.handleInjury(this, InjuryType.BITTEN, actingPlayer.getPlayer(), game.getDefender(), feedOnPlayerCoordinate, null, ApothecaryMode.FEEDING);
        fEndTurn = UtilPlayer.hasBall(game, game.getDefender());  // turn end on biting the ball carrier
        publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultFeeding));
        publishParameters(UtilInjury.dropPlayer(this, game.getDefender()));
        getResult().setSound(Sound.SLURP);
        actingPlayer.setSufferingBloodLust(false);
        doNextStep = true;
      } else {
      	fEndTurn = true;
        if (!playerState.isCasualty() && (playerState.getBase() != PlayerState.KNOCKED_OUT) && (playerState.getBase() != PlayerState.RESERVE)) {
          if (playerCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
            game.getFieldModel().setBallMoving(true);
            publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
          }
          game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.RESERVE));
          UtilBox.putPlayerIntoBox(game, actingPlayer.getPlayer());
          getResult().addReport(new ReportBiteSpectator(actingPlayer.getPlayerId()));
        }
        doNextStep = true;
      }
    }
    if (doNextStep) {
    	publishParameter(new StepParameter(StepParameterKey.END_TURN, fEndTurn));
      actingPlayer.setHasFed(true);
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addBoolean(fFeedOnPlayerChoice);
  	pByteList.addBoolean(fFeedingAllowed);
  	pByteList.addBoolean(fEndTurn);
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fFeedOnPlayerChoice = pByteArray.getBoolean();
  	fFeedingAllowed = pByteArray.getBoolean();
  	fEndTurn = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}

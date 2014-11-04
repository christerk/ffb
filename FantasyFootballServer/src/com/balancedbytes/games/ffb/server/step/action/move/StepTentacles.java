package com.balancedbytes.games.ffb.server.step.action.move;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to handle skill TENTACLES.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepTentacles extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnSuccess;
	private FieldCoordinate fCoordinateFrom;
	private Boolean fUsingTentacles;
	
	public StepTentacles(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.TENTACLES;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				// mandatory
  				case GOTO_LABEL_ON_SUCCESS:
  					fGotoLabelOnSuccess = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (fGotoLabelOnSuccess == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
  	}
  }
  
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) pParameter.getValue();
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_PLAYER_CHOICE:
          ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
          if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.TENTACLES) {
            fUsingTentacles = StringTool.isProvided(playerChoiceCommand.getPlayerId());
            getGameState().getGame().setDefenderId(playerChoiceCommand.getPlayerId());
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
    if (fUsingTentacles == null) {
      if (actingPlayer.isDodging() || actingPlayer.isLeaping()) {
        Player[] playerArray = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, fCoordinateFrom, Skill.TENTACLES, false) ;
        if (ArrayTool.isProvided(playerArray)) {
          String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
          String[] descriptionArray = new String[playerArray.length];
          for (int i = 0; i < playerArray.length; i++) {
            int attributeDiff = UtilCards.getPlayerStrength(game, playerArray[i]) - actingPlayer.getStrength();
            StringBuilder description = new StringBuilder();
            if (attributeDiff > 0) {
              description.append("(").append(attributeDiff).append(" ST advantage)");
            }
            if (attributeDiff == 0) {
              description.append("(equal ST)");
            }
            if (attributeDiff < 0) {
              description.append("(").append(Math.abs(attributeDiff)).append(" ST disadavantage)");
            }
            descriptionArray[i] = description.toString();
          } 
          UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.TENTACLES, playerArray, descriptionArray, 1));
        } else {
        	fUsingTentacles = false;
        }
      } else {
      	fUsingTentacles = false;
      }
    }
    if (fUsingTentacles != null) {
      boolean doNextStep = true;
      if (fUsingTentacles && (game.getDefender() != null)) {
        boolean rollTentacles = true;
        if (ReRolledAction.TENTACLES_ESCAPE == getReRolledAction()) {
          if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
            rollTentacles = false;
          }
        }
        if (rollTentacles) {
          int[] rollEscape = getGameState().getDiceRoller().rollTentaclesEscape();
          boolean successful = DiceInterpreter.getInstance().isTentaclesEscapeSuccessful(rollEscape, UtilCards.getPlayerStrength(game, game.getDefender()), actingPlayer.getStrength());
          int minimumRoll = DiceInterpreter.getInstance().minimumRollTentaclesEscape(UtilCards.getPlayerStrength(game, game.getDefender()), actingPlayer.getStrength());
          boolean reRolled = ((getReRolledAction() == ReRolledAction.TENTACLES_ESCAPE) && (getReRollSource() != null));
          getResult().addReport(new ReportTentaclesShadowingRoll(Skill.TENTACLES, game.getDefenderId(), rollEscape, successful, minimumRoll, reRolled));
          if (successful) {
          	fUsingTentacles = false;
          } else {
            if (getReRolledAction() != ReRolledAction.TENTACLES_ESCAPE) {
              if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.TENTACLES_ESCAPE, minimumRoll, false)) {
                doNextStep = false;
              }
            }
          }
        }
      }
      if (doNextStep) {
        if (fUsingTentacles) {
          game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), fCoordinateFrom);
          publishParameter(new StepParameter(StepParameterKey.FEEDING_ALLOWED, false));
          publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnSuccess);
        } else {
        	getResult().setNextAction(StepAction.NEXT_STEP);
        }
      }
    }
  }
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnSuccess = pByteArray.getString();
  	fCoordinateFrom = pByteArray.getFieldCoordinate();
  	fUsingTentacles = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
    IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
    IServerJsonOption.USING_TENTACLES.addTo(jsonObject, fUsingTentacles);
    return jsonObject;
  }
  
  @Override
  public StepTentacles initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(jsonObject);
    fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    fUsingTentacles = IServerJsonOption.USING_TENTACLES.getFrom(jsonObject);
    return this;
  }
  	
}

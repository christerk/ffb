package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupDelete;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupLoad;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTeamSetupSave;
import com.balancedbytes.games.ffb.report.ReportNoPlayersToField;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilSetup;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to setup the playing team.
 * 
 * @author Kalimar
 */
public final class StepSetup extends AbstractStep {
	
  private String fGotoLabelOnEnd;
  private boolean fEndSetup;

	public StepSetup(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.SETUP;
	}
	
	@Override
	public void start() {
		executeStep();
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
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}
	
  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
	      case CLIENT_TEAM_SETUP_LOAD:
	        ClientCommandTeamSetupLoad loadSetupCommand = (ClientCommandTeamSetupLoad) pReceivedCommand.getCommand();
	        UtilSetup.loadTeamSetup(getGameState(), loadSetupCommand.getSetupName());
	        commandStatus = StepCommandStatus.SKIP_STEP;
	        break;
	      case CLIENT_TEAM_SETUP_SAVE:
	        ClientCommandTeamSetupSave saveSetupCommand = (ClientCommandTeamSetupSave) pReceivedCommand.getCommand();
	        UtilSetup.saveTeamSetup(getGameState(), saveSetupCommand.getSetupName(), saveSetupCommand.getPlayerNumbers(), saveSetupCommand.getPlayerCoordinates());
	        commandStatus = StepCommandStatus.SKIP_STEP;
	        break;
	      case CLIENT_TEAM_SETUP_DELETE:
	        ClientCommandTeamSetupDelete deleteSetupCommand = (ClientCommandTeamSetupDelete) pReceivedCommand.getCommand();
	        UtilSetup.deleteTeamSetup(getGameState(), deleteSetupCommand.getSetupName());
	        commandStatus = StepCommandStatus.SKIP_STEP;
	        break;
	      case CLIENT_SETUP_PLAYER:
	        ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
	        UtilSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(), setupPlayerCommand.getCoordinate());
	        commandStatus = StepCommandStatus.SKIP_STEP;
	        break;
        case CLIENT_END_TURN:
        	if (UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
        		fEndSetup = true;
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
  	if (checkNoPlayersInBoxOrField()) {
  		game.setTurnMode(TurnMode.NO_PLAYERS_TO_FIELD);
  		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
  		return;
  	}
    if (fEndSetup) {
      getResult().setSound(Sound.DING);
      if (UtilKickoffSequence.checkSetup(getGameState(), game.isHomePlaying())) {
	      game.setHomePlaying(!game.isHomePlaying());
	      game.getTurnData().setTurnStarted(false);
	      game.getTurnData().setFirstTurnAfterKickoff(false);
	      UtilBox.refreshBoxes(game);
	      if (game.isSetupOffense()) {
      		game.setTurnMode(TurnMode.KICKOFF);
	      } else {
	        game.setSetupOffense(true);
	        SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.BEFORE_SETUP, !game.isHomePlaying());
	      }
	      getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
      	fEndSetup = false;
      }
    }
  }
  
	//	In the rare event that one team has no players to set up after
	//	KO’d rolls, both teams' turn markers are moved forward along
	//	the turn track two spaces and if one team could field at least one
	//	player then that team is awarded a touchdown (however no
	//	player receives Star Player points (see page 25) for this.) If this
	//	takes the number of turns to 8 or more for both teams, then the
	//	half ends. If there are still turns left in the half, then continue
	//	playing as if a drive has just ended (i.e. clear the pitch and roll for
	//	KO'd players).
  private boolean checkNoPlayersInBoxOrField() {
  	Game game = getGameState().getGame();
  	Player[] playersInBoxHome = UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome());
  	Player[] playersInBoxAway = UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway());
  	if (!ArrayTool.isProvided(playersInBoxHome) || !ArrayTool.isProvided(playersInBoxAway)) {
    	if (ArrayTool.isProvided(playersInBoxHome) && !ArrayTool.isProvided(playersInBoxAway)) {
    		game.setHomePlaying(true);
    		game.getGameResult().getTeamResultHome().setScore(game.getGameResult().getTeamResultHome().getScore() + 1);
    		getResult().addReport(new ReportNoPlayersToField(game.getTeamAway().getId()));
    	} else if (!ArrayTool.isProvided(playersInBoxHome) && ArrayTool.isProvided(playersInBoxAway)) {
    		game.setHomePlaying(false);
    		game.getGameResult().getTeamResultAway().setScore(game.getGameResult().getTeamResultAway().getScore() + 1);
    		getResult().addReport(new ReportNoPlayersToField(game.getTeamHome().getId()));
    	} else {
    		getResult().addReport(new ReportNoPlayersToField(null));
    	}
    	return true;
  	}
  	return false;
  }
  
  // ByteArray serialization
    
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fEndSetup = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.END_KICKOFF.addTo(jsonObject, fEndSetup);
    return jsonObject;
  }
  
  @Override
  public StepSetup initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fEndSetup = IServerJsonOption.END_KICKOFF.getFrom(jsonObject);
    return this;
  }
  
}

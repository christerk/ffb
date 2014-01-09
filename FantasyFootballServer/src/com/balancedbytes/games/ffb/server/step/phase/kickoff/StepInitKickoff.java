package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandKickoff;
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
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilSetup;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the kickoff sequence.
 * 
 * Sets stepParameter KICKOFF_START_COORDINATE for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepInitKickoff extends AbstractStep {
	
  private String fGotoLabelOnEnd;
  private FieldCoordinate fKickoffStartCoordinate;
  private boolean fEndKickoff;

	public StepInitKickoff(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_KICKOFF;
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
			Game game = getGameState().getGame();
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
        case CLIENT_KICKOFF:
          ClientCommandKickoff kickoffCommand = (ClientCommandKickoff) pReceivedCommand.getCommand();
          if (game.isHomePlaying()) {
          	fKickoffStartCoordinate = kickoffCommand.getBallCoordinate();
          } else {
          	fKickoffStartCoordinate = kickoffCommand.getBallCoordinate().transform();
          }
          commandStatus = StepCommandStatus.EXECUTE_STEP;
          break;
        case CLIENT_END_TURN:
        	if (UtilSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
        		fEndKickoff = true;
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
    switch (game.getTurnMode()) {
    	case START_GAME:
        UtilDialog.hideDialog(getGameState());
        UtilGame.startHalf(this, 1);
        game.setTurnMode(TurnMode.SETUP);
        game.startTurn();
        UtilGame.updateLeaderReRolls(this);
    		break;
	    case SETUP:
      	if (checkNoPlayersInBoxOrField()) {
      		game.setTurnMode(TurnMode.NO_PLAYERS_TO_FIELD);
      		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
      		return;
      	}
	      if (fEndKickoff) {
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
			      }
	        } else {
	        	fEndKickoff = false;
	        }
	      }
	      break;
	    case KICKOFF:
	      if (fKickoffStartCoordinate != null) {
	        UtilDialog.hideDialog(getGameState());
	        publishParameter(new StepParameter(StepParameterKey.KICKOFF_START_COORDINATE, fKickoffStartCoordinate));
	      	SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.BEFORE_KICKOFF_SCATTER, game.isHomePlaying());
	      	SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.BEFORE_KICKOFF_SCATTER, !game.isHomePlaying());
	        getResult().setNextAction(StepAction.NEXT_STEP);
	      }
	      break;
			default:
				break;
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
    
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addFieldCoordinate(fKickoffStartCoordinate);
  	pByteList.addBoolean(fEndKickoff);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fKickoffStartCoordinate = pByteArray.getFieldCoordinate();
  	fEndKickoff = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.KICKOFF_START_COORDINATE.addTo(jsonObject, fKickoffStartCoordinate);
    IServerJsonOption.END_KICKOFF.addTo(jsonObject, fEndKickoff);
    return jsonObject;
  }
  
  @Override
  public StepInitKickoff initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fKickoffStartCoordinate = IServerJsonOption.KICKOFF_START_COORDINATE.getFrom(jsonObject);
    fEndKickoff = IServerJsonOption.END_KICKOFF.getFrom(jsonObject);
    return this;
  }
  
}

package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportPlayCard;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the inducement sequence.
 * 
 * Needs to be initialized with stepParameter CARD.
 * Needs to be initialized with stepParameter HOME_TEAM.
 *
 * @author Kalimar
 */
public final class StepInitCard extends AbstractStep {
	
  private Card fCard;
  private boolean fHomeTeam;

  private transient String fPlayerId;
	private transient boolean fEndCardPlaying;
	
	public StepInitCard(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_CARD;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				// mandatory
  				case CARD:
  					fCard = (Card) parameter.getValue();
  					break;
  				// mandatory
  				case HOME_TEAM:
  					fHomeTeam = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (fCard == null) {
			throw new StepException("StepParameter " + StepParameterKey.CARD + " is not initialized.");
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
	      case CLIENT_PLAYER_CHOICE:
	      	ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pNetCommand;
	      	fPlayerId = playerChoiceCommand.getPlayerId();
	      	if (!StringTool.isProvided(fPlayerId)) {
	      		fEndCardPlaying = true;
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
  	UtilDialog.hideDialog(getGameState());
  	Game game = getGameState().getGame();
		InducementSet inducementSet = fHomeTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
  	Team playingTeam = fHomeTeam ? game.getTeamHome() : game.getTeamAway();
		if (fEndCardPlaying) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if (StringTool.isProvided(fPlayerId)) {
			inducementSet.activateCard(fCard);
			game.getFieldModel().addCard(game.getPlayerById(fPlayerId), fCard);
			getResult().addReport(new ReportPlayCard(playingTeam.getId(), fCard, fPlayerId));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if (fCard.getTarget().isPlayedOnPlayer()) {
			// step initInducement has already checked if this card can be played
			Player[] allowedPlayers = UtilCards.findAllowedPlayersForCard(game, fCard);
			game.setDialogParameter(new DialogPlayerChoiceParameter(playingTeam.getId(), PlayerChoiceMode.CARD, allowedPlayers, null, 1));
		} else {
			inducementSet.activateCard(fCard);
			getResult().addReport(new ReportPlayCard(playingTeam.getId(), fCard));
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
  	pByteList.addSmallInt((fCard != null) ? fCard.getId() : 0);
  	pByteList.addBoolean(fHomeTeam);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fCard = new CardFactory().forId(pByteArray.getSmallInt());
  	fHomeTeam = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
    IServerJsonOption.CARD.addTo(jsonObject, fCard);
    return jsonObject;
  }
  
  @Override
  public StepInitCard initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fHomeTeam = IServerJsonOption.HOME_TEAM.getFrom(jsonObject);
    fCard = (Card) IServerJsonOption.CARD.getFrom(jsonObject);
    return this;
  }

}

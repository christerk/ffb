package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerCards;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerSetup;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to play a card.
 * 
 * Needs to be initialized with stepParameter CARD.
 * Needs to be initialized with stepParameter HOME_TEAM.
 * 
 * @author Kalimar
 */
public final class StepPlayCard extends AbstractStep {

  private Card fCard;
  private boolean fHomeTeam;
  private boolean fIllegalSubstitution;
  private String fSetupPlayerId;
  private FieldCoordinate fSetupPlayerCoordinate;

  private transient String fPlayerId;
  private transient String fOpponentId;
  private transient boolean fEndCardPlaying;
  

  public StepPlayCard(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.PLAY_CARD;
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      Game game = getGameState().getGame();
      switch (pReceivedCommand.getId()) {
        case CLIENT_PLAYER_CHOICE:
          ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
          if (PlayerChoiceMode.BLOCK == playerChoiceCommand.getPlayerChoiceMode()) {
            fOpponentId = playerChoiceCommand.getPlayerId();
          } else {
            fPlayerId = playerChoiceCommand.getPlayerId();
            if (!StringTool.isProvided(fPlayerId)) {
              fEndCardPlaying = true;
            }
          }
          commandStatus = StepCommandStatus.EXECUTE_STEP;
          break;
        case CLIENT_SETUP_PLAYER:
          if (fIllegalSubstitution) {
            ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
            fSetupPlayerId = setupPlayerCommand.getPlayerId();
            fSetupPlayerCoordinate = setupPlayerCommand.getCoordinate();
            commandStatus = StepCommandStatus.SKIP_STEP;
          }
          break;
        case CLIENT_END_TURN:
          if (fIllegalSubstitution && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
            fEndCardPlaying = true;
            Player setupPlayer = game.getPlayerById(fSetupPlayerId);
            if ((setupPlayer != null) && (fSetupPlayerCoordinate != null)) {
              game.getFieldModel().addCardEffect(setupPlayer, CardEffect.ILLEGALLY_SUBSTITUTED);
              UtilServerSetup.setupPlayer(getGameState(), fSetupPlayerId, fSetupPlayerCoordinate);
            }
            fSetupPlayerId = null;
            fSetupPlayerCoordinate = null;
            fIllegalSubstitution = false;
            game.setTurnMode(TurnMode.REGULAR);
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
    UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    Team ownTeam = fHomeTeam ? game.getTeamHome() : game.getTeamAway();
    if (fEndCardPlaying) {
      getResult().setNextAction(StepAction.NEXT_STEP);
    } else if (StringTool.isProvided(fPlayerId)) {
      playCardOnPlayer();
    } else if (fCard.getTarget().isPlayedOnPlayer()) {
      // step initInducement has already checked if this card can be played
      Player[] allowedPlayers = UtilServerCards.findAllowedPlayersForCard(game, fCard);
      game.setDialogParameter(new DialogPlayerChoiceParameter(ownTeam.getId(), PlayerChoiceMode.CARD, allowedPlayers, null, 1));
    } else {
      playCardOnTurn();
    }
  }

  private void playCardOnTurn() {
    boolean doNextStep = true;
    switch (fCard) {
      case ILLEGAL_SUBSTITUTION:
        doNextStep = playIllegalSubstitution();
        break;
      default:
        UtilServerCards.activateCard(this, fCard, fHomeTeam, null);
        break;
    }
    if (doNextStep) {
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  private void playCardOnPlayer() {
    Game game = getGameState().getGame();
    Player player = game.getPlayerById(fPlayerId);
    if (player == null) {
      return;
    }
    boolean doNextStep = true;
    switch (fCard) {
      case CHOP_BLOCK:
        doNextStep = playCardChopBlock();
        break;
      default:
        UtilServerCards.activateCard(this, fCard, fHomeTeam, fPlayerId);
        break;
    }
    if (doNextStep) {
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private boolean playIllegalSubstitution() {
    Game game = getGameState().getGame();
    UtilServerCards.activateCard(this, fCard, fHomeTeam, null);
    game.setTurnMode(TurnMode.ILLEGAL_SUBSTITUTION);
    fIllegalSubstitution = true;
    return false;
  }
    
  private boolean playCardChopBlock() {
    boolean doNextStep = false;
    Game game = getGameState().getGame();
    Player player = game.getPlayerById(fPlayerId);
    FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
    Team ownTeam = fHomeTeam ? game.getTeamHome() : game.getTeamAway();
    Team otherTeam = fHomeTeam ? game.getTeamAway() : game.getTeamHome();
    if (!StringTool.isProvided(fOpponentId)) {
      Player[] blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, otherTeam, playerCoordinate);
      if (blockablePlayers.length == 1) {
        fOpponentId = blockablePlayers[0].getId();
      } else {
        game.setDialogParameter(new DialogPlayerChoiceParameter(ownTeam.getId(), PlayerChoiceMode.BLOCK, blockablePlayers, null, 1));
      }
      UtilServerCards.activateCard(this, fCard, fHomeTeam, fPlayerId);
    }
    if (StringTool.isProvided(fOpponentId)) {
      doNextStep = true;
      Player opponent = game.getPlayerById(fOpponentId);
      publishParameters(UtilServerInjury.stunPlayer(this, opponent, ApothecaryMode.DEFENDER));
      publishParameters(UtilServerInjury.dropPlayer(this, player, ApothecaryMode.ATTACKER));
    }
    return doNextStep;
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
    IServerJsonOption.CARD.addTo(jsonObject, fCard);
    IServerJsonOption.ILLEGAL_SUBSTITUTION.addTo(jsonObject, fIllegalSubstitution);
    IServerJsonOption.SETUP_PLAYER_ID.addTo(jsonObject, fSetupPlayerId);
    IServerJsonOption.SETUP_PLAYER_COORDINATE.addTo(jsonObject, fSetupPlayerCoordinate);
    return jsonObject;
  }

  @Override
  public StepPlayCard initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fHomeTeam = IServerJsonOption.HOME_TEAM.getFrom(jsonObject);
    fCard = (Card) IServerJsonOption.CARD.getFrom(jsonObject);
    fIllegalSubstitution = IServerJsonOption.ILLEGAL_SUBSTITUTION.getFrom(jsonObject);
    fSetupPlayerId = IServerJsonOption.SETUP_PLAYER_ID.getFrom(jsonObject);
    fSetupPlayerCoordinate = IServerJsonOption.SETUP_PLAYER_COORDINATE.getFrom(jsonObject);
    return this;
  }

}

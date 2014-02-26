package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportPlayCard;
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
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the inducement sequence.
 * 
 * Needs to be initialized with stepParameter CARD. Needs to be initialized with
 * stepParameter HOME_TEAM.
 * 
 * @author Kalimar
 */
public final class StepInitCard extends AbstractStep {

  private Card fCard;
  private boolean fHomeTeam;

  private transient String fPlayerId;
  private transient String fOpponentId;
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
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
    Team ownTeam = fHomeTeam ? game.getTeamHome() : game.getTeamAway();
    if (fEndCardPlaying) {
      getResult().setNextAction(StepAction.NEXT_STEP);
    } else if (StringTool.isProvided(fPlayerId)) {
      playCardOnPlayer();
    } else if (fCard.getTarget().isPlayedOnPlayer()) {
      // step initInducement has already checked if this card can be played
      Player[] allowedPlayers = UtilCards.findAllowedPlayersForCard(game, fCard);
      game.setDialogParameter(new DialogPlayerChoiceParameter(ownTeam.getId(), PlayerChoiceMode.CARD, allowedPlayers, null, 1));
    } else {
      activateCard(null);
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  private void playCardOnPlayer() {
    Game game = getGameState().getGame();
    Player player = game.getPlayerById(fPlayerId);
    if (player == null) {
      return;
    }
    boolean doNextStep;
    switch (fCard) {
      case CHOP_BLOCK:
        doNextStep = playChopBlock();
        break;
      case CUSTARD_PIE:
        doNextStep = playCustardPie();
        break;
      default:
        activateCard(fPlayerId);
        doNextStep = true;
        break;
    }
    if (doNextStep) {
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  private void activateCard(String pPlayerId) {
    Game game = getGameState().getGame();
    Team ownTeam = fHomeTeam ? game.getTeamHome() : game.getTeamAway();
    InducementSet inducementSet = fHomeTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
    inducementSet.activateCard(fCard);
    if (StringTool.isProvided(pPlayerId)) {
      game.getFieldModel().addCard(game.getPlayerById(pPlayerId), fCard);
    }
    getResult().setAnimation(new Animation(fCard));
    UtilGame.syncGameModel(this);
    if (StringTool.isProvided(pPlayerId)) {
      getResult().addReport(new ReportPlayCard(ownTeam.getId(), fCard, pPlayerId));
    } else {
      getResult().addReport(new ReportPlayCard(ownTeam.getId(), fCard));
    }
  }

  private boolean playCustardPie() {
    Game game = getGameState().getGame();
    Player player = game.getPlayerById(fPlayerId);
    activateCard(fPlayerId);
    PlayerState playerState = game.getFieldModel().getPlayerState(player);
    game.getFieldModel().setPlayerState(player, playerState.changeHypnotized(true));
    return true;
  }
  
  private boolean playChopBlock() {
    boolean doNextStep = false;
    Game game = getGameState().getGame();
    Player player = game.getPlayerById(fPlayerId);
    PlayerState playerState = game.getFieldModel().getPlayerState(player);
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
      activateCard(fPlayerId);
    }
    if (StringTool.isProvided(fOpponentId)) {
      doNextStep = true;
      game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE).changeActive(false));
      Player opponent = game.getPlayerById(fOpponentId);
      PlayerState opponentState = game.getFieldModel().getPlayerState(opponent);
      game.getFieldModel().setPlayerState(opponent, opponentState.changeBase(PlayerState.STUNNED).changeActive(false));
    }
    return doNextStep;
  }

  // ByteArray serialization

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

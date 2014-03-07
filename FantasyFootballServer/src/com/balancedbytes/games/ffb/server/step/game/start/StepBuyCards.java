package com.balancedbytes.games.ffb.server.step.game.start;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.dialog.DialogBuyCardsParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyCard;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportCardsBought;
import com.balancedbytes.games.ffb.server.CardDeck;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in start game sequence to buy cards.
 * 
 * Sets stepParameter INDUCEMENT_GOLD_AWAY for all steps on the stack. Sets
 * stepParameter INDUCEMENT_GOLD_HOME for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepBuyCards extends AbstractStep {

  private int fInducementGoldHome;
  private int fInducementGoldAway;

  private boolean fCardsSelectedHome;
  private boolean fCardsSelectedAway;

  private boolean fReportedHome;
  private boolean fReportedAway;

  private transient Map<CardType, CardDeck> fDeckByType;
  private transient CardType fBuyCardHome;
  private transient CardType fBuyCardAway;

  public StepBuyCards(GameState pGameState) {
    super(pGameState);
    fDeckByType = new HashMap<CardType, CardDeck>();
    fBuyCardHome = null;
    fBuyCardAway = null;
  }

  public StepId getId() {
    return StepId.BUY_CARDS;
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
      case CLIENT_BUY_CARD:
        ClientCommandBuyCard buyCardCommand = (ClientCommandBuyCard) pReceivedCommand.getCommand();
        if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
          fBuyCardHome = buyCardCommand.getCardType();
          if (fBuyCardHome == null) {
            fCardsSelectedHome = true;
          }
        } else {
          fBuyCardAway = buyCardCommand.getCardType();
          if (fBuyCardAway == null) {
            fCardsSelectedAway = true;
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
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    if (UtilGameOption.getIntOption(game, GameOptionId.MAX_NR_OF_CARDS) == 0) {
      calculateInducementGold();
      publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_GOLD_HOME, fInducementGoldHome));
      publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_GOLD_AWAY, fInducementGoldAway));
      getResult().setNextAction(StepAction.NEXT_STEP);
      return;
    }
    buildDecks();
    if (fBuyCardHome != null) {
      fInducementGoldHome -= fBuyCardHome.getPrice();
      CardDeck deck = fDeckByType.get(fBuyCardHome);
      Card card = getGameState().getDiceRoller().drawCard(deck);
      game.getTurnDataHome().getInducementSet().addAvailableCard(card);
      fBuyCardHome = null;
    } else if (fBuyCardAway != null) {
      fInducementGoldAway -= fBuyCardAway.getPrice();
      CardDeck deck = fDeckByType.get(fBuyCardAway);
      Card card = getGameState().getDiceRoller().drawCard(deck);
      game.getTurnDataAway().getInducementSet().addAvailableCard(card);
      fBuyCardAway = null;
    } else {
      if (!fCardsSelectedHome && !fCardsSelectedAway) {
        calculateInducementGold();
        fInducementGoldHome += UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH);
        fInducementGoldAway += UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH);
      }
      if (fInducementGoldHome < CardType.getMinimumPrice()) {
        fCardsSelectedHome = true;
      }
      if (fInducementGoldAway < CardType.getMinimumPrice()) {
        fCardsSelectedAway = true;
      }
      if (fCardsSelectedHome && !fReportedHome) {
        fReportedHome = true;
        int totalCostHome = 0;
        Card[] cardsHome = game.getTurnDataHome().getInducementSet().getAllCards();
        for (Card card : cardsHome) {
          totalCostHome += card.getType().getPrice();
        }
        gameResult.getTeamResultHome().setPettyCashUsed(
            Math.max(0, totalCostHome - UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH)));
        getResult().addReport(new ReportCardsBought(game.getTeamHome().getId(), cardsHome.length, totalCostHome));
      }
      if (fCardsSelectedAway && !fReportedAway) {
        fReportedAway = true;
        int totalCostAway = 0;
        Card[] cardsAway = game.getTurnDataAway().getInducementSet().getAllCards();
        for (Card card : cardsAway) {
          totalCostAway += card.getType().getPrice();
        }
        gameResult.getTeamResultAway().setPettyCashUsed(
            Math.max(0, totalCostAway - UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH)));
        getResult().addReport(new ReportCardsBought(game.getTeamAway().getId(), cardsAway.length, totalCostAway));
      }
      if (!fCardsSelectedHome && !fCardsSelectedAway) {
        int homeTV = gameResult.getTeamResultHome().getTeamValue();
        int awayTV = gameResult.getTeamResultAway().getTeamValue();
        if (homeTV > awayTV) {
          UtilServerDialog.showDialog(getGameState(), createDialogParameter(game.getTeamHome().getId(), fInducementGoldHome));
        } else {
          UtilServerDialog.showDialog(getGameState(), createDialogParameter(game.getTeamAway().getId(), fInducementGoldAway));
        }
      } else if (!fCardsSelectedHome) {
        UtilServerDialog.showDialog(getGameState(), createDialogParameter(game.getTeamHome().getId(), fInducementGoldHome));
      } else if (!fCardsSelectedAway) {
        UtilServerDialog.showDialog(getGameState(), createDialogParameter(game.getTeamAway().getId(), fInducementGoldAway));
      } else {
        fInducementGoldHome = Math.max(0,
            fInducementGoldHome - UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH));
        publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_GOLD_HOME, fInducementGoldHome));
        fInducementGoldAway = Math.max(0,
            fInducementGoldAway - UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH));
        publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_GOLD_AWAY, fInducementGoldAway));
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }
  }

  private void calculateInducementGold() {
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    int homeTV = gameResult.getTeamResultHome().getTeamValue();
    int awayTV = gameResult.getTeamResultAway().getTeamValue();
    fInducementGoldHome = gameResult.getTeamResultHome().getPettyCashTransferred();
    fInducementGoldAway = gameResult.getTeamResultAway().getPettyCashTransferred();
    if ((awayTV > homeTV) && ((awayTV - homeTV) > fInducementGoldHome)) {
      fInducementGoldHome = (awayTV - homeTV);
    }
    if ((homeTV > awayTV) && ((homeTV - awayTV) > fInducementGoldAway)) {
      fInducementGoldAway = (homeTV - awayTV);
    }
  }

  private void buildDecks() {
    Game game = getGameState().getGame();
    fDeckByType.clear();
    for (CardType type : CardType.values()) {
      CardDeck deck = new CardDeck(type);
      deck.build(game);
      fDeckByType.put(type, deck);
    }
  }

  private DialogBuyCardsParameter createDialogParameter(String pTeamId, int pAvailableGold) {
    int availableCards = UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.MAX_NR_OF_CARDS);
    DialogBuyCardsParameter dialogParameter = new DialogBuyCardsParameter(pTeamId, availableCards, pAvailableGold);
    for (CardType type : fDeckByType.keySet()) {
      CardDeck deck = fDeckByType.get(type);
      dialogParameter.put(type, deck.size());
    }
    return dialogParameter;
  }
  
  // ByteArray serialization

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fInducementGoldHome = pByteArray.getInt();
    fInducementGoldAway = pByteArray.getInt();
    fCardsSelectedHome = pByteArray.getBoolean();
    fCardsSelectedAway = pByteArray.getBoolean();
    fReportedHome = pByteArray.getBoolean();
    fReportedAway = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.INDUCEMENT_GOLD_AWAY.addTo(jsonObject, fInducementGoldAway);
    IServerJsonOption.INDUCEMENT_GOLD_HOME.addTo(jsonObject, fInducementGoldHome);
    IServerJsonOption.CARDS_SELECTED_AWAY.addTo(jsonObject, fCardsSelectedAway);
    IServerJsonOption.CARDS_SELECTED_HOME.addTo(jsonObject, fCardsSelectedHome);
    IServerJsonOption.REPORTED_AWAY.addTo(jsonObject, fReportedAway);
    IServerJsonOption.REPORTED_HOME.addTo(jsonObject, fReportedHome);
    return jsonObject;
  }
  
  @Override
  public StepBuyCards initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fInducementGoldAway = IServerJsonOption.INDUCEMENT_GOLD_AWAY.getFrom(jsonObject);
    fInducementGoldHome = IServerJsonOption.INDUCEMENT_GOLD_HOME.getFrom(jsonObject);
    fCardsSelectedAway = IServerJsonOption.CARDS_SELECTED_AWAY.getFrom(jsonObject);
    fCardsSelectedHome = IServerJsonOption.CARDS_SELECTED_HOME.getFrom(jsonObject);
    fReportedAway = IServerJsonOption.REPORTED_AWAY.getFrom(jsonObject);
    fReportedHome = IServerJsonOption.REPORTED_HOME.getFrom(jsonObject);
    return this;
  }

}

package com.balancedbytes.games.ffb.server.util;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.CardTarget;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.report.ReportCardDeactivated;
import com.balancedbytes.games.ffb.report.ReportCardEffectRoll;
import com.balancedbytes.games.ffb.report.ReportPlayCard;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.ISkillProperty;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class UtilServerCards {

  public static Player[] findAllowedPlayersForCard(Game pGame, Card pCard) {
    if ((pGame == null) || (pCard == null) || !pCard.getTarget().isPlayedOnPlayer()) {
      return new Player[0];
    }
    List<Player> allowedPlayers = new ArrayList<Player>();
    Team ownTeam = pGame.getTurnDataHome().getInducementSet().isAvailable(pCard) ? pGame.getTeamHome() : pGame.getTeamAway();
    Team otherTeam = (pGame.getTeamHome() == ownTeam) ? pGame.getTeamAway() : pGame.getTeamHome();
    for (Player player : pGame.getPlayers()) {
      PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
      FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(player);
      boolean playerAllowed = ((playerState != null) && !playerState.isCasualty() && (playerState.getBase() != PlayerState.BANNED)
          && (playerState.getBase() != PlayerState.MISSING));
      if (pCard.getTarget() == CardTarget.OWN_PLAYER) {
        playerAllowed &= ownTeam.hasPlayer(player);
      }
      if (pCard.getTarget() == CardTarget.OPPOSING_PLAYER) {
        playerAllowed &= otherTeam.hasPlayer(player);
      }
      switch (pCard) {
        case FORCE_SHIELD:
          playerAllowed &= UtilPlayer.hasBall(pGame, player);
          break;
        case RABBITS_FOOT:
          playerAllowed &= !UtilCards.hasSkill(pGame, player, ServerSkill.LONER);
          break;
        case CHOP_BLOCK:
          playerAllowed &= playerState.isActive() && !playerState.isProne()
              && (UtilPlayer.findAdjacentBlockablePlayers(pGame, otherTeam, playerCoordinate).length > 0);
          break;
        case CUSTARD_PIE:
          playerAllowed &= (UtilPlayer.findAdjacentStandingOrPronePlayers(pGame, ownTeam, playerCoordinate).length > 0);
          break;
        default:
          break;
      }
      if (playerAllowed) {
        allowedPlayers.add(player);
      }
    }
    return allowedPlayers.toArray(new Player[allowedPlayers.size()]);
  }

  public static void activateCard(IStep pStep, Card pCard, boolean pHomeTeam, String pPlayerId) {

    if ((pStep == null) || (pCard == null)) {
      return;
    }

    // play animation first before activating card and its effects
    pStep.getResult().setAnimation(new Animation(pCard));
    UtilServerGame.syncGameModel(pStep);

    Game game = pStep.getGameState().getGame();
    Team ownTeam = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
    if (StringTool.isProvided(pPlayerId)) {
      pStep.getResult().addReport(new ReportPlayCard(ownTeam.getId(), pCard, pPlayerId));
    } else {
      pStep.getResult().addReport(new ReportPlayCard(ownTeam.getId(), pCard));
    }

    InducementSet inducementSet = pHomeTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
    inducementSet.activateCard(pCard);
    Player player = game.getPlayerById(pPlayerId);
    if (player != null) {
      game.getFieldModel().addCard(player, pCard);
      switch (pCard) {
        case DISTRACT:
          activateCardDistract(pStep, player);
          break;
        case CUSTARD_PIE:
          activateCardCustardPie(pStep, player);
          break;
        case PIT_TRAP:
          activateCardPitTrap(pStep, player);
          break;
        case WITCH_BREW:
          activateCardWitchBrew(pStep, player);
          break;
        default:
          break;
      }
    }

  }

  public static void deactivateCard(IStep pStep, Card pCard) {

    if ((pStep == null) || (pCard == null)) {
      return;
    }

    Game game = pStep.getGameState().getGame();
    if (game.getTurnDataHome().getInducementSet().isActive(pCard)) {
      game.getTurnDataHome().getInducementSet().deactivateCard(pCard);
    } else if (game.getTurnDataAway().getInducementSet().isActive(pCard)) {
      game.getTurnDataAway().getInducementSet().deactivateCard(pCard);
    } else {
      return;
    }

    pStep.getResult().addReport(new ReportCardDeactivated(pCard));

    if (pCard.getTarget().isPlayedOnPlayer()) {
      Player player = game.getFieldModel().findPlayer(pCard);
      if (player != null) {
        if (!pCard.isRemainsInPlay()) {
          game.getFieldModel().removeCard(player, pCard);
        }
        switch (pCard) {
          case CUSTARD_PIE:
            deactivateCardCustardPie(pStep, player);
            break;
          case DISTRACT:
            deactivateCardDistract(pStep, player);
            break;
          case WITCH_BREW:
            deactivateCardWitchBrew(pStep, player);
            break;
          default:
            break;
        }
      }
    } else {
      switch (pCard) {
        case ILLEGAL_SUBSTITUTION:
          deactivateCardIllegalSubstitution(pStep);
          break;
        default:
          break;
      }
    }
  }

  private static void deactivateCardCustardPie(IStep pStep, Player pPlayer) {
    Game game = pStep.getGameState().getGame();
    PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
    if ((playerState != null) && playerState.isHypnotized()) {
      game.getFieldModel().setPlayerState(pPlayer, playerState.changeHypnotized(false));
    }
  }

  private static void deactivateCardDistract(IStep pStep, Player pPlayer) {
    Game game = pStep.getGameState().getGame();
    Player[] players = game.getFieldModel().findPlayers(CardEffect.DISTRACTED);
    for (Player player : players) {
      game.getFieldModel().removeCardEffect(player, CardEffect.DISTRACTED);
      PlayerState playerState = game.getFieldModel().getPlayerState(player);
      if (!hasSkillWithProperty(player, NamedProperties.appliesConfusion) && playerState.isConfused()) {
        game.getFieldModel().setPlayerState(player, playerState.changeConfused(false));
      }
    }
  }

  private static void deactivateCardWitchBrew(IStep pStep, Player pPlayer) {
    Game game = pStep.getGameState().getGame();
    if (game.getFieldModel().hasCardEffect(pPlayer, CardEffect.SEDATIVE)) {
      game.getFieldModel().removeCardEffect(pPlayer, CardEffect.SEDATIVE);
    }
    if (game.getFieldModel().hasCardEffect(pPlayer, CardEffect.MAD_CAP_MUSHROOM_POTION)) {
      game.getFieldModel().removeCardEffect(pPlayer, CardEffect.MAD_CAP_MUSHROOM_POTION);
    }
  }

  private static void deactivateCardIllegalSubstitution(IStep pStep) {
    Game game = pStep.getGameState().getGame();
    Player[] players = game.getFieldModel().findPlayers(CardEffect.ILLEGALLY_SUBSTITUTED);
    for (Player player : players) {
      game.getFieldModel().removeCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED);
    }
  }

  private static void activateCardDistract(IStep pStep, Player pPlayer) {
    Game game = pStep.getGameState().getGame();
    Team otherTeam = UtilPlayer.findOtherTeam(game, pPlayer);
    FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
    FieldCoordinate[] adjacentCoordinates = game.getFieldModel().findAdjacentCoordinates(playerCoordinate, FieldCoordinateBounds.FIELD, 3, false);
    for (FieldCoordinate coordinate : adjacentCoordinates) {
      Player otherPlayer = game.getFieldModel().getPlayer(coordinate);
      if ((otherPlayer != null) && otherTeam.hasPlayer(otherPlayer)) {
        game.getFieldModel().addCardEffect(otherPlayer, CardEffect.DISTRACTED);
      }
    }
  }

  private static void activateCardCustardPie(IStep pStep, Player pPlayer) {
    Game game = pStep.getGameState().getGame();
    PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
    game.getFieldModel().setPlayerState(pPlayer, playerState.changeHypnotized(true));
  }

  private static void activateCardPitTrap(IStep pStep, Player pPlayer) {
    pStep.publishParameters(UtilServerInjury.dropPlayer(pStep, pPlayer, ApothecaryMode.DEFENDER));
  }

  private static void activateCardWitchBrew(IStep pStep, Player pPlayer) {
    Game game = pStep.getGameState().getGame();
    int roll = pStep.getGameState().getDiceRoller().rollCardEffect();
    CardEffect cardEffect = DiceInterpreter.getInstance().interpretWitchBrewRoll(roll);
    game.getFieldModel().addCardEffect(pPlayer, cardEffect);
    ReportCardEffectRoll cardEffectReport = new ReportCardEffectRoll(Card.WITCH_BREW, roll);
    cardEffectReport.setCardEffect(cardEffect);
    pStep.getResult().addReport(cardEffectReport);
  }

  public static ServerSkill getSkillCancelling(Game game, Player player, ServerSkill skill) {
    for (Skill playerSkill : player.getSkills()) {
      ServerSkill serverSkill = (ServerSkill)playerSkill;
      if (serverSkill.canCancel(skill)) {
        return serverSkill;
      }
    }
    return null;
  }

  public static ServerSkill getSkillWithProperty(Player player, ISkillProperty property) {
    for (Skill playerSkill : player.getSkills()) {
      ServerSkill serverSkill = (ServerSkill)playerSkill;
      if (serverSkill.hasSkillProperty(property)) {
        return serverSkill;
      }
    }
    return null;
  }
  
  public static boolean hasSkillWithProperty(Player player, ISkillProperty property) {
    return getSkillWithProperty(player, property) != null;
  }
}

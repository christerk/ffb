package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.dialog.DialogSetupErrorParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class UtilKickoffSequence {
	
  public static boolean checkSetup(GameState pGameState, boolean pHomeTeam) {
  	if (pGameState == null) {
  		throw new IllegalArgumentException("Parameter gameState must not be null.");
  	}
    int playersOnField = 0;
    int playersInUpperWideZone = 0;
    int playersInLowerWideZone = 0;
    int playersOnLos = 0;
    int availablePlayers = 0;
    Game game = pGameState.getGame();
    List<String> messageList = new ArrayList<String>(); 
    Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
    for (Player player : team.getPlayers()) {
      PlayerState playerState = game.getFieldModel().getPlayerState(player);
      if (playerState.canBeSetUp()) {
        availablePlayers++;
      }
      FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
      if ((pHomeTeam && FieldCoordinateBounds.HALF_HOME.isInBounds(playerCoordinate)) || (!pHomeTeam && FieldCoordinateBounds.HALF_AWAY.isInBounds(playerCoordinate))) {
        playersOnField++;
      }
      if ((pHomeTeam && FieldCoordinateBounds.UPPER_WIDE_ZONE_HOME.isInBounds(playerCoordinate)) || (!pHomeTeam && FieldCoordinateBounds.UPPER_WIDE_ZONE_AWAY.isInBounds(playerCoordinate))) {
        playersInUpperWideZone++;
      }
      if ((pHomeTeam && FieldCoordinateBounds.LOWER_WIDE_ZONE_HOME.isInBounds(playerCoordinate)) || (!pHomeTeam && FieldCoordinateBounds.LOWER_WIDE_ZONE_AWAY.isInBounds(playerCoordinate))) {
        playersInLowerWideZone++;
      }
      if ((pHomeTeam && FieldCoordinateBounds.LOS_HOME.isInBounds(playerCoordinate)) || (!pHomeTeam && FieldCoordinateBounds.LOS_AWAY.isInBounds(playerCoordinate))) {
        playersOnLos++;
      }
    }
    int maxPlayersOnField = game.getOptions().getOptionValue(GameOption.MAX_PLAYERS_ON_FIELD).getValue();
    if (playersOnField > maxPlayersOnField) {
      messageList.add("You placed " + playersOnField + " Players on the field. Maximum are " + maxPlayersOnField + " players.");
    }
    if ((playersOnField < maxPlayersOnField) && (availablePlayers >= maxPlayersOnField)) {
      messageList.add("You placed " + playersOnField + " Players on the field. You have to put " + maxPlayersOnField + " players on the field.");
    } else {
      if ((playersOnField < maxPlayersOnField) && (playersOnField < availablePlayers)) {
        messageList.add("You placed " + playersOnField + " Players on the field. You have to put all players on the field.");
      }
    }
    int maxPlayersInWideZone = game.getOptions().getOptionValue(GameOption.MAX_PLAYERS_IN_WIDE_ZONE).getValue();
    if (playersInLowerWideZone > maxPlayersInWideZone) {
      messageList.add("You placed " + playersInLowerWideZone + " Players in the lower wide zone. Only " + maxPlayersInWideZone + " allowed there.");
    }
    if (playersInUpperWideZone > maxPlayersInWideZone) {
      messageList.add("You placed " + playersInUpperWideZone + " Players in the upper wide zone. Only " + maxPlayersInWideZone + " allowed there.");
    }
    int minPlayersOnLos = game.getOptions().getOptionValue(GameOption.MIN_PLAYERS_ON_LOS).getValue();
    if ((playersOnLos < minPlayersOnLos) && (availablePlayers >= minPlayersOnLos)) {
      messageList.add("You placed " + playersOnLos + " Players on the Line of Scrimmage. You have to put " + minPlayersOnLos + " there.");
    } else {
      if (playersOnLos < minPlayersOnLos && playersOnLos < availablePlayers) {
        messageList.add("You placed " + playersOnLos + " Players on the Line of Scrimmage. You have to put all your Players there.");
      }
    }
    if (messageList.size() > 0) {
      UtilDialog.showDialog(pGameState, new DialogSetupErrorParameter(team.getId(), messageList.toArray(new String[messageList.size()])));
      return false;
    } else {
      return true;
    }
  }
  
  public static void pinPlayersInTacklezones(GameState pGameState, Team pTeam) {
    Game game = pGameState.getGame();
    Team otherTeam = (pTeam == game.getTeamHome()) ? game.getTeamAway() : game.getTeamHome();
    for (Player player : pTeam.getPlayers()) {
      PlayerState playerState = game.getFieldModel().getPlayerState(player);
      if (playerState.isActive()) {
        FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
        if (UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, playerCoordinate, false).length > 0) {
          game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
        }
      }
    }
  }

}

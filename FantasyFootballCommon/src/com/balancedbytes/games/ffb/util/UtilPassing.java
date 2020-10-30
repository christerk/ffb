package com.balancedbytes.games.ffb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PassingDistanceFactory;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;

/**
 * 
 * @author Kalimar
 */
public class UtilPassing {

  public static double RULER_WIDTH = 1.74;

  private static final String[] _THROWING_RANGE_TABLE = new String[] {
      "T Q Q Q S S S L L L L B B B",
      "Q Q Q Q S S S L L L L B B B",
      "Q Q Q S S S S L L L L B B  ",
      "Q Q S S S S S L L L B B B  ",
      "S S S S S S L L L L B B B  ",
      "S S S S S L L L L B B B    ",
      "S S S S L L L L L B B B    ",
      "L L L L L L L L B B B      ",
      "L L L L L L L B B B B      ",
      "L L L L L B B B B B        ",
      "L L L B B B B B B          ",
      "B B B B B B B              ",
      "B B B B B                  ",
      "B B                        "
  };

  private static final PassingDistance[][] _PASSING_DISTANCES_TABLE = new PassingDistance[14][14];

  static {
    PassingDistanceFactory passingDistanceFactory = new PassingDistanceFactory();
    for (int y = 0; y < 14; y++) {
      for (int x = 0; x < 14; x++) {
        _PASSING_DISTANCES_TABLE[y][x] = passingDistanceFactory.forShortcut(_THROWING_RANGE_TABLE[y].charAt(x * 2));
      }
    }
  }

  public static PassingDistance findPassingDistance(Game pGame, FieldCoordinate pFromCoordinate, FieldCoordinate pToCoordinate, boolean pThrowTeamMate) {
    PassingDistance passingDistance = null;
    if ((pFromCoordinate != null) && (pToCoordinate != null)) {
      int deltaY = Math.abs(pToCoordinate.getY() - pFromCoordinate.getY());
      int deltaX = Math.abs(pToCoordinate.getX() - pFromCoordinate.getX());
      if ((deltaY < 14) && (deltaX < 14)) {
        passingDistance = _PASSING_DISTANCES_TABLE[deltaY][deltaX];
      }
      if ((pThrowTeamMate || (Weather.BLIZZARD == pGame.getFieldModel().getWeather()))
          && ((passingDistance == PassingDistance.LONG_BOMB) || (passingDistance == PassingDistance.LONG_PASS))) {
        passingDistance = null;
      }
    }
    return passingDistance;
  }

  public static Player[] findInterceptors(Game pGame, Player pThrower, FieldCoordinate pTargetCoordinate) {
    List<Player> interceptors = new ArrayList<Player>();
    if ((pTargetCoordinate != null) && (pThrower != null)) {
      FieldCoordinate throwerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pThrower);
      Team otherTeam = pGame.getTeamHome().hasPlayer(pThrower) ? pGame.getTeamAway() : pGame.getTeamHome();
      Player[] otherPlayers = otherTeam.getPlayers();
      for (int i = 0; i < otherPlayers.length; i++) {
        PlayerState interceptorState = pGame.getFieldModel().getPlayerState(otherPlayers[i]);
        FieldCoordinate interceptorCoordinate = pGame.getFieldModel().getPlayerCoordinate(otherPlayers[i]);
        if ((interceptorCoordinate != null) && (interceptorState != null) && interceptorState.hasTacklezones()
            && !UtilCards.hasSkill(pGame, otherPlayers[i], Skill.NO_HANDS)) {
          if (canIntercept(throwerCoordinate, pTargetCoordinate, interceptorCoordinate)) {
            interceptors.add(otherPlayers[i]);
          }
        }
      }
    }
    return interceptors.toArray(new Player[interceptors.size()]);
  }

  private static boolean canIntercept(FieldCoordinate pThrowerCoordinate, FieldCoordinate pTargetCoordinate, FieldCoordinate pIinterceptorCoordinate) {
    int receiverX = pTargetCoordinate.getX() - pThrowerCoordinate.getX();
    int receiverY = pTargetCoordinate.getY() - pThrowerCoordinate.getY();
    int interceptorX = pIinterceptorCoordinate.getX() - pThrowerCoordinate.getX();
    int interceptorY = pIinterceptorCoordinate.getY() - pThrowerCoordinate.getY();
    int a = ((receiverX - interceptorX) * (receiverX - interceptorX)) + ((receiverY - interceptorY) * (receiverY - interceptorY));
    int b = (interceptorX * interceptorX) + (interceptorY * interceptorY);
    int c = (receiverX * receiverX) + (receiverY * receiverY);
    double d1 = Math.abs((receiverY * (interceptorX + 0.5)) - (receiverX * (interceptorY + 0.5)));
    double d2 = Math.abs((receiverY * (interceptorX + 0.5)) - (receiverX * (interceptorY - 0.5)));
    double d3 = Math.abs((receiverY * (interceptorX - 0.5)) - (receiverX * (interceptorY + 0.5)));
    double d4 = Math.abs((receiverY * (interceptorX - 0.5)) - (receiverX * (interceptorY - 0.5)));
    return (c > a) && (c > b) && (RULER_WIDTH > (2 * Math.min(Math.min(Math.min(d1, d2), d3), d4) / Math.sqrt(c)));
  }

  public static Set<FieldCoordinate> findValidPassBlockEndCoordinates(Game pGame) {

    Set<FieldCoordinate> validCoordinates = new HashSet<FieldCoordinate>();

    // Sanity checks
    if ((pGame == null) || (pGame.getThrower() == null) || (pGame.getPassCoordinate() == null)) {
      return validCoordinates;
    }

    ActingPlayer actingPlayer = pGame.getActingPlayer();

    // Add the thrower tacklezone
    FieldCoordinate[] neighbours = pGame.getFieldModel().findAdjacentCoordinates(pGame.getFieldModel().getPlayerCoordinate(pGame.getThrower()),
        FieldCoordinateBounds.FIELD, 1, false);
    for (FieldCoordinate c : neighbours) {
      Player playerInTz = pGame.getFieldModel().getPlayer(c);
      if ((playerInTz == null) || (playerInTz == actingPlayer.getPlayer())) {
        validCoordinates.add(c);
      }
    }

    Player targetPlayer = pGame.getFieldModel().getPlayer(pGame.getPassCoordinate());

    if (PlayerAction.HAIL_MARY_PASS == pGame.getThrowerAction()) {

      if (targetPlayer != null) {
        validCoordinates.add(pGame.getPassCoordinate());
      }

    } else {

      validCoordinates.addAll(findInterceptCoordinates(pGame));

      // If there's a target, add the target's tacklezones
      if (targetPlayer != null) {
        neighbours = pGame.getFieldModel().findAdjacentCoordinates(pGame.getPassCoordinate(), FieldCoordinateBounds.FIELD, 1, false);
        for (FieldCoordinate c : neighbours) {
          Player playerInTz = pGame.getFieldModel().getPlayer(c);
          if ((playerInTz == null) || (playerInTz == actingPlayer.getPlayer())) {
            validCoordinates.add(c);
          }
        }
      } else {
        validCoordinates.add(pGame.getPassCoordinate());
      }

    }

    return validCoordinates;

  }

  private static Set<FieldCoordinate> findInterceptCoordinates(Game pGame) {

    FieldModel fieldModel = pGame.getFieldModel();
    Set<FieldCoordinate> eligibleCoordinates = new HashSet<FieldCoordinate>();
    Set<FieldCoordinate> closedSet = new HashSet<FieldCoordinate>();
    List<FieldCoordinate> openSet = new ArrayList<FieldCoordinate>();
    FieldCoordinate throwerCoord = fieldModel.getPlayerCoordinate(pGame.getThrower());

    // Start with the thrower's location.
    openSet.add(throwerCoord);

    while (!openSet.isEmpty()) {
      // Get an unprocessed coordinate
      FieldCoordinate currentCoordinate = openSet.remove(0);

      // Since coordinates may be added multiple times to the open set, let's check if we already processed this coordinate
      if (closedSet.contains(currentCoordinate)) {
        continue;
      }

      if (currentCoordinate.equals(throwerCoord) || canIntercept(throwerCoord, pGame.getPassCoordinate(), currentCoordinate)) {
        // This coordinate is eligible to intercept, so we add it to the list...
        eligibleCoordinates.add(currentCoordinate);

        // ... and queue all adjacent non-processed squares for processing
        FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(currentCoordinate, FieldCoordinateBounds.FIELD, 1, false);
        for (FieldCoordinate c : adjacentCoordinates)
          if (!closedSet.contains(c)) {
            openSet.add(c);
          }
      }

      // Mark the coordinate as processed
      closedSet.add(currentCoordinate);
    }

    // Remove coordinates occupied by players in the list.
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    FieldCoordinate actingPlayerPosition = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
    FieldCoordinate[] playerCoordinates = fieldModel.getPlayerCoordinates();
    for (FieldCoordinate pCoord : playerCoordinates) {
      if (!pCoord.equals(actingPlayerPosition)) {
        eligibleCoordinates.remove(pCoord);
      }
    }

    return eligibleCoordinates;

  }

}

package com.balancedbytes.games.ffb.server.util;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;


/**
 * 
 * @author Kalimar
 */
public class UtilServerPushback {
  
  public static PushbackSquare findStartingSquare(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, boolean pHomeChoice) {

    int deltaX = pStartCoordinate.getX() - pEndCoordinate.getX();
    int deltaY = pStartCoordinate.getY() - pEndCoordinate.getY();
    
    if (deltaY < 0) {
      if (deltaX < 0) {
        return new PushbackSquare(pEndCoordinate, Direction.SOUTHEAST, pHomeChoice);
      } else if (deltaX > 0) {
        return new PushbackSquare(pEndCoordinate, Direction.SOUTHWEST, pHomeChoice);
      } else {
        return new PushbackSquare(pEndCoordinate, Direction.SOUTH, pHomeChoice);
      }
    
    } else if (deltaY > 0) {
      if (deltaX < 0) {
        return new PushbackSquare(pEndCoordinate, Direction.NORTHEAST, pHomeChoice);
      } else if (deltaX > 0) {
        return new PushbackSquare(pEndCoordinate, Direction.NORTHWEST, pHomeChoice);
      } else {
        return new PushbackSquare(pEndCoordinate, Direction.NORTH, pHomeChoice);
      }
    
    } else {
      if (deltaX < 0) {
        return new PushbackSquare(pEndCoordinate, Direction.EAST, pHomeChoice);
      } else if (deltaX > 0) {
        return new PushbackSquare(pEndCoordinate, Direction.WEST, pHomeChoice);
      } else {
        return null;  // startCoordinate == endCoordinate
      }
    }
        
  }
 
  public static PushbackSquare[] findPushbackSquares(Game pGame, PushbackSquare pStartingSquare, PushbackMode pPushbackMode) {
    
    List<PushbackSquare> pushbackSquares = new ArrayList<PushbackSquare>();
    FieldCoordinate startCoordinate = pStartingSquare.getCoordinate();
    
    FieldCoordinate northCoordinate = new FieldCoordinate(startCoordinate.getX(), startCoordinate.getY() - 1);
    FieldCoordinate northEastCoordinate = new FieldCoordinate(startCoordinate.getX() + 1, startCoordinate.getY() - 1);
    FieldCoordinate eastCoordinate = new FieldCoordinate(startCoordinate.getX() + 1, startCoordinate.getY());
    FieldCoordinate southEastCoordinate = new FieldCoordinate(startCoordinate.getX() + 1, startCoordinate.getY() + 1);
    FieldCoordinate southCoordinate = new FieldCoordinate(startCoordinate.getX(), startCoordinate.getY() + 1);
    FieldCoordinate southWestCoordinate = new FieldCoordinate(startCoordinate.getX() - 1, startCoordinate.getY() + 1);
    FieldCoordinate westCoordinate = new FieldCoordinate(startCoordinate.getX() - 1, startCoordinate.getY());
    FieldCoordinate northWestCoordinate = new FieldCoordinate(startCoordinate.getX() - 1, startCoordinate.getY() - 1);

    boolean homeChoice = pGame.isHomePlaying();
    if ((pPushbackMode == PushbackMode.SIDE_STEP) || (pPushbackMode == PushbackMode.GRAB)) {
      FieldCoordinate[] adjacentCoordinates = pGame.getFieldModel().findAdjacentCoordinates(startCoordinate, FieldCoordinateBounds.FIELD, 1, false);
      for (int i = 0; i < adjacentCoordinates.length; i++) {
        if (pGame.getFieldModel().getPlayer(adjacentCoordinates[i]) == null) {
          pushbackSquares.add(findStartingSquare(startCoordinate, adjacentCoordinates[i], homeChoice));
        }
      }
      if ((pPushbackMode == PushbackMode.SIDE_STEP) && (pGame.getDefender() != null)) {
        boolean sideStepPlayerHome = pGame.getTeamHome().hasPlayer(pGame.getDefender());
        for (PushbackSquare pushbackSquare : pushbackSquares) {
          pushbackSquare.setHomeChoice(sideStepPlayerHome);
        }
      }
    }
    
    if (pushbackSquares.size() == 0) {
      
      switch (pStartingSquare.getDirection()) {
        case NORTH:
          pushbackSquares.add(new PushbackSquare(northWestCoordinate, Direction.NORTHWEST, homeChoice));
          pushbackSquares.add(new PushbackSquare(northCoordinate, Direction.NORTH, homeChoice));
          pushbackSquares.add(new PushbackSquare(northEastCoordinate, Direction.NORTHEAST, homeChoice));
          break;
        case NORTHEAST:
          pushbackSquares.add(new PushbackSquare(northCoordinate, Direction.NORTH, homeChoice));
          pushbackSquares.add(new PushbackSquare(northEastCoordinate, Direction.NORTHEAST, homeChoice));
          pushbackSquares.add(new PushbackSquare(eastCoordinate, Direction.EAST, homeChoice));
          break;
        case EAST:
          pushbackSquares.add(new PushbackSquare(northEastCoordinate, Direction.NORTHEAST, homeChoice));
          pushbackSquares.add(new PushbackSquare(eastCoordinate, Direction.EAST, homeChoice));
          pushbackSquares.add(new PushbackSquare(southEastCoordinate, Direction.SOUTHEAST, homeChoice));
          break;
        case SOUTHEAST:
          pushbackSquares.add(new PushbackSquare(eastCoordinate, Direction.EAST, homeChoice));
          pushbackSquares.add(new PushbackSquare(southEastCoordinate, Direction.SOUTHEAST, homeChoice));
          pushbackSquares.add(new PushbackSquare(southCoordinate, Direction.SOUTH, homeChoice));
          break;
        case SOUTH:
          pushbackSquares.add(new PushbackSquare(southEastCoordinate, Direction.SOUTHEAST, homeChoice));
          pushbackSquares.add(new PushbackSquare(southCoordinate, Direction.SOUTH, homeChoice));
          pushbackSquares.add(new PushbackSquare(southWestCoordinate, Direction.SOUTHWEST, homeChoice));
          break;
        case SOUTHWEST:
          pushbackSquares.add(new PushbackSquare(southCoordinate, Direction.SOUTH, homeChoice));
          pushbackSquares.add(new PushbackSquare(southWestCoordinate, Direction.SOUTHWEST, homeChoice));
          pushbackSquares.add(new PushbackSquare(westCoordinate, Direction.WEST, homeChoice));
          break;
        case WEST:
          pushbackSquares.add(new PushbackSquare(southWestCoordinate, Direction.SOUTHWEST, homeChoice));
          pushbackSquares.add(new PushbackSquare(westCoordinate, Direction.WEST, homeChoice));
          pushbackSquares.add(new PushbackSquare(northWestCoordinate, Direction.NORTHWEST, homeChoice));
          break;
        case NORTHWEST:
          pushbackSquares.add(new PushbackSquare(westCoordinate, Direction.WEST, homeChoice));
          pushbackSquares.add(new PushbackSquare(northWestCoordinate, Direction.NORTHWEST, homeChoice));
          pushbackSquares.add(new PushbackSquare(northCoordinate, Direction.NORTH, homeChoice));
          break;
      }
  
      List<PushbackSquare> validPushbackSquares = new ArrayList<PushbackSquare>();
      for (int i = 0; i < pushbackSquares.size(); i++) {
        PushbackSquare pushbackSquare = pushbackSquares.get(i);
        if (FieldCoordinateBounds.FIELD.isInBounds(pushbackSquare.getCoordinate())) {
          validPushbackSquares.add(pushbackSquare);
        }
      }
      pushbackSquares = validPushbackSquares;
  
      if (pushbackSquares.size() > 0) {
        
        boolean freeSquare = false;
        FieldModel fieldModel = pGame.getFieldModel();
        for (int i = 0; !freeSquare && (i < pushbackSquares.size()); i++) {
          Player player = fieldModel.getPlayer(pushbackSquares.get(i).getCoordinate());
          if (player == null) {
            freeSquare = true;
          }
        }
        
        if (freeSquare) {
          List<PushbackSquare> freePushBackSquares = new ArrayList<PushbackSquare>();
          for (int i = 0; i < pushbackSquares.size(); i++) {
            Player player = fieldModel.getPlayer(pushbackSquares.get(i).getCoordinate());
            if (player == null) {
              freePushBackSquares.add(pushbackSquares.get(i));
            }
          }
          pushbackSquares = freePushBackSquares;

        } else {
          // crowdpush if one arrow points outside and there is no free square
          if (pushbackSquares.size() < 3) {
            pushbackSquares.clear();
          }
        }
        
      }
    
    }
    
    return pushbackSquares.toArray(new PushbackSquare[pushbackSquares.size()]);
    
  }
  
}

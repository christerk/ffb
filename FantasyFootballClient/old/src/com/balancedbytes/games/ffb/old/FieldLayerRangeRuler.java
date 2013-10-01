package com.balancedbytes.games.ffb.client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.client.state.ClientState;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;

/**
 * 
 * @author Kalimar
 */
public class FieldLayerRangeRuler extends FieldLayer {

  public static final int LENGTH_QUICK_PASS = (3 * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2);
  public static final int LENGTH_SHORT_PASS = LENGTH_QUICK_PASS + (4 * FIELD_SQUARE_SIZE);
  public static final int LENGTH_LONG_PASS = LENGTH_SHORT_PASS + (3 * FIELD_SQUARE_SIZE);
  public static final int LENGTH_LONG_BOMB = LENGTH_LONG_PASS + (3 * FIELD_SQUARE_SIZE);

  public static final Color COLOR_QUICK_PASS = new Color(1.0f, 1.0f, 1.0f, 0.3f);
  public static final Color COLOR_SHORT_PASS = new Color(0.0f, 1.0f, 0.0f, 0.3f);
  public static final Color COLOR_LONG_PASS = new Color(1.0f, 1.0f, 0.0f, 0.3f);
  public static final Color COLOR_LONG_BOMB = new Color(1.0f, 0.0f, 0.0f, 0.3f);
  public static final Color COLOR_INTERCEPTION = new Color(0.0f, 0.0f, 0.0f, 0.3f);
  public static final Color COLOR_SELECT_SQUARE = new Color(0.0f, 0.0f, 1.0f, 0.2f);

  private Polygon fPolygonQuickPass;
  private Polygon fPolygonShortPass;
  private Polygon fPolygonLongPass;
  private Polygon fPolygonLongBomb;
  private Polygon fPolygonComplete;

  private List<Player> fInterceptors;
  private FieldCoordinate fSelectSquareCoordinate;
  private Rectangle fLastBallBounds;

  protected FieldLayerRangeRuler(IBloodBowlClient pClient) {
    super(pClient);
    fInterceptors = new ArrayList<Player>();
  }

  public void drawRangeRuler(FieldCoordinate pTargetCoordinate) {

    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();

    if ((pTargetCoordinate != null) && FieldCoordinateBounds.PLAYING_AREA.isInBounds(pTargetCoordinate)
        && (actingPlayer.getPlayer() != null)) {

      FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

      removeRangeRuler();

      Point startCenter = new Point((throwerCoordinate.getX() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2),
          (throwerCoordinate.getY() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2));
      Point endCenter = new Point((pTargetCoordinate.getX() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2),
          (pTargetCoordinate.getY() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2));

      int lengthY = startCenter.y - endCenter.y;
      int lengthX = endCenter.x - startCenter.x;
      double length = Math.sqrt((lengthY * lengthY) + (lengthX * lengthX));

      double sinPhi = lengthY / length;
      double cosPhi = lengthX / length;

      int quickPassLength = Math.min(LENGTH_QUICK_PASS, (int) length);
      fPolygonQuickPass = findPolygon(startCenter, 0, quickPassLength, sinPhi, cosPhi);
      int shortPassLength = Math.min(LENGTH_SHORT_PASS, (int) length);
      fPolygonShortPass = findPolygon(startCenter, LENGTH_QUICK_PASS, shortPassLength, sinPhi, cosPhi);
      int longPassLength = Math.min(LENGTH_LONG_PASS, (int) length);
      fPolygonLongPass = findPolygon(startCenter, LENGTH_SHORT_PASS, longPassLength, sinPhi, cosPhi);
      int longBombLength = Math.min(LENGTH_LONG_BOMB, (int) length);
      fPolygonLongBomb = findPolygon(startCenter, LENGTH_LONG_PASS, longBombLength, sinPhi, cosPhi);
      fPolygonComplete = findPolygon(startCenter, 0, longBombLength, sinPhi, cosPhi);

      Graphics2D g2d = getImage().createGraphics();
      if (fPolygonQuickPass != null) {
        g2d.setPaint(COLOR_QUICK_PASS);
        g2d.fillPolygon(fPolygonQuickPass);
      }
      if (fPolygonShortPass != null) {
        g2d.setPaint(COLOR_SHORT_PASS);
        g2d.fillPolygon(fPolygonShortPass);
      }
      if (fPolygonLongPass != null) {
        g2d.setPaint(COLOR_LONG_PASS);
        g2d.fillPolygon(fPolygonLongPass);
      }
      if (fPolygonLongBomb != null) {
        g2d.setPaint(COLOR_LONG_BOMB);
        g2d.fillPolygon(fPolygonLongBomb);
      }

      if ((getClient().getCommunication().getLoginMode() != ClientMode.SPECTATOR)
          && getClient().getGame().isHomePlaying()) {

        // [ cos(theta) -sin(theta) tx ]
        // [ sin(theta) cos(theta) ty ]
        // [ 0 0 1 ]

        // [ m00 m01 m02 ]
        // [ m10 m11 m12 ]
        // [ 0 0 1 ]

        g2d.transform(new AffineTransform(cosPhi, -sinPhi, sinPhi, cosPhi, startCenter.x, startCenter.y));
        g2d.setFont(new Font("Sans Serif", Font.BOLD, 32));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));

        if (fPolygonQuickPass != null) {
          drawRulerModifier(g2d, quickPassLength, 0, findMinimumRoll(PassingDistance.QUICK_PASS));
        }
        if (fPolygonShortPass != null) {
          drawRulerModifier(g2d, shortPassLength, quickPassLength, findMinimumRoll(PassingDistance.SHORT_PASS));
        }
        if (fPolygonLongPass != null) {
          drawRulerModifier(g2d, longPassLength, shortPassLength, findMinimumRoll(PassingDistance.LONG_PASS));
        }
        if (fPolygonLongBomb != null) {
          drawRulerModifier(g2d, longBombLength, longPassLength, findMinimumRoll(PassingDistance.LONG_BOMB));
        }

      }

      g2d.dispose();

      if (fPolygonComplete != null) {
        Rectangle bounds = fPolygonComplete.getBounds();
        addUpdatedArea(bounds);
      }

      Team otherTeam = null;
      if (game.isHomePlaying()) {
        otherTeam = game.getTeamAway();
      } else {
        otherTeam = game.getTeamHome();
      }
      Player[] otherPlayers = otherTeam.getPlayers();
      for (int i = 0; i < otherPlayers.length; i++) {
        FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(otherPlayers[i]);
        PlayerState playerState = game.getFieldModel().getPlayerState(otherPlayers[i]);
        if ((testCoordinateInsideRangeRuler(playerCoordinate) != null) && (playerState != null)
            && playerState.isAbleToCatch()) {
          fInterceptors.add(otherPlayers[i]);
          clear(playerCoordinate);
          drawSelectSquare(playerCoordinate, FieldLayerRangeRuler.COLOR_INTERCEPTION);
        }
      }

      Color selectSquareColor = null;
      Player catcher = game.getFieldModel().getPlayer(pTargetCoordinate);
      if ((catcher == null) || otherTeam.hasPlayer(catcher)) {
        selectSquareColor = COLOR_SELECT_SQUARE;
      } else {
        PassingDistance passingDistance = testCoordinateInsideRangeRuler(pTargetCoordinate);
        if (passingDistance != null) {
          switch (passingDistance) {
          case LONG_BOMB:
            selectSquareColor = COLOR_LONG_BOMB;
            break;
          case LONG_PASS:
            selectSquareColor = COLOR_LONG_PASS;
            break;
          case SHORT_PASS:
            selectSquareColor = COLOR_SHORT_PASS;
            break;
          case QUICK_PASS:
            selectSquareColor = COLOR_QUICK_PASS;
            break;
          }
        }
      }

      if (selectSquareColor != null) {
        fSelectSquareCoordinate = pTargetCoordinate;
        clear(fSelectSquareCoordinate);
        drawSelectSquare(fSelectSquareCoordinate, selectSquareColor);
      }

    }

  }

  public void removeRangeRuler() {
    Game game = getClient().getGame();
    if (fPolygonComplete != null) {
      Rectangle oldBounds = fPolygonComplete.getBounds();
      clear(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height);
    }
    fPolygonComplete = null;
    Player[] interceptors = getInterceptors();
    for (int i = 0; i < interceptors.length; i++) {
      FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(interceptors[i]);
      clear(playerCoordinate);
    }
    fInterceptors.clear();
    if (fSelectSquareCoordinate != null) {
      clear(fSelectSquareCoordinate);
    }
    fSelectSquareCoordinate = null;
  }

  public PassingDistance testCoordinateInsideRangeRuler(FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      int x = pCoordinate.getX() * FIELD_SQUARE_SIZE;
      int y = pCoordinate.getY() * FIELD_SQUARE_SIZE;
      Rectangle playerSquare = new Rectangle(x, y, FIELD_SQUARE_SIZE, FIELD_SQUARE_SIZE);
      if ((fPolygonLongBomb != null) && fPolygonLongBomb.intersects(playerSquare)) {
        return PassingDistance.LONG_BOMB;
      }
      if ((fPolygonLongPass != null) && fPolygonLongPass.intersects(playerSquare)) {
        return PassingDistance.LONG_PASS;
      }
      if ((fPolygonShortPass != null) && fPolygonShortPass.intersects(playerSquare)) {
        return PassingDistance.SHORT_PASS;
      }
      if ((fPolygonQuickPass != null) && fPolygonQuickPass.intersects(playerSquare)) {
        return PassingDistance.QUICK_PASS;
      }
    }
    return null;
  }

  private Polygon findPolygon(Point pStartCenter, int pMinLength, int pMaxLength, double pSinPhi, double pCosPhi) {

    if (pMaxLength > pMinLength) {

      Point point1 = new Point(pStartCenter.x + pMinLength, pStartCenter.y - FIELD_SQUARE_SIZE);
      point1 = rotate(point1, pStartCenter, pSinPhi, pCosPhi);
      Point point2 = new Point(pStartCenter.x + pMinLength, pStartCenter.y + FIELD_SQUARE_SIZE);
      point2 = rotate(point2, pStartCenter, pSinPhi, pCosPhi);
      Point point3 = new Point(pStartCenter.x + pMaxLength, pStartCenter.y + FIELD_SQUARE_SIZE);
      point3 = rotate(point3, pStartCenter, pSinPhi, pCosPhi);
      Point point4 = new Point(pStartCenter.x + pMaxLength, pStartCenter.y - FIELD_SQUARE_SIZE);
      point4 = rotate(point4, pStartCenter, pSinPhi, pCosPhi);

      return new Polygon(new int[] { point1.x, point2.x, point3.x, point4.x }, new int[] { point1.y, point2.y,
          point3.y, point4.y }, 4);

    } else {
      return null;
    }

  }

  private Point rotate(Point pPoint, Point pCenter, double pSinPhi, double pCosPhi) {
    int x = pPoint.x - pCenter.x;
    int y = pPoint.y - pCenter.y;
    return new Point((int) ((pCosPhi * x) + (pSinPhi * y) + pCenter.getX()),
        (int) ((-pSinPhi * x) + (pCosPhi * y) + pCenter.getY()));
  }

  private void drawSelectSquare(FieldCoordinate pCoordinate, Color pColor) {
    if (pCoordinate != null) {
      int x = pCoordinate.getX() * FIELD_SQUARE_SIZE;
      int y = pCoordinate.getY() * FIELD_SQUARE_SIZE;
      Rectangle bounds = new Rectangle(x, y, FIELD_SQUARE_SIZE, FIELD_SQUARE_SIZE);
      Graphics2D g2d = getImage().createGraphics();
      g2d.setPaint(pColor);
      g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
      g2d.dispose();
      addUpdatedArea(bounds);
    }
  }

  public Player[] getInterceptors() {
    return fInterceptors.toArray(new Player[fInterceptors.size()]);
  }

  public void animateThrownBall(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate,
      FieldCoordinate pInterceptorCoordinate, int pStepping) {

    fLastBallBounds = null;

    int startX = (pStartCoordinate.getX() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
    int startY = (pStartCoordinate.getY() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);

    int endX = (pEndCoordinate.getX() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
    int endY = (pEndCoordinate.getY() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);

    int interceptorX = endX;
    int interceptorY = endY;

    if (pInterceptorCoordinate != null) {
      interceptorX = (pInterceptorCoordinate.getX() * ClientState.FIELD_SQUARE_SIZE)
          + (ClientState.FIELD_SQUARE_SIZE / 2);
      interceptorY = (pInterceptorCoordinate.getY() * ClientState.FIELD_SQUARE_SIZE)
          + (ClientState.FIELD_SQUARE_SIZE / 2);
    }

    if (Math.abs(endX - startX) > Math.abs(endY - startY)) {
      if (startX < endX) {
        for (int x = startX; x < interceptorX; x += pStepping) {
          moveBallAlongXAndWait(x, startX, startY, endX, endY);
        }
      } else {
        for (int x = startX; x > interceptorX; x -= pStepping) {
          moveBallAlongXAndWait(x, startX, startY, endX, endY);
        }
      }
    } else {
      if (startY < endY) {
        for (int y = startY; y < interceptorY; y += pStepping) {
          moveBallAlongYAndWait(y, startX, startY, endX, endY);
        }
      } else {
        for (int y = startY; y > interceptorY; y -= pStepping) {
          moveBallAlongYAndWait(y, startX, startY, endX, endY);
        }
      }
    }

    removeThrownBall();

  }

  private void removeThrownBall() {
    if (fLastBallBounds != null) {
      clear(fLastBallBounds.x, fLastBallBounds.y, fLastBallBounds.width, fLastBallBounds.height);
    }
  }

  private void moveBallAlongXAndWait(int pX, int pStartX, int pStartY, int
pEndX, int pEndY) {
   int y = pStartY + (int) (((double) (pEndY - pStartY) / (double) (pEndX
- pStartX)) * (pX - pStartX));  // y - y1 = (y2 - y1) / (x2 - x1) * (x -
x1)
   double scale = Math.sin((Math.PI / 6) + ((((double) (pX - pStartX) /
(double) (pEndX - pStartX))) * 2 * Math.PI / 3));
   removeThrownBall();
   drawScaledBallIcon(pX, y, scale);
   getClient().getUserInterface().getFieldComponent().refresh();
   synchronized (this) {
     try {
       wait(20);
     } catch (InterruptedException ie) {
     }
   }
 }

  private void moveBallAlongYAndWait(int pY, int pStartX, int pStartY, int
pEndX, int pEndY) {
   int x = pStartX + (int) (((double) (pEndX - pStartX) / (double) (pEndY
- pStartY)) * (pY - pStartY));  // x - x1 = (x2 - x1) / (y2 - y1) * (y -
y1)
   double scale = Math.sin((Math.PI / 6) + ((((double) (pY - pStartY) /
(double) (pEndY - pStartY))) * 2 * Math.PI / 3));
   removeThrownBall();
   drawScaledBallIcon(x, pY, scale);
   getClient().getUserInterface().getFieldComponent().refresh();
   synchronized (this) {
     try {
       wait(10);
     } catch (InterruptedException ie) {
     }
   }
 }

  private void drawScaledBallIcon(int pCenterX, int pCenterY, double scale) {
    BufferedImage ballIcon = getClient().getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL);
    BufferedImage scaledBallIcon = new BufferedImage(ballIcon.getWidth(), ballIcon.getHeight(),
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = scaledBallIcon.createGraphics();
    AffineTransform transformation = AffineTransform.getScaleInstance(scale, scale);
    g2d.drawRenderedImage(ballIcon, transformation);
    g2d.dispose();
    int x = pCenterX - (int) ((double) scaledBallIcon.getWidth() * scale / 2);
    int y = pCenterY - (int) ((double) scaledBallIcon.getHeight() * scale / 2);
    draw(scaledBallIcon, x, y);
    fLastBallBounds = new Rectangle(x, y, scaledBallIcon.getWidth(), scaledBallIcon.getHeight());
  }

  private void drawRulerModifier(Graphics2D pG2d, int pTotalLength, int pPreviousSegmentLength, int pMinimumRoll) {
    String numberString;
    if (pMinimumRoll < 6) {
      numberString = new StringBuffer().append(pMinimumRoll).append("+").toString();
    } else {
      numberString = "6";
    }
    FontMetrics metrics = pG2d.getFontMetrics();
    Rectangle2D numberBounds = metrics.getStringBounds(numberString, pG2d);
    int segmentLength = pTotalLength - pPreviousSegmentLength;
    if (numberBounds.getWidth() < segmentLength) {
      int baselineX = pPreviousSegmentLength + (segmentLength - (int) numberBounds.getWidth()) / 2;
      int baselineY = ((int) (numberBounds.getHeight() / 4)) + 2;
      pG2d.drawString(numberString, baselineX, baselineY);
    }
  }

  private int findMinimumRoll(PassingDistance pPassingDistance) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Set<PassModifier> passModifiers = UtilActingPlayer.findPassModifiers(game);
    return getClient().getDiceInterpreter().minimumRollPass(actingPlayer.getPlayer(), pPassingDistance, passModifiers);
  }

}

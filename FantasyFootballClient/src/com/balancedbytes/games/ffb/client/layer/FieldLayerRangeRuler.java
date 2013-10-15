package com.balancedbytes.games.ffb.client.layer;

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
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPassing;


/**
 * 
 * @author Kalimar
 */
public class FieldLayerRangeRuler extends FieldLayer {
  
  private static final Map<PassingDistance, Color> _COLOR_BY_PASSING_DISTANCE = new HashMap<PassingDistance, Color>();
  
  static {
    _COLOR_BY_PASSING_DISTANCE.put(PassingDistance.QUICK_PASS, new Color(0.0f, 1.0f, 0.0f, 0.3f));
    _COLOR_BY_PASSING_DISTANCE.put(PassingDistance.SHORT_PASS, new Color(1.0f, 1.0f, 0.0f, 0.3f));
    _COLOR_BY_PASSING_DISTANCE.put(PassingDistance.LONG_PASS, new Color(1.0f, 0.0f, 0.0f, 0.3f));
    _COLOR_BY_PASSING_DISTANCE.put(PassingDistance.LONG_BOMB, new Color(0.0f, 0.0f, 0.0f, 0.3f));
  }
  
  public static final Color COLOR_INTERCEPTION = new Color(1.0f, 1.0f, 1.0f, 0.3f);
  public static final Color COLOR_THROWABLE_PLAYER = new Color(1.0f, 1.0f, 1.0f, 0.3f);
  public static final Color COLOR_SELECT_SQUARE = new Color(0.0f, 0.0f, 1.0f, 0.2f);

  private Polygon fPolygonComplete;
  
  private FieldCoordinate fSelectSquareCoordinate;
  private FieldCoordinate[] fMarkedCoordinates;
    
  public FieldLayerRangeRuler(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void drawRangeRuler(RangeRuler pRangeRuler) {
    
    removeRangeRuler();
    
    if ((pRangeRuler != null) && FieldCoordinateBounds.FIELD.isInBounds(pRangeRuler.getTargetCoordinate()) && StringTool.isProvided(pRangeRuler.getThrowerId())) {

      Game game = getClient().getGame();
      Player thrower = game.getPlayerById(pRangeRuler.getThrowerId());
      FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
      
      PassingDistance passingDistance = UtilPassing.findPassingDistance(game, throwerCoordinate, pRangeRuler.getTargetCoordinate(), false);
      if (passingDistance != null) {
      
        Point startCenter = new Point(
          (throwerCoordinate.getX() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2), 
          (throwerCoordinate.getY() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2) 
        );
        Point endCenter = new Point(
          (pRangeRuler.getTargetCoordinate().getX() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2),
          (pRangeRuler.getTargetCoordinate().getY() * FIELD_SQUARE_SIZE) + (FIELD_SQUARE_SIZE / 2) 
        );
        
        int lengthY = startCenter.y - endCenter.y;
        int lengthX = endCenter.x - startCenter.x;
        double length = Math.sqrt((lengthY * lengthY) + (lengthX * lengthX));
        
        double sinPhi = lengthY / length;
        double cosPhi = lengthX / length;

        fPolygonComplete = findPolygon(startCenter, 0, (int) length, sinPhi, cosPhi);

        if (fPolygonComplete != null) {
        
          Graphics2D g2d = getImage().createGraphics();
          g2d.setPaint(_COLOR_BY_PASSING_DISTANCE.get(passingDistance));
          g2d.fillPolygon(fPolygonComplete);
      
          if (pRangeRuler.getMinimumRoll() > 0) {
          
            //    [   cos(theta)    -sin(theta)    tx   ]
            //    [   sin(theta)     cos(theta)    ty   ]
            //    [       0              0          1   ]
            
            //  [  m00  m01  m02  ]
            //  [  m10  m11  m12  ]
            //  [   0    0    1   ]
            
            g2d.transform(new AffineTransform(cosPhi, -sinPhi, sinPhi, cosPhi, startCenter.x, startCenter.y));
            g2d.setFont(new Font("Sans Serif", Font.BOLD, 32));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
      
            drawRulerModifier(g2d, (int) length, 0, pRangeRuler.getMinimumRoll());
            
          }
        
          g2d.dispose();
          
          Rectangle bounds = fPolygonComplete.getBounds();
          addUpdatedArea(bounds);

        }

        Color selectSquareColor = null; 
        Team otherTeam = game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
        Player catcher = game.getFieldModel().getPlayer(pRangeRuler.getTargetCoordinate());
        if ((catcher == null) || otherTeam.hasPlayer(catcher)) {
          selectSquareColor = COLOR_SELECT_SQUARE;
        } else {
          selectSquareColor = _COLOR_BY_PASSING_DISTANCE.get(passingDistance);
        }

        if (selectSquareColor != null) {
          fSelectSquareCoordinate = pRangeRuler.getTargetCoordinate();
          clear(fSelectSquareCoordinate, true);
          drawSelectSquare(fSelectSquareCoordinate, selectSquareColor);
        }

        if (!pRangeRuler.isThrowTeamMate()) {
          markPlayers(UtilPassing.findInterceptors(game, thrower, fSelectSquareCoordinate), COLOR_INTERCEPTION);
        }
        
      }

    }
    
  }
  
  public void removeRangeRuler() {
    if (fPolygonComplete != null) {
      Rectangle oldBounds = fPolygonComplete.getBounds();
      clear(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height, true);
    }
    fPolygonComplete = null;
    if (fMarkedCoordinates != null) {
      for (FieldCoordinate playerCoordinate : fMarkedCoordinates) {
        clear(playerCoordinate, true);
      }
      fMarkedCoordinates = null;
    }
    if (fSelectSquareCoordinate != null) {
      clear(fSelectSquareCoordinate, true);
    }
    fSelectSquareCoordinate = null;
  }
  
  public boolean isRulerShown() {
    return (fPolygonComplete != null);
  }
  
  public boolean testCoordinateInsideRangeRuler(FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      int x = pCoordinate.getX() * FIELD_SQUARE_SIZE;
      int y = pCoordinate.getY() * FIELD_SQUARE_SIZE;
      Rectangle playerSquare = new Rectangle(x, y, FIELD_SQUARE_SIZE, FIELD_SQUARE_SIZE);
      if ((fPolygonComplete != null) && fPolygonComplete.intersects(playerSquare)) {
        return true;
      }
    }
    return false;
  }
  
  private Polygon findPolygon(
    Point pStartCenter,
    int pMinLength,
    int pMaxLength,
    double pSinPhi,
    double pCosPhi
  ) {
    
    if (pMaxLength > pMinLength) {
   
      int halfRulerWidth = (int) (FIELD_SQUARE_SIZE * UtilPassing.RULER_WIDTH / 2);
      Point point1 = new Point(pStartCenter.x + pMinLength, pStartCenter.y - halfRulerWidth);
      point1 = rotate(point1, pStartCenter, pSinPhi, pCosPhi);
      Point point2 = new Point(pStartCenter.x + pMinLength, pStartCenter.y + halfRulerWidth);
      point2 = rotate(point2, pStartCenter, pSinPhi, pCosPhi);
      Point point3 = new Point(pStartCenter.x + pMaxLength, pStartCenter.y + halfRulerWidth);
      point3 = rotate(point3, pStartCenter, pSinPhi, pCosPhi);
      Point point4 = new Point(pStartCenter.x + pMaxLength, pStartCenter.y - halfRulerWidth);
      point4 = rotate(point4, pStartCenter, pSinPhi, pCosPhi);

      return new Polygon(
        new int[] { point1.x, point2.x, point3.x, point4.x },
        new int[] { point1.y, point2.y, point3.y, point4.y },
        4
      );
      
    } else {
      return null;
    }
    
  }
  
  private Point rotate(Point pPoint, Point pCenter, double pSinPhi, double pCosPhi) {
    int x = pPoint.x - pCenter.x;
    int y = pPoint.y - pCenter.y;
    return new Point(
      (int) ((pCosPhi * x) + (pSinPhi * y) + pCenter.getX()),
      (int) ((-pSinPhi * x) + (pCosPhi * y) + pCenter.getY())
    );
  }
 
  private void drawSelectSquare(FieldCoordinate pCoordinate, Color pColor) {
    if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
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

  public void markCoordinates(FieldCoordinate[] pMarkedCoordinates, Color pColor) {
    fMarkedCoordinates = pMarkedCoordinates;
    if (ArrayTool.isProvided(pMarkedCoordinates) && (pColor != null)) {
      for (int i = 0; i < pMarkedCoordinates.length; i++) {
        clear(fMarkedCoordinates[i], true);
        drawSelectSquare(fMarkedCoordinates[i], pColor);
      }
    }
  }

  public void markPlayers(Player[] pMarkedPlayers, Color pColor) {
    Game game = getClient().getGame();
    if (ArrayTool.isProvided(pMarkedPlayers) && (pColor != null)) {
      fMarkedCoordinates = new FieldCoordinate[pMarkedPlayers.length];
      for (int i = 0; i < pMarkedPlayers.length; i++) {
        fMarkedCoordinates[i] = game.getFieldModel().getPlayerCoordinate(pMarkedPlayers[i]);
        clear(fMarkedCoordinates[i], true);
        drawSelectSquare(fMarkedCoordinates[i], pColor);
      }
    }
  }

  public void clearMarkedCoordinates() {
    if (ArrayTool.isProvided(fMarkedCoordinates)) {
      for (int i = 0; i < fMarkedCoordinates.length; i++) {
        clear(fMarkedCoordinates[i], true);
      }
    }
  }
  
  private void drawRulerModifier(Graphics2D pG2d, int pTotalLength, int pPreviousSegmentLength, int pMinimumRoll) {
    String numberString;
    if (pMinimumRoll < 6) {
      numberString = new StringBuilder().append(pMinimumRoll).append("+").toString();
    } else {
      numberString = "6";
    }
    FontMetrics metrics = pG2d.getFontMetrics();
    Rectangle2D numberBounds = metrics.getStringBounds(numberString, pG2d);
    int segmentLength = pTotalLength - pPreviousSegmentLength;
    if (numberBounds.getWidth() < segmentLength) {
      int baselineX = pPreviousSegmentLength + (segmentLength - (int) numberBounds.getWidth()) / 2 ;
      int baselineY = ((int) (numberBounds.getHeight() / 4)) + 2;
      pG2d.drawString(numberString, baselineX, baselineY);
    }
  }
  
  public void init() {
    clear(true);
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    if (fieldModel != null) {
      drawRangeRuler(fieldModel.getRangeRuler());
    }
  }
  
  public static Color getColorForPassingDistance(PassingDistance pPassingDistance) {
    return _COLOR_BY_PASSING_DISTANCE.get(pPassingDistance);
  }
    
}

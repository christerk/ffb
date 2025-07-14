/**
 * FieldLayerTackleZones
 *
 * Renders the colored tackle zone overlays for both teams on the field.
 * Responds to user toggles for per-team visibility, overlapping, contours, and "opposing only" logic.
 * 
 * The rendering respects user settings set in the menu (see CommonProperty and IClientPropertyValue),
 * and draws translucent rectangles and (optionally) dashed contours to show tackle zone coverage.
 * 
 * This layer is integrated in FieldComponent as a part of the field rendering stack.
 *
 * @author Garcangel
 */

package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.Stroke;


public class FieldLayerTackleZones extends FieldLayer {

  public FieldLayerTackleZones(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, 
                               PitchDimensionProvider pitchDimensionProvider, FontCache fontCache) {
    super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
  }

  @Override
  public void initLayout() {
    super.initLayout();
  }
  private static final float[] TZ_DASH = { 6f, 4f };

  @Override
  public void init() {
    updateTackleZones();
  }

  public void updateTackleZones() {
    clear(true);

    if (!isHomeTzEnabled() && !isAwayTzEnabled()) {
      addUpdatedArea(new Rectangle(0, 0, getImage().getWidth(), getImage().getHeight()));
      return;
    }

    // 1. Tally tackle zones per tile (by team)
    int[][] homeMap, awayMap;
    int pitchWidth = FieldCoordinate.FIELD_WIDTH;
    int pitchHeight = FieldCoordinate.FIELD_HEIGHT;
    homeMap = new int[pitchWidth][pitchHeight];
    awayMap = new int[pitchWidth][pitchHeight];

    countTackleZones(homeMap, awayMap, pitchWidth, pitchHeight);

    // 2. Draw the tiles with blending/overlap
    drawTackleZoneTiles(homeMap, awayMap, pitchWidth, pitchHeight);

    addUpdatedArea(new Rectangle(0, 0, getImage().getWidth(), getImage().getHeight()));
  }

  // Tallies up all tackle zone influences into two team maps.
  private void countTackleZones(int[][] homeMap, int[][] awayMap, int pitchWidth, int pitchHeight) {
    Game game = getClient().getGame();
    List<Player<?>> playerList = Arrays.asList(game.getPlayers());

    boolean showBothTz = showBothTz(game.getTurnMode());
    boolean homeTz = isHomeTzEnabled();
    boolean awayTz = isAwayTzEnabled();
    boolean isHomeActive = game.isHomePlaying();
    boolean opposingTz = isOpposingTzEnabled();

    for (Player<?> player : playerList) {
      PlayerState playerState = game.getFieldModel().getPlayerState(player);
      if (!playerState.hasTacklezones()) continue;

      FieldCoordinate coord = game.getFieldModel().getPlayerCoordinate(player);
      if (coord == null || coord.isBoxCoordinate()) continue;

      boolean isHomePlayer = player.getTeam() == game.getTeamHome();
      
      // Opponent only logic
      if (opposingTz && !showBothTz && (isHomePlayer == isHomeActive)) {
        continue;
      }

      // Per-team toggles
      if (!opposingTz && ((isHomePlayer && !homeTz) || (!isHomePlayer && !awayTz))) {
        continue;
      }
      int px = coord.getX();
      int py = coord.getY();

      for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
          int tx = px + dx, ty = py + dy;
          if (tx < 0 || ty < 0 || tx >= pitchWidth || ty >= pitchHeight) continue;
          if (isHomePlayer) {
            homeMap[tx][ty]++;
        } else {
            awayMap[tx][ty]++;
        }
        }
      }
    }
  }

  // Paints tackle zone overlays for the entire pitch with.
  private void drawTackleZoneTiles(int[][] homeMap, int[][] awayMap, int pitchWidth, int pitchHeight) {
    Graphics2D g2d    = getImage().createGraphics();
    float      baseA  = 0.15f;
    int        cap    = isNoOverlapEnabled() ? 1 : 3; // caps number of tz drawn on a single tile
    int        unscaled = pitchDimensionProvider.unscaledFieldSquare();
    boolean    portrait = pitchDimensionProvider.isPitchPortrait();

    String swapSetting = getClient().getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
    boolean swapColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapSetting);

    Color homeColor   = swapColors ? new Color(0,0,255,255) : new Color(255,0,0,255);
    Color awayColor   = swapColors ? new Color(255,0,0,255) : new Color(0,0,255,255);

    // Precompute exact pixel origins for every column and 
    // row to handle different column/row sizes.
    // 1) Build origin arrays, swapping dimensions in portrait
    int cols = portrait ? pitchHeight : pitchWidth;
    int[] originX = new int[cols + 1];
    for (int i = 0; i <= cols; i++) {
      originX[i] = pitchDimensionProvider.scale(i * unscaled);
    }

    int rows = portrait ? pitchWidth : pitchHeight;
    int[] originY = new int[rows + 1];
    for (int j = 0; j <= rows; j++) {
      originY[j] = pitchDimensionProvider.scale(j * unscaled);
    }

    // 2) Draw each tile
    for (int x = 0; x < pitchWidth; x++) {
      for (int y = 0; y < pitchHeight; y++) {
        int hO = homeMap[x][y], aO = awayMap[x][y];
        if (hO == 0 && aO == 0) continue;

        int col = portrait ? y               : x;
        int row = portrait ? (pitchWidth-1-x) : y;

        int px = originX[col];
        int py = originY[row];
        int w  = originX[col+1] - px;
        int h  = originY[row+1] - py;

        if (hO > 0 && aO > 0) {
          paintZoneRect(g2d, px, py, w, h, homeColor, (baseA * Math.min(hO, cap)) / 2f);
          paintZoneRect(g2d, px, py, w, h, awayColor, (baseA * Math.min(aO, cap)) / 2f);
        } else if (hO > 0) {
          paintZoneRect(g2d, px, py, w, h, homeColor, baseA * Math.min(hO, cap));
        } else {
          paintZoneRect(g2d, px, py, w, h, awayColor, baseA * Math.min(aO, cap));
        }
      }
    }

    if (isContourEnabled()) {
      // draw contours
      List<int[]> homeEdges = getPerimeterEdges(homeMap, pitchWidth, pitchHeight);
      List<int[]> awayEdges = getPerimeterEdges(awayMap, pitchWidth, pitchHeight);
      drawContour(g2d, homeEdges, pitchWidth, portrait, originX, originY, TZ_DASH, homeColor);
      drawContour(g2d, awayEdges, pitchWidth, portrait, originX, originY, TZ_DASH, awayColor);
    }

    g2d.dispose();
  }

  // Paints a single colored translucent zone square.
  private void paintZoneRect(Graphics2D g2d, int x, int y, int w, int h, Color color, float alpha) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    g2d.setColor(color);
    g2d.fillRect(x, y, w, h);
  }

  // Scans the map array and returns a list of edges on the perimeter.
  // Each entry is int[]{ x1, y1, x2, y2 } in tile coordinates.
  // For each occupied tile, checks its four sides. If a neighboring tile is empty or out of bounds,
  // adds that side as a perimeter edge (as a line from one corner to the next).
  // Returns all perimeter edges as a list of [x1, y1, x2, y2] tile coordinates.
  private List<int[]> getPerimeterEdges(int[][] map, int width, int height) {
    List<int[]> edges = new ArrayList<>();
    int[][] sides = {
      { 0,-1, 0,0, 1,0},
      { 1, 0, 1,0, 1,1},
      { 0, 1, 1,1, 0,1},
      {-1, 0, 0,1, 0,0},
    };
    for (int x=0; x<width; x++) for (int y=0; y<height; y++) {
      if (map[x][y]==0) continue;
      for (int[] s : sides) {
        int nx=x+s[0], ny=y+s[1];
        if (nx<0||nx>=width||ny<0||ny>=height||map[nx][ny]==0) {
          edges.add(new int[]{ x+s[2], y+s[3], x+s[4], y+s[5] });
        }
      }
    }
    return edges;
  }

  //Draws a dashed contour for the given edges list.
  //Reuses the same originX/originY arrays from drawTackleZoneTiles.
  private void drawContour(Graphics2D g2d, List<int[]> edges, int pitchWidth, boolean portrait,
                            int[] originX, int[] originY, float[] dash, Color color) {
    Stroke old = g2d.getStroke();
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    g2d.setColor(color);
    g2d.setStroke(new BasicStroke(
      1f,
      BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_MITER,
      10f,
      dash,
      0f
    ));
    for (int[] e : edges) {
      int x1=e[0], y1=e[1], x2=e[2], y2=e[3];
      int col1 = portrait ? y1               : x1;
      int row1 = portrait ? (pitchWidth - x1) : y1;
      int col2 = portrait ? y2               : x2;
      int row2 = portrait ? (pitchWidth - x2) : y2;
      int px1 = originX[col1], py1 = originY[row1];
      int px2 = originX[col2], py2 = originY[row2];
      g2d.drawLine(px1,py1,px2,py2);

    }
    g2d.setStroke(old);
  }

  private boolean isHomeTzEnabled() {
    return IClientPropertyValue.SETTING_TACKLEZONES_HOME_ON.equals(
      getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_HOME)
    );
  }

  private boolean isAwayTzEnabled() {
    return IClientPropertyValue.SETTING_TACKLEZONES_AWAY_ON.equals(
      getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_AWAY)
    );
  }

  private boolean isOpposingTzEnabled() {
    return IClientPropertyValue.SETTING_TACKLEZONES_OPPOSING_ON.equals(
      getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_OPPOSING)
    );
  }

  private boolean isNoOverlapEnabled() {
    return IClientPropertyValue.SETTING_TACKLEZONES_NO_OVERLAP_ON.equals(
      getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_NO_OVERLAP)
    );
  }

  private boolean isContourEnabled() {
    return IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_ON.equals(
      getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_CONTOUR)
    );
  }

  public static boolean showBothTz(TurnMode turnMode) {
    return turnMode == TurnMode.SETUP ||
           turnMode == TurnMode.KICKOFF ||
           turnMode == TurnMode.PERFECT_DEFENCE ||
           turnMode == TurnMode.SOLID_DEFENCE ||
           turnMode == TurnMode.KICKOFF_RETURN; 
  }

 
}

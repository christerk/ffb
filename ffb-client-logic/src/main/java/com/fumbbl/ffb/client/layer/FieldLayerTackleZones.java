/**
 * FieldLayerTackleZones
 * <p>
 * Renders the colored tackle zone overlays for both teams on the field.
 * Responds to user toggles for per-team visibility, overlapping, contours, and "opposing only" logic.
 * <p>
 * The rendering respects user settings set in the menu (see CommonProperty and IClientPropertyValue),
 * and draws translucent rectangles and (optionally) dashed contours to show tackle zone coverage.
 * <p>
 * This layer is integrated in FieldComponent as a part of the field rendering stack.
 *
 * @author Garcangel
 */

package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FieldLayerTackleZones extends FieldLayer {

	private static final float ALPHA = 0.1f;
	private static final float[] TZ_DASH = {6f, 4f};

	private final StyleProvider styleProvider;

	public FieldLayerTackleZones(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider,
															 PitchDimensionProvider pitchDimensionProvider, FontCache fontCache, StyleProvider styleProvider) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
		this.styleProvider = styleProvider;
	}

	@Override
	public void initLayout() {
		super.initLayout();
	}

	@Override
	public void init() {
		updateTackleZones();
	}

	public void updateTackleZones() {
		clear(true);

		if (isTzDisabled()) {
			return;
		}


		String setting = getCurrentTacklezoneSetting();

		Game game = getClient().getGame();
		boolean isHomeActive = game.isHomePlaying();

		boolean showPassiveOnly = IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE.equals(setting);

		Graphics2D g2d = getImage().createGraphics();

		if (showPassiveOnly) {
			if (isHomeActive) {
				processPlayers(g2d, game.getTeamAway(), styleProvider.getAway());
			} else {
				processPlayers(g2d, game.getTeamHome(), styleProvider.getHome());
			}
		} else {
			boolean showHome = IClientPropertyValue.SETTING_TACKLEZONES_HOME.equals(setting)
				|| IClientPropertyValue.SETTING_TACKLEZONES_BOTH.equals(setting);
			boolean showAway = IClientPropertyValue.SETTING_TACKLEZONES_AWAY.equals(setting)
				|| IClientPropertyValue.SETTING_TACKLEZONES_BOTH.equals(setting);

			if (showAway) {
				processPlayers(g2d, game.getTeamAway(), styleProvider.getAway());
			}

			if (showHome) {
				processPlayers(g2d, game.getTeamHome(), styleProvider.getHome());
			}
		}

		g2d.dispose();
	}

	private void processPlayers(Graphics2D g2d, Team team, Color color) {
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();

		boolean overlapEnabled = isOverlapEnabled();

		Set<FieldCoordinate> processedCoords = new HashSet<>();

		for (Player<?> player : team.getPlayers()) {

			FieldCoordinate coord = fieldModel.getPlayerCoordinate(player);

			if (coord == null) {
				continue;
			}

			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (!playerState.hasTacklezones()) {
				continue;
			}

			FieldCoordinate[] tackleZones = fieldModel.findAdjacentCoordinates(coord, FieldCoordinateBounds.FIELD, 1, false);

			for (FieldCoordinate tackleZone : tackleZones) {
				if (overlapEnabled || processedCoords.add(tackleZone)) {
					drawTackleZone(g2d, tackleZone, color);
				}
			}
		}
	}

	private void drawTackleZone(Graphics2D g2d, FieldCoordinate coordinate, Color color) {
		Dimension origin = pitchDimensionProvider.mapToLocal(coordinate);
		paintZoneRect(g2d, origin.width, origin.height, color, ALPHA);
	}


	// Paints tackle zone overlays for the entire pitch with.
/*	private void drawTackleZoneTiles(int[][] homeMap, int[][] awayMap, int pitchWidth, int pitchHeight) {
		Graphics2D g2d = getImage().createGraphics();
		float baseA = 0.15f;
		int cap = isOverlapEnabled() ? 3 : 1; // caps number of tz drawn on a single tile
		int unscaled = pitchDimensionProvider.unscaledFieldSquare();
		boolean portrait = pitchDimensionProvider.isPitchPortrait();

		String swapSetting = getClient().getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
		boolean swapColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapSetting);

		Color homeColor = swapColors ? new Color(0, 0, 255, 255) : new Color(255, 0, 0, 255);
		Color awayColor = swapColors ? new Color(255, 0, 0, 255) : new Color(0, 0, 255, 255);

		// Precompute exact pixel origins for every column and
		// row to handle different column/row sizes.
		// 1) Build origin arrays, swapping dimensions in portrait mode
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

				int col = portrait ? y : x;
				int row = portrait ? (pitchWidth - 1 - x) : y;

				int px = originX[col];
				int py = originY[row];
				int w = originX[col + 1] - px;
				int h = originY[row + 1] - py;

				float homeVal = Math.min(hO, cap);// Cap home tackle zones
				float awayVal = Math.min(aO, cap);// Cap away tackle zones

				if (homeVal > 0 && awayVal > 0) {
					float totalVal = homeVal + awayVal;
					float maxVal = Math.max(homeVal, awayVal);// Use the larger count for max intensity
					float totalIntensity = baseA * maxVal;    // Set total intensity by the largest stack
					// Split intensity proportionally between teams
					float homeAlpha = totalIntensity * (homeVal / totalVal);
					float awayAlpha = totalIntensity * (awayVal / totalVal);
					paintZoneRect(g2d, px, py, w, h, homeColor, homeAlpha);
					paintZoneRect(g2d, px, py, w, h, awayColor, awayAlpha);
				} else if (homeVal > 0) {
					// Only home team present
					paintZoneRect(g2d, px, py, w, h, homeColor, baseA * homeVal);
				} else if (awayVal > 0) {
					// Only away team present
					paintZoneRect(g2d, px, py, w, h, awayColor, baseA * awayVal);
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
	}*/

	// Paints a single colored translucent zone square.
	private void paintZoneRect(Graphics2D g2d, int x, int y, Color color, float alpha) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.setColor(color);
		g2d.fillRect(x, y, pitchDimensionProvider.fieldSquareSize(), pitchDimensionProvider.fieldSquareSize());
	}

	// Scans the map array and returns a list of edges on the perimeter.
	// Each entry is int[]{ x1, y1, x2, y2 } in tile coordinates.
	// For each occupied tile, checks its four sides. If a neighboring tile is empty or out of bounds,
	// adds that side as a perimeter edge (as a line from one corner to the next).
	// Returns all perimeter edges as a list of [x1, y1, x2, y2] tile coordinates.
	private List<int[]> getPerimeterEdges(int[][] map, int width, int height) {
		List<int[]> edges = new ArrayList<>();
		int[][] sides = {
			{0, -1, 0, 0, 1, 0},
			{1, 0, 1, 0, 1, 1},
			{0, 1, 1, 1, 0, 1},
			{-1, 0, 0, 1, 0, 0},
		};
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				if (map[x][y] == 0) continue;
				for (int[] s : sides) {
					int nx = x + s[0], ny = y + s[1];
					if (nx < 0 || nx >= width || ny < 0 || ny >= height || map[nx][ny] == 0) {
						edges.add(new int[]{x + s[2], y + s[3], x + s[4], y + s[5]});
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
			int x1 = e[0], y1 = e[1], x2 = e[2], y2 = e[3];
			int col1 = portrait ? y1 : x1;
			int row1 = portrait ? (pitchWidth - x1) : y1;
			int col2 = portrait ? y2 : x2;
			int row2 = portrait ? (pitchWidth - x2) : y2;
			int px1 = originX[col1], py1 = originY[row1];
			int px2 = originX[col2], py2 = originY[row2];
			g2d.drawLine(px1, py1, px2, py2);

		}
		g2d.setStroke(old);
	}

	private String getCurrentTacklezoneSetting() {
		ClientMode mode = getClient().getMode();
		if (mode == ClientMode.PLAYER) {
			return getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE);
		} else {
			return getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE);
		}
	}

	private boolean isTzDisabled() {
		return IClientPropertyValue.SETTING_TACKLEZONES_NONE.equals(getCurrentTacklezoneSetting());
	}

	private boolean isOverlapEnabled() {
		return IClientPropertyValue.SETTING_TACKLEZONES_OVERLAP_ON.equals(
			getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_OVERLAP)
		);
	}

	private boolean isContourEnabled() {
		return IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_ON.equals(
			getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_CONTOUR)
		);
	}

}

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
import com.fumbbl.ffb.Direction;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class FieldLayerTackleZones extends FieldLayer {

	private static final float ALPHA = 0.1f;
	private static final float[] TZ_DASH = {6f, 4f};
	private static final BasicStroke STROKE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, TZ_DASH, 0f);

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

		clear(true);

		if (isTzDisabled()) {
			return;
		}

		String setting = getCurrentTackleZoneSetting();
		Game game = getClient().getGame();
		boolean isHomeActive = game.isHomePlaying();

		boolean showPassiveOnly = IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE.equals(setting);
		boolean showPassiveBothOnSetup = IClientPropertyValue.SETTING_TACKLEZONES_PASSIVE_BOTH_ON_SETUP.equals(setting);

		Graphics2D g2d = getImage().createGraphics();
		g2d.setStroke(STROKE);

		if (showPassiveOnly || showPassiveBothOnSetup) {
			// Handle both passive modes
			if (showPassiveBothOnSetup && isSetupPhase(game.getTurnMode())) {
				// Special case: Show both teams during the setup phases
				processPlayers(g2d, game.getTeamHome(), styleProvider.getHome());
				processPlayers(g2d, game.getTeamAway(), styleProvider.getAway());
			} else {
				// Common case for both passive modes: Show only non-active team
				if (isHomeActive) {
					processPlayers(g2d, game.getTeamAway(), styleProvider.getAway());
				} else {
					processPlayers(g2d, game.getTeamHome(), styleProvider.getHome());
				}
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

		g2d.setColor(color);

		boolean overlapEnabled = isOverlapEnabled();

		Set<FieldCoordinate> processedCoords = new HashSet<>();

		for (Player<?> player : team.getPlayers()) {

			FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(player);

			if (coordinate == null) {
				continue;
			}

			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (!playerState.hasTacklezones()) {
				continue;
			}

			FieldCoordinate[] tackleZones = fieldModel.findAdjacentCoordinates(coordinate, FieldCoordinateBounds.FIELD, 1, true);

			for (FieldCoordinate tackleZone : tackleZones) {
				if (processedCoords.add(tackleZone) || overlapEnabled ) {
					drawTackleZone(g2d, tackleZone);
				}
			}
		}
		if (isContourEnabled()) {
			for (FieldCoordinate processedCoord : processedCoords) {
				drawContours(g2d, processedCoord, processedCoords);
			}
		}
	}

	private void drawContours(Graphics2D g2d, FieldCoordinate coordinate, Set<FieldCoordinate> processedCoords) {
		List<Direction> directions = borderDirections(coordinate, processedCoords);
		Dimension origin = pitchDimensionProvider.mapToLocal(coordinate);
		for (Direction direction : directions) {
			drawContour(g2d, contourOrigin(origin, direction), contourTarget(origin, direction));
		}
	}

	private List<Direction> borderDirections(FieldCoordinate coordinate, Set<FieldCoordinate> processedCoords) {
		List<Direction> directions = new java.util.ArrayList<>();
		if (!processedCoords.contains(new FieldCoordinate(coordinate.getX(), coordinate.getY() - 1))) {
			directions.add(Direction.NORTH);
		}
		if (!processedCoords.contains(new FieldCoordinate(coordinate.getX(), coordinate.getY() + 1))) {
			directions.add(Direction.SOUTH);
		}
		if (!processedCoords.contains(new FieldCoordinate(coordinate.getX() - 1, coordinate.getY()))) {
			directions.add(Direction.WEST);
		}
		if (!processedCoords.contains(new FieldCoordinate(coordinate.getX() + 1, coordinate.getY()))) {
			directions.add(Direction.EAST);
		}
		return directions.stream().map(pitchDimensionProvider::mapToLocal).collect(Collectors.toList());
	}

	private void drawTackleZone(Graphics2D g2d, FieldCoordinate coordinate) {
		Dimension origin = pitchDimensionProvider.mapToLocal(coordinate);
		paintZoneRect(g2d, origin.width, origin.height);
	}

	private Dimension contourOrigin(Dimension upperLeft, Direction fromPlayer) {
		switch (fromPlayer) {
			case SOUTH:
				return new Dimension(upperLeft.width, upperLeft.height + pitchDimensionProvider.fieldSquareSize());

			case EAST:
				return new Dimension(upperLeft.width + pitchDimensionProvider.fieldSquareSize(), upperLeft.height);

			default:
				return upperLeft;
		}
	}

	private Dimension contourTarget(Dimension upperLeft, Direction fromPlayer) {
		switch (fromPlayer) {
			case NORTH:
				return new Dimension(upperLeft.width + pitchDimensionProvider.fieldSquareSize(), upperLeft.height);

			case WEST:
				return new Dimension(upperLeft.width, upperLeft.height + pitchDimensionProvider.fieldSquareSize());

			case EAST:
			case SOUTH:
				return new Dimension(upperLeft.width + pitchDimensionProvider.fieldSquareSize(), upperLeft.height + pitchDimensionProvider.fieldSquareSize());
			default:
				return upperLeft;
		}
	}

	// Paints a single colored translucent zone square.
	private void paintZoneRect(Graphics2D g2d, int x, int y) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));
		g2d.fillRect(x, y, pitchDimensionProvider.fieldSquareSize(), pitchDimensionProvider.fieldSquareSize());
	}

	//Draws a dashed contour for the given edges list.
	//Reuses the same originX/originY arrays from drawTackleZoneTiles.
	private void drawContour(Graphics2D g2d, Dimension from, Dimension to) {

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.drawLine(from.width, from.height, to.width, to.height);

	}

	private String getCurrentTackleZoneSetting() {
		ClientMode mode = getClient().getMode();
		if (mode == ClientMode.PLAYER) {
			return getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE);
		} else {
			return getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE);
		}
	}

	private boolean isTzDisabled() {
		return IClientPropertyValue.SETTING_TACKLEZONES_NONE.equals(getCurrentTackleZoneSetting());
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

	private boolean isSetupPhase(TurnMode turnMode) {
		return turnMode == TurnMode.SETUP ||
			turnMode == TurnMode.KICKOFF ||
			turnMode == TurnMode.PERFECT_DEFENCE ||
			turnMode == TurnMode.SOLID_DEFENCE ||
			turnMode == TurnMode.KICKOFF_RETURN;
	}

}

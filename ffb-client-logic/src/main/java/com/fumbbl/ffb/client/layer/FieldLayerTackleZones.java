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
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FieldLayerTackleZones extends FieldLayer {

	private static final float ALPHA = 0.15f;

	private final StyleProvider styleProvider;

	public FieldLayerTackleZones(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider,
															 PitchDimensionProvider pitchDimensionProvider, PitchViewport pitchViewport,
															 FontCache fontCache, StyleProvider styleProvider) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, pitchViewport, fontCache);
		this.styleProvider = styleProvider;
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

		Graphics2D g2d = createWorldGraphics();
		float pixel = (float) (1 / pitchViewport.effectiveScale());
		g2d.setStroke(new BasicStroke(pixel, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
			10f, new float[]{6 * pixel, 4 * pixel}, 0f));

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
				processPlayers(g2d, game.getTeamAway(), styleProvider.getTackleZoneAway());
			}

			if (showHome) {
				processPlayers(g2d, game.getTeamHome(), styleProvider.getTackleZoneHome());
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
				if (processedCoords.add(tackleZone) || overlapEnabled) {
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
		Rectangle2D bounds = pitchViewport.worldSquare(coordinate);
		for (Direction direction : directions) {
			g2d.setComposite(AlphaComposite.SrcOver);
			switch (direction) {
				case NORTH:
					g2d.draw(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY()));
					break;
				case SOUTH:
					g2d.draw(new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY()));
					break;
				case WEST:
					g2d.draw(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY()));
					break;
				case EAST:
					g2d.draw(new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()));
					break;
				default:
					break;
			}
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
		return directions;
	}

	private void drawTackleZone(Graphics2D g2d, FieldCoordinate coordinate) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));
		fillWorldSquare(g2d, coordinate);
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
		return (
			!UtilGameOption.isOptionEnabled(getClient().getGame(), GameOptionId.ENABLE_TACKLEZONE_OVERLAYS)
				&& getClient().getMode() == ClientMode.PLAYER
		) || IClientPropertyValue.SETTING_TACKLEZONES_NONE.equals(getCurrentTackleZoneSetting());
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

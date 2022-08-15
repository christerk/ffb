package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerMarker;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerPlayers extends FieldLayer {

	public FieldLayerPlayers(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
	}

	public void refresh(FieldCoordinateBounds pBounds) {
		if (pBounds != null) {
			for (FieldCoordinate fieldCoordinate : pBounds.fieldCoordinates()) {
				updateBallAndPlayers(fieldCoordinate, false);
			}
		}
	}

	public void updateBallAndPlayers(FieldCoordinate pCoordinate, boolean pPlayerOverBall) {
		if ((pCoordinate != null) && !pCoordinate.isBoxCoordinate()) {
			Game game = getClient().getGame();
			Dimension dimension = dimensionProvider.map(pCoordinate, true);
			int x = dimension.width - (PlayerIconFactory.MAX_ICON_WIDTH / 2);
			int y = dimension.height - (PlayerIconFactory.MAX_ICON_HEIGHT / 2);
			clear(x, y, PlayerIconFactory.MAX_ICON_WIDTH, PlayerIconFactory.MAX_ICON_HEIGHT, true); // also adds updated area
			Graphics2D g2d = getImage().createGraphics();
			g2d.setClip(x, y, PlayerIconFactory.MAX_ICON_WIDTH, PlayerIconFactory.MAX_ICON_HEIGHT);
			FieldCoordinate[] adjacentCoordinates = game.getFieldModel().findAdjacentCoordinates(pCoordinate,
				FieldCoordinateBounds.FIELD, 1, true);
			for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
				if (pPlayerOverBall) {
					drawBall(g2d, adjacentCoordinate);
					drawPlayer(g2d, adjacentCoordinate);
					drawBomb(g2d, adjacentCoordinate); // moving bomb always on top
				} else {
					drawPlayer(g2d, adjacentCoordinate);
					drawBall(g2d, adjacentCoordinate);
					drawBomb(g2d, adjacentCoordinate); // moving bomb always on top
				}
			}
		}
	}

	private void drawPlayer(Graphics2D pG2d, FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			Player<?> player = getClient().getGame().getFieldModel().getPlayer(pCoordinate);
			if (player != null) {
				PlayerIconFactory playerIconFactory = getClient().getUserInterface().getPlayerIconFactory();
				BufferedImage icon = playerIconFactory.getIcon(getClient(), player);
				if (icon != null) {
					pG2d.drawImage(icon, findCenteredIconUpperLeftX(icon, pCoordinate),
							findCenteredIconUpperLeftY(icon, pCoordinate), null);
				}
			}
		}
	}

	private void drawBall(Graphics2D pG2d, FieldCoordinate pCoordinate) {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		UserInterface userInterface = getClient().getUserInterface();
		if (pCoordinate.equals(fieldModel.getBallCoordinate()) && fieldModel.isBallMoving()) {
			IconCache iconCache = userInterface.getIconCache();
			BufferedImage ballIcon = iconCache.getIconByProperty(IIconProperty.GAME_BALL);
			if (!fieldModel.isBallInPlay()) {
				ballIcon = PlayerIconFactory.fadeIcon(ballIcon);
			}
			pG2d.drawImage(ballIcon, findCenteredIconUpperLeftX(ballIcon, pCoordinate),
					findCenteredIconUpperLeftY(ballIcon, pCoordinate), null);
		}
	}

	private void drawBomb(Graphics2D pG2d, FieldCoordinate pCoordinate) {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		UserInterface userInterface = getClient().getUserInterface();
		if (pCoordinate.equals(fieldModel.getBombCoordinate()) && fieldModel.isBombMoving()) {
			IconCache iconCache = userInterface.getIconCache();
			BufferedImage bombIcon = iconCache.getIconByProperty(IIconProperty.GAME_BOMB);
			pG2d.drawImage(bombIcon, findCenteredIconUpperLeftX(bombIcon, pCoordinate),
					findCenteredIconUpperLeftY(bombIcon, pCoordinate), null);
		}
	}

	public void updatePlayerMarker(PlayerMarker pPlayerMarker) {
		if (pPlayerMarker == null) {
			return;
		}
		Game game = getClient().getGame();
		Player<?> player = game.getPlayerById(pPlayerMarker.getPlayerId());
		if (player == null) {
			return;
		}
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		updateBallAndPlayers(playerCoordinate, true);
	}

	public void init() {
		clear(true);
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel != null) {
			FieldCoordinate[] playerCoordinates = fieldModel.getPlayerCoordinates();
			for (FieldCoordinate playerCoordinate : playerCoordinates) {
				updateBallAndPlayers(playerCoordinate, true);
			}
			updateBallAndPlayers(fieldModel.getBallCoordinate(), false);
		}
	}

}

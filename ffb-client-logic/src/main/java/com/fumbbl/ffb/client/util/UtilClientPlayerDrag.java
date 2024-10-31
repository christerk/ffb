package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.BoxComponent;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

/**
 *
 * @author Kalimar
 */
public class UtilClientPlayerDrag {

	public static FieldCoordinate getFieldCoordinate(FantasyFootballClient pClient, MouseEvent pMouseEvent,
			boolean pBoxMode) {
		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
		Dimension fieldDimension = dimensionProvider.dimension(Component.FIELD);
		Dimension boxComponentSize = dimensionProvider.dimension(Component.BOX);
		FieldCoordinate coordinate;
		if (pBoxMode) {
			coordinate = getBoxFieldCoordinate(pClient, pMouseEvent.getX(), pMouseEvent.getY());
			if ((coordinate == null) && (pMouseEvent.getX() >= boxComponentSize.width)) {
				coordinate = getFieldFieldCoordinate(fieldDimension, pMouseEvent.getX() - boxComponentSize.width, pMouseEvent.getY(), dimensionProvider);
			}
		} else {
			coordinate = getFieldFieldCoordinate(fieldDimension, pMouseEvent.getX(), pMouseEvent.getY(), dimensionProvider);
			if ((coordinate == null) && (pMouseEvent.getX() < 0)) {
				coordinate = getBoxFieldCoordinate(pClient, boxComponentSize.width + pMouseEvent.getX(), pMouseEvent.getY());
			}
		}
		return coordinate;
	}

	private static FieldCoordinate getFieldFieldCoordinate(Dimension fieldDimension, int pMouseX, int pMouseY, DimensionProvider dimensionProvider) {

		int actualX = pMouseX;
		int actualY = pMouseY;

		if (dimensionProvider.isPitchPortrait()) {
			//noinspection SuspiciousNameCombination
			actualY = pMouseX;
			actualX = fieldDimension.height - pMouseY;
		}

		if ((actualX >= 0) && (actualX < fieldDimension.width) && (actualY >= 0)
			&& (actualY < fieldDimension.height)) {
			return new FieldCoordinate((actualX / dimensionProvider.fieldSquareSize()), (actualY / dimensionProvider.fieldSquareSize()));
		} else {
			return null;
		}
	}

	private static FieldCoordinate getBoxFieldCoordinate(FantasyFootballClient pClient, int pMouseX, int pMouseY) {
		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
		Dimension boxSquareSie = dimensionProvider.dimension(Component.BOX_SQUARE);
		Dimension boxComponentSize = dimensionProvider.dimension(Component.BOX);
		if ((pMouseX >= 0) && (pMouseX < boxComponentSize.width) && (pMouseY >= 0) && (pMouseY < boxComponentSize.height)) {
			int boxTitleOffset = pClient.getUserInterface().getSideBarHome().getBoxComponent().getMaxTitleOffset();
			int y = (((pMouseY - boxTitleOffset) / boxSquareSie.height) * 3)
				+ (pMouseX / boxSquareSie.width);
			if ((y >= 0) && (y < BoxComponent.MAX_BOX_ELEMENTS)) {
				return new FieldCoordinate(FieldCoordinate.RSV_HOME_X, y);
			}
		}
		return null;
	}

	public static void mousePressed(FantasyFootballClient pClient, MouseEvent pMouseEvent, boolean pBoxMode) {
		FieldCoordinate coordinate = getFieldCoordinate(pClient, pMouseEvent, pBoxMode);
		initPlayerDragging(pClient, coordinate, pBoxMode);
	}

	private static void initPlayerDragging(FantasyFootballClient pClient, FieldCoordinate pCoordinate, boolean pBoxMode) {
		Game game = pClient.getGame();
		ClientData clientData = pClient.getClientData();
		UserInterface userInterface = pClient.getUserInterface();
		Player<?> player = game.getFieldModel().getPlayer(pCoordinate);
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		boolean initDragAllowed = ((ClientMode.PLAYER == pClient.getMode()) && (player != null)
				&& game.getTeamHome().hasPlayer(player) && pClient.getClientState().isInitDragAllowed(pCoordinate));
		if (initDragAllowed) {
			if (pBoxMode) {
				initDragAllowed = (((playerState.getBase() == PlayerState.STANDING) && playerState.isActive())
						|| (playerState.getBase() == PlayerState.RESERVE) || (playerState.getBase() == PlayerState.BEING_DRAGGED));
			} else {
				initDragAllowed = (((playerState.getBase() == PlayerState.STANDING) && playerState.isActive())
						|| (playerState.getBase() == PlayerState.BEING_DRAGGED));
			}
		}

		if (initDragAllowed) {
			clientData.setSelectedPlayer(player);
			clientData.setDragStartPosition(pCoordinate);
			clientData.setDragEndPosition(pCoordinate);
			game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.BEING_DRAGGED));
			if (pBoxMode) {
				userInterface.getSideBarHome().refresh();
			} else {
				pClient.getClientState().hideSelectSquare();
				userInterface.getFieldComponent().refresh();
			}
		} else {
			clientData.setDragStartPosition(null);
		}
	}

	public static void mouseDragged(FantasyFootballClient pClient, MouseEvent pMouseEvent, boolean pBoxMode) {
		Game game = pClient.getGame();
		ClientData clientData = pClient.getClientData();
		UserInterface userInterface = pClient.getUserInterface();
		FieldCoordinate coordinate = getFieldCoordinate(pClient, pMouseEvent, pBoxMode);
		if ((coordinate != null) && pClient.getClientState().isDragAllowed(coordinate)) {
			if (clientData.getDragStartPosition() == null) {
				initPlayerDragging(pClient, coordinate, pBoxMode);
			} else {
				if (!coordinate.equals(clientData.getDragEndPosition())) {
					game.getFieldModel().setPlayerCoordinate(clientData.getSelectedPlayer(), coordinate);
					clientData.setDragEndPosition(coordinate);
					userInterface.getSideBarHome().refresh();
					userInterface.getFieldComponent().refresh();
				}
			}
		}
	}

	public static void mouseReleased(FantasyFootballClient pClient) {
		Game game = pClient.getGame();
		ClientData clientData = pClient.getClientData();
		if ((clientData.getSelectedPlayer() != null) && (clientData.getDragStartPosition() != null)
			&& (clientData.getDragEndPosition() != null)) {
			if (pClient.getClientState().isDropAllowed(clientData.getDragEndPosition())) {
				pClient.getCommunication().sendSetupPlayer(clientData.getSelectedPlayer(), clientData.getDragEndPosition());
			} else {
				game.getFieldModel().setPlayerCoordinate(clientData.getSelectedPlayer(), clientData.getDragStartPosition());
			}
		}
		resetDragging(pClient);
		clientData.clear();
	}

	public static void resetDragging(FantasyFootballClient pClient) {
		Game game = pClient.getGame();
		for (Player<?> player : game.getPlayers()) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.getBase() == PlayerState.BEING_DRAGGED) {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				if (playerCoordinate != null) {
					if (playerCoordinate.isBoxCoordinate()) {
						game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
					} else {
						game.getFieldModel().setPlayerState(player,
								playerState.changeBase(PlayerState.STANDING).changeActive(true));
					}
				}
			}
		}
		UserInterface userInterface = pClient.getUserInterface();
		userInterface.getSideBarHome().refresh();
		userInterface.getFieldComponent().refresh();
	}

}

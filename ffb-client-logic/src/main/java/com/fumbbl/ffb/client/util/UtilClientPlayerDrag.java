package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author Kalimar
 */
public class UtilClientPlayerDrag {

	public static FieldCoordinate getFieldCoordinate(FantasyFootballClient pClient, MouseEvent pMouseEvent,
			boolean pBoxMode) {
		UserInterface userInterface = pClient.getUserInterface();
		Point contentPoint = userInterface.toClientContentPoint(
			(java.awt.Component) pMouseEvent.getSource(),
			pMouseEvent.getPoint()
		);
		int boxTitleOffset = userInterface.getSideBarHome().getBoxComponent().getMaxTitleOffset();

		return userInterface.getSetupDragHitTester().toFieldCoordinate(contentPoint, boxTitleOffset);
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

package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.ui.BoxComponent;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.event.MouseEvent;

/**
 *
 * @author Kalimar
 */
public class UtilClientPlayerDrag {

	public static FieldCoordinate getFieldCoordinate(FantasyFootballClient pClient, MouseEvent pMouseEvent,
			boolean pBoxMode) {
		FieldCoordinate coordinate;
		if (pBoxMode) {
			coordinate = getBoxFieldCoordinate(pClient, pMouseEvent.getX(), pMouseEvent.getY());
			if ((coordinate == null) && (pMouseEvent.getX() >= BoxComponent.WIDTH)) {
				coordinate = getFieldFieldCoordinate(pMouseEvent.getX() - BoxComponent.WIDTH, pMouseEvent.getY());
			}
		} else {
			coordinate = getFieldFieldCoordinate(pMouseEvent.getX(), pMouseEvent.getY());
			if ((coordinate == null) && (pMouseEvent.getX() < 0)) {
				coordinate = getBoxFieldCoordinate(pClient, BoxComponent.WIDTH + pMouseEvent.getX(), pMouseEvent.getY());
			}
		}
		return coordinate;
	}

	private static FieldCoordinate getFieldFieldCoordinate(int pMouseX, int pMouseY) {
		if ((pMouseX >= 0) && (pMouseX < FieldLayer.FIELD_IMAGE_WIDTH) && (pMouseY >= 0)
			&& (pMouseY < FieldLayer.FIELD_IMAGE_HEIGHT)) {
			return new FieldCoordinate((pMouseX / FieldLayer.FIELD_SQUARE_SIZE), (pMouseY / FieldLayer.FIELD_SQUARE_SIZE));
		} else {
			return null;
		}
	}

	private static FieldCoordinate getBoxFieldCoordinate(FantasyFootballClient pClient, int pMouseX, int pMouseY) {
		if ((pMouseX >= 0) && (pMouseX < BoxComponent.WIDTH) && (pMouseY >= 0) && (pMouseY < BoxComponent.HEIGHT)) {
			int boxTitleOffset = pClient.getUserInterface().getSideBarHome().getBoxComponent().getMaxTitleOffset();
			int y = (((pMouseY - boxTitleOffset) / BoxComponent.FIELD_SQUARE_SIZE) * 3)
					+ (pMouseX / BoxComponent.FIELD_SQUARE_SIZE);
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

	public static void mouseReleased(FantasyFootballClient pClient, MouseEvent pMouseEvent, boolean pBoxMode) {
		Game game = pClient.getGame();
		ClientData clientData = pClient.getClientData();
		if ((clientData.getSelectedPlayer() != null) && (clientData.getDragStartPosition() != null)
			&& (clientData.getDragEndPosition() != null)) {
			if (pClient.getClientState().isDropAllowed(clientData.getDragEndPosition())) {
				System.out.println("Util: Sending setup command: " + clientData.getSelectedPlayer().getId());
				pClient.getCommunication().sendSetupPlayer(clientData.getSelectedPlayer(), clientData.getDragEndPosition());
			} else {
				System.out.println("Util: Resetting player: " + clientData.getSelectedPlayer().getId());
				game.getFieldModel().setPlayerCoordinate(clientData.getSelectedPlayer(), clientData.getDragStartPosition());
			}
		} else {
			System.out.println("Util: Ignoring released event");
			System.out.println("Player is " + (clientData.getSelectedPlayer() != null ? clientData.getSelectedPlayer().getId() : "null"));
			System.out.println("DragStart is " + (clientData.getDragStartPosition() != null ? clientData.getDragStartPosition() : "null"));
			System.out.println("DragEnd is " + (clientData.getDragEndPosition() != null ? clientData.getDragEndPosition() : "null"));
		}
		System.out.println("Event: " + pMouseEvent);
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

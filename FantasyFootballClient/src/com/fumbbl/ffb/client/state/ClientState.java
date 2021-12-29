package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientMarker;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.INetCommandHandler;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Kalimar
 */
public abstract class ClientState implements INetCommandHandler, MouseListener, MouseMotionListener, ActionListener {

	public static final int FIELD_SQUARE_SIZE = 30;

	private final FantasyFootballClient fClient;

	private FieldCoordinate fSelectSquareCoordinate;

	private boolean fClickable;

	private boolean fSelectable;

	private boolean fPopupMenuShown;

	private JPopupMenu fPopupMenu;

	private Player<?> fPopupMenuPlayer;

	protected ClientState(FantasyFootballClient pClient) {
		fClient = pClient;
		setSelectable(true);
		setClickable(true);
	}

	public abstract ClientStateId getId();

	public void enterState() {
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getDialogManager().updateDialog();
		UtilClientCursor.setDefaultCursor(userInterface);
	}

	public void leaveState() {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
	}

	// Interface Methods

	// MouseMotionListener
	public void mouseDragged(MouseEvent pMouseEvent) {
	}

	// MouseListener
	public void mouseClicked(MouseEvent pMouseEvent) {
	}

	// MouseListener
	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	// MouseListener
	public void mousePressed(MouseEvent pMouseEvent) {
	}

	public void handleCommand(NetCommand pNetCommand) {
	}

	// Helper Methods

	public FantasyFootballClient getClient() {
		return fClient;
	}

	protected FieldCoordinate getFieldCoordinate(MouseEvent pMouseEvent) {
		FieldCoordinate coordinate = null;
		int x = pMouseEvent.getX();
		int y = pMouseEvent.getY();
		if ((x < 31 * FIELD_SQUARE_SIZE) && (y > 0) && (y < 450)) {
			coordinate = new FieldCoordinate((x / FIELD_SQUARE_SIZE), (y / FIELD_SQUARE_SIZE));
		}
		return coordinate;
	}

	public void showSelectSquare(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			fSelectSquareCoordinate = pCoordinate;
			drawSelectSquare(fSelectSquareCoordinate, new Color(0.0f, 0.0f, 1.0f, 0.2f));
			// drawSelectSquare(fSelectSquareCoordinate, new Color(0.0f, 0.0f, 1.0f));
		}
	}

	protected void drawSelectSquare(FieldCoordinate pCoordinate, Color pColor) {
		if (pCoordinate != null) {
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
			int x = pCoordinate.getX() * FIELD_SQUARE_SIZE + 1;
			int y = pCoordinate.getY() * FIELD_SQUARE_SIZE + 1;
			Rectangle bounds = new Rectangle(x, y, FIELD_SQUARE_SIZE - 2, FIELD_SQUARE_SIZE - 2);
			Graphics2D g2d = fieldComponent.getImage().createGraphics();
			g2d.setPaint(pColor);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2d.dispose();
			fieldComponent.repaint(bounds);
		}
	}

	public void hideSelectSquare() {
		if (fSelectSquareCoordinate != null) {
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
			int x = fSelectSquareCoordinate.getX() * FIELD_SQUARE_SIZE;
			int y = fSelectSquareCoordinate.getY() * FIELD_SQUARE_SIZE;
			Rectangle bounds = new Rectangle(x, y, FIELD_SQUARE_SIZE, FIELD_SQUARE_SIZE);
			fieldComponent.refresh(bounds);
			fSelectSquareCoordinate = null;
		}
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		if (isSelectable()) {
			FieldCoordinate coordinate = getFieldCoordinate(pMouseEvent);
			if ((coordinate == null) || !FieldCoordinateBounds.FIELD.isInBounds(coordinate)) {
				hideSelectSquare();
			} else {
				if (!coordinate.equals(fSelectSquareCoordinate)) {
					hideSelectSquare();
					boolean selectable;
					Game game = getClient().getGame();
					Player<?> player = game.getFieldModel().getPlayer(coordinate);
					if (player != null) {
						selectable = mouseOverPlayer(player);
					} else {
						selectable = mouseOverField(coordinate);
					}
					if (selectable) {
						showSelectSquare(coordinate);
					}
				}
			}
		}
	}

	public void mouseExited(MouseEvent pMouseEvent) {
		hideSelectSquare();
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
		setSelectable(true);
		FieldCoordinate coordinate = getFieldCoordinate(pMouseEvent);
		if ((getClient().getGame() != null) && (coordinate != null)) {
			Player<?> player = getClient().getGame().getFieldModel().getPlayer(coordinate);
			if (pMouseEvent.isShiftDown()) {
				hideSelectSquare();
				if (player != null) {
					int x = (coordinate.getX() + 1) * FIELD_SQUARE_SIZE;
					int y = (coordinate.getY() + 1) * FIELD_SQUARE_SIZE;
					UtilClientMarker.showMarkerPopup(getClient(), player, x, y);
				} else {
					UtilClientMarker.showMarkerPopup(getClient(), coordinate);
				}
			} else {
				if (isClickable()) {
					hideSelectSquare();
					if (player != null) {
						clickOnPlayer(player);
					} else {
						clickOnField(coordinate);
					}
				} else {
					fPopupMenuShown = false;
				}
			}
		}
	}

	public void createPopupMenu(JMenuItem[] pMenuItems) {
		fPopupMenu = new JPopupMenu();
		for (JMenuItem pMenuItem : pMenuItems) {
			pMenuItem.addActionListener(this);
			fPopupMenu.add(pMenuItem);
		}
	}

	public void showPopupMenuForPlayer(Player<?> pPlayer) {
		if ((pPlayer != null) && (fPopupMenu != null)) {
			fPopupMenuPlayer = pPlayer;
			FieldCoordinate coordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(fPopupMenuPlayer);
			if (coordinate != null) {
				setSelectable(false);
				int x = (coordinate.getX() + 1) * FIELD_SQUARE_SIZE;
				int y = (coordinate.getY() + 1) * FIELD_SQUARE_SIZE;
				fPopupMenu.show(fClient.getUserInterface().getFieldComponent(), x, y);
				fPopupMenuShown = true;
			}
		}
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		JMenuItem menuItem = (JMenuItem) (pActionEvent.getSource());
		setSelectable(true);
		fPopupMenuShown = false;
		menuItemSelected(fPopupMenuPlayer, menuItem.getMnemonic());
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		if (getClient().getClientData().getSelectedPlayer() != pPlayer) {
			getClient().getClientData().setSelectedPlayer(pPlayer);
			getClient().getUserInterface().refreshSideBars();
		}
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (getClient().getClientData().getSelectedPlayer() != null) {
			getClient().getClientData().setSelectedPlayer(null);
			getClient().getUserInterface().refreshSideBars();
		}
		return true;
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
	}

	public void setClickable(boolean pClickable) {
		fClickable = pClickable;
	}

	public boolean isClickable() {
		return (fClickable && getClient().getUserInterface().getDialogManager().isDialogHidden() && !fPopupMenuShown);
	}

	public void setSelectable(boolean pSelectable) {
		fSelectable = pSelectable;
		if (!isSelectable()) {
			hideSelectSquare();
		}
	}

	public boolean isSelectable() {
		return fSelectable;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		return false;
	}

	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		return false;
	}

	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		return false;
	}

	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		return false;
	}

	public void refreshSettings() {
	}

	public void endTurn() {
	}

	protected boolean isHypnoticGazeActionAvailable(boolean declareAtStart, Player<?> player) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		return ((mechanic.declareGazeActionAtStart() == declareAtStart)
			&& mechanic.isGazeActionAllowed(game.getTurnMode(), actingPlayer.getPlayerAction())
			&& UtilPlayer.canGaze(game, player));
	}

}

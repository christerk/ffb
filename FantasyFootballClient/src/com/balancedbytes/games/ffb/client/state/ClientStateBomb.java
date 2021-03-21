package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldComponent;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.net.ClientCommunication;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientStateBomb extends ClientState {

	private boolean fShowRangeRuler;
	private RangeGridHandler fRangeGridHandler;

	protected ClientStateBomb(FantasyFootballClient pClient) {
		super(pClient);
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	public ClientStateId getId() {
		return ClientStateId.BOMB;
	}

	public void enterState() {
		super.enterState();
		setSelectable(true);
		fShowRangeRuler = true;
		fRangeGridHandler.refreshSettings();
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			createAndShowPopupMenuForActingPlayer();
		} else {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			clickOnField(playerCoordinate);
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, pCoordinate, false);
		if ((PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) || (passingDistance != null)) {
			game.setPassCoordinate(pCoordinate);
			getClient().getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
			game.getFieldModel().setRangeRuler(null);
			getClient().getUserInterface().getFieldComponent().refresh();
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		return mouseOverField(playerCoordinate);
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;
		if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
			game.getFieldModel().setRangeRuler(null);
			userInterface.getFieldComponent().refresh();
			selectable = true;
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
		} else {
			drawRangeRuler(pCoordinate);
		}
		return selectable;
	}

	private boolean drawRangeRuler(FieldCoordinate pCoordinate) {
		RangeRuler rangeRuler = null;
		Game game = getClient().getGame();
		if (fShowRangeRuler && (game.getPassCoordinate() == null)) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			UserInterface userInterface = getClient().getUserInterface();
			FieldComponent fieldComponent = userInterface.getFieldComponent();
			rangeRuler = UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), pCoordinate, false);
			game.getFieldModel().setRangeRuler(rangeRuler);
			if (rangeRuler != null) {
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
			} else {
				UtilClientCursor.setDefaultCursor(userInterface);
			}
			fieldComponent.getLayerUnderPlayers().clearMovePath();
			fieldComponent.refresh();
		}
		return (rangeRuler != null);
	}

	@Override
	public void handleCommand(NetCommand pNetCommand) {
		fRangeGridHandler.refreshRangeGrid();
		super.handleCommand(pNetCommand);
	}

	@Override
	public void leaveState() {
		fRangeGridHandler.setShowRangeGrid(false);
		fRangeGridHandler.refreshRangeGrid();
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (isHailMaryPassActionAvailable()) {
			String text = (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) ? "Don't use Hail Mary Pass"
					: "Use Hail Mary Pass";
			JMenuItem hailMaryBombAction = new JMenuItem(text,
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_HAIL_MARY_BOMB)));
			hailMaryBombAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB);
			hailMaryBombAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB, 0));
			menuItemList.add(hailMaryBombAction);
		}

		if (isRangeGridAvailable()) {
			JMenuItem toggleRangeGridAction = new JMenuItem("Range Grid on/off",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_RANGE_GRID)));
			toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
			menuItemList.add(toggleRangeGridAction);
		}

		if (isEndTurnActionAvailable()) {
			String endMoveActionLabel = actingPlayer.hasActed() ? "End Move" : "Deselect Player";
			JMenuItem endMoveAction = new JMenuItem(endMoveActionLabel,
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
			endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
			endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
			menuItemList.add(endMoveAction);
		}

		createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ClientCommunication communication = getClient().getCommunication();
		switch (pMenuKey) {
		case IPlayerPopupMenuKeys.KEY_END_MOVE:
			if (isEndTurnActionAvailable()) {
				communication.sendActingPlayer(null, null, false);
			}
			break;
		case IPlayerPopupMenuKeys.KEY_RANGE_GRID:
			if (isRangeGridAvailable()) {
				fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
				fRangeGridHandler.refreshRangeGrid();
			}
			break;
		case IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB:
			if (isHailMaryPassActionAvailable()) {
				if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
					communication.sendActingPlayer(pPlayer, PlayerAction.THROW_BOMB, actingPlayer.isJumping());
					fShowRangeRuler = true;
				} else {
					communication.sendActingPlayer(pPlayer, PlayerAction.HAIL_MARY_BOMB, actingPlayer.isJumping());
					fShowRangeRuler = false;
				}
				if (!fShowRangeRuler && (game.getFieldModel().getRangeRuler() != null)) {
					game.getFieldModel().setRangeRuler(null);
				}
			}
			break;
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		switch (pActionKey) {
		case PLAYER_ACTION_RANGE_GRID:
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			return true;
		case PLAYER_ACTION_HAIL_MARY_PASS:
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB);
			return true;
		case PLAYER_ACTION_END_MOVE:
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_END_MOVE);
			return true;
		default:
			return super.actionKeyPressed(pActionKey);
		}
	}

	private boolean isHailMaryPassActionAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)
				&& !(game.getFieldModel().getWeather().equals(Weather.BLIZZARD)));
	}

	private boolean isRangeGridAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB);
	}

	private boolean isEndTurnActionAvailable() {
		Game game = getClient().getGame();
		return !game.getTurnMode().isBombTurn();
	}

}

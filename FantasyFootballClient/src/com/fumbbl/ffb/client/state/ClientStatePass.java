package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.UtilPlayer;
import com.fumbbl.ffb.util.UtilRangeRuler;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ClientStatePass extends ClientStateMove {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	protected ClientStatePass(FantasyFootballClient pClient) {
		super(pClient);
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	public ClientStateId getId() {
		return ClientStateId.PASS;
	}

	public void enterState() {
		super.enterState();
		fShowRangeRuler = true;
		fRangeGridHandler.refreshSettings();
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			if (!actingPlayer.hasPassed() && (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()
				|| (UtilPlayer.hasBall(game, actingPlayer.getPlayer())
				&& ((PlayerAction.PASS == actingPlayer.getPlayerAction()) || canPlayerGetPass(pPlayer))))) {
				game.setPassCoordinate(game.getFieldModel().getPlayerCoordinate(pPlayer));
				getClient().getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
				game.getFieldModel().setRangeRuler(null);
				userInterface.getFieldComponent().refresh();
			}
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		if (actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) {
			super.clickOnField(pCoordinate);
		} else {
			if ((PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction())
				|| UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
				game.setPassCoordinate(pCoordinate);
				getClient().getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
				game.getFieldModel().setRangeRuler(null);
				userInterface.getFieldComponent().refresh();
			}
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		boolean selectable = false;
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((PlayerAction.HAIL_MARY_PASS != actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
			FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			if ((PlayerAction.PASS == actingPlayer.getPlayerAction()) || canPlayerGetPass(pPlayer)) {
				drawRangeRuler(catcherCoordinate);
			}
		} else {
			game.getFieldModel().setRangeRuler(null);
			FieldComponent fieldComponent = userInterface.getFieldComponent();
			fieldComponent.getLayerUnderPlayers().clearMovePath();
			fieldComponent.refresh();
			selectable = true;
			if (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) {
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
			} else {
				UtilClientCursor.setDefaultCursor(userInterface);
			}
		}
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();
		return selectable;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;
		if (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) {
			game.getFieldModel().setRangeRuler(null);
			userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
			userInterface.getFieldComponent().refresh();
			selectable = true;
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
		} else if (actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) {
			game.getFieldModel().setRangeRuler(null);
			userInterface.getFieldComponent().refresh();
			selectable = super.mouseOverField(pCoordinate);
		} else {
			drawRangeRuler(pCoordinate);
		}
		return selectable;
	}

	private void drawRangeRuler(FieldCoordinate pCoordinate) {
		RangeRuler rangeRuler;
		Game game = getClient().getGame();
		if (fShowRangeRuler && (game.getPassCoordinate() == null)) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			UserInterface userInterface = getClient().getUserInterface();
			FieldComponent fieldComponent = userInterface.getFieldComponent();
			rangeRuler = UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), pCoordinate, false);
			game.getFieldModel().setRangeRuler(rangeRuler);
			if (rangeRuler != null) {
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
			} else {
				UtilClientCursor.setDefaultCursor(userInterface);
			}
			fieldComponent.getLayerUnderPlayers().clearMovePath();
			fieldComponent.refresh();
		}
	}

	public boolean canPlayerGetPass(Player<?> pCatcher) {
		boolean canGetPass = false;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((pCatcher != null) && (actingPlayer.getPlayer() != null)) {
			PlayerState catcherState = game.getFieldModel().getPlayerState(pCatcher);
			canGetPass = ((catcherState != null)
				&& catcherState.hasTacklezones() && (game.getTeamHome() == pCatcher.getTeam())
				&& (!actingPlayer.isSufferingAnimosity() || actingPlayer.getRace().equals(pCatcher.getRace())));
		}
		return canGetPass;
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

		if ((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && !actingPlayer.hasPassed()) {
			JMenuItem passAction = new JMenuItem(dimensionProvider(), "Pass Ball (any square)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
		}

		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && !actingPlayer.hasPassed()
			&& !game.getFieldModel().getWeather().equals(Weather.BLIZZARD)) {
			String text = (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) ? "Don't use Hail Mary Pass"
				: "Use Hail Mary Pass";
			JMenuItem hailMaryPassAction = new JMenuItem(dimensionProvider(), text,
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_HAIL_MARY_PASS)));
			hailMaryPassAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS);
			hailMaryPassAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS, 0));
			menuItemList.add(hailMaryPassAction);
		}

		if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
			JMenuItem jumpAction;
			if (actingPlayer.isJumping()) {
				jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			} else {
				jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP)));
			}
			jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
			jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
			menuItemList.add(jumpAction);
		}

		if (!actingPlayer.hasPassed()) {
			JMenuItem toggleRangeGridAction = new JMenuItem(dimensionProvider(), "Range Grid on/off",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_RANGE_GRID)));
			toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
			menuItemList.add(toggleRangeGridAction);
		}

		if (!actingPlayer.hasPassed()) {
			if (!actingPlayer.isSufferingAnimosity()) {
				JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
				moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
				moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
				menuItemList.add(moveAction);
			}
		}

		if (isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}

		if (isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		addEndActionLabel(iconCache, menuItemList, actingPlayer);

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ClientCommunication communication = getClient().getCommunication();
		switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_RANGE_GRID:
				fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
				fRangeGridHandler.refreshRangeGrid();
				break;
			case IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS:
				if (game.getActingPlayer().getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)) {
					if (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(pPlayer, PlayerAction.PASS, actingPlayer.isJumping());
						fShowRangeRuler = true;
					} else {
						communication.sendActingPlayer(pPlayer, PlayerAction.HAIL_MARY_PASS, actingPlayer.isJumping());
						fShowRangeRuler = false;
					}
					if (!fShowRangeRuler && (game.getFieldModel().getRangeRuler() != null)) {
						game.getFieldModel().setRangeRuler(null);
					}
				}
				break;
			default:
				super.menuItemSelected(pPlayer, pMenuKey);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == ActionKey.PLAYER_ACTION_RANGE_GRID) {
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			return true;
		} else if (pActionKey == ActionKey.PLAYER_ACTION_HAIL_MARY_PASS) {
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS);
			return true;
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}

}

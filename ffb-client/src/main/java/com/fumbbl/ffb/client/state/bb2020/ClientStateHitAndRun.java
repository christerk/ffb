package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.HitAndRunLogicModule;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientStateHitAndRun extends ClientStateAwt<HitAndRunLogicModule> {
	public ClientStateHitAndRun(FantasyFootballClientAwt client) {
		super(client, new HitAndRunLogicModule(client));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.HIT_AND_RUN;
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			getClient().getCommunication().sendFieldCoordinate(pCoordinate);
		}
	}

	@Override
	protected void clickOnPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			createAndShowPopupMenuForActingPlayer();
		}
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		menuItemList.add(createCancelItem(iconCache));

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	@Override
	protected boolean mouseOverPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_HIT_AND_RUN);
		}
		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (getClient().getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_HIT_AND_RUN);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_HIT_AND_RUN);
		}

		return true;
	}

	protected JMenuItem createCancelItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Cancel Hit And Run",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HIT_AND_RUN)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_HIT_AND_RUN);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HIT_AND_RUN, 0));
		return menuItem;
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (pMenuKey == IPlayerPopupMenuKeys.KEY_HIT_AND_RUN) {
			getClient().getCommunication().sendEndTurn(getClient().getGame().getTurnMode());
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return null;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		if (pActionKey == ActionKey.PLAYER_ACTION_HIT_AND_RUN) {
			menuItemSelected(player, IPlayerPopupMenuKeys.KEY_HIT_AND_RUN);
			return true;
		}
		return false;
	}

}

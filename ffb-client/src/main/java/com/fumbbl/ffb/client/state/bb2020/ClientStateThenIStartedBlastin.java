package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.ThenIStartedBlastinLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientStateThenIStartedBlastin extends ClientStateAwt<ThenIStartedBlastinLogicModule> {

	public ClientStateThenIStartedBlastin(FantasyFootballClientAwt pClient) {
		super(pClient, new ThenIStartedBlastinLogicModule(pClient));
	}

	@Override
	public void enterState() {
		super.enterState();
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	@Override
	public void leaveState() {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		fieldModel.clearMoveSquares();
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			default:
				break;
		}
	}


	public boolean mouseOverPlayer(Player<?> player) {
		UserInterface userInterface = getClient().getUserInterface();

		InteractionResult result = logicModule.playerPeek(player);
		switch (result.getKind()) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BLASTIN);
				break;
			case RESET:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_BLASTIN);
				break;
			default:
				break;
		}
		userInterface.refreshSideBars();
		return true;
	}

	@Override
	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_BLASTIN);
		return super.mouseOverField(pCoordinate);
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (isEndPlayerActionAvailable()) {
			addEndActionLabel(iconCache, menuItemList);
		}

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
		}};
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_END_MOVE:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
				return true;
			default:
				return super.actionKeyPressed(pActionKey);
		}
	}

	private boolean isEndPlayerActionAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !actingPlayer.hasActed();
	}

	@Override
	protected String deselectPlayerLabel() {
		return "Don't blast";
	}
}

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
import com.fumbbl.ffb.client.state.logic.bb2020.ThrowKegLogicModule;
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

public class ClientStateThrowKeg extends ClientStateAwt<ThrowKegLogicModule> {

	public ClientStateThrowKeg(FantasyFootballClientAwt pClient) {
		super(pClient, new ThrowKegLogicModule(pClient));
	}

	@Override
	public void initUI() {
		super.initUI();
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
		InteractionResult result = logicModule.playerPeek(player);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.refreshSideBars();
		switch (result.getKind()) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_KEG);
				break;
			case INVALID:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_KEG);
				break;
			default:
				break;
		}

		return true;
	}

	@Override
	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_KEG);
		return super.mouseOverField(pCoordinate);
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (logicModule.isEndPlayerActionAvailable()) {
			addEndActionLabel(iconCache, menuItemList);
		}

		if (logicModule.isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
		}
		if (logicModule.isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}
		if (logicModule.isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (logicModule.isLookIntoMyEyesAvailable(actingPlayer)) {
			menuItemList.add(createLookIntoMyEyesItem(iconCache));
		}
		if (logicModule.isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		if (logicModule.isBlackInkAvailable(actingPlayer)) {
			menuItemList.add(createBlackInkItem(iconCache));
		}
		if (logicModule.isCatchOfTheDayAvailable(actingPlayer)) {
			menuItemList.add(createCatchOfTheDayItem(iconCache));
		}
		if (logicModule.isThenIStartedBlastinAvailable(actingPlayer)) {
			menuItemList.add(createThenIStartedBlastinItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

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
			case PLAYER_ACTION_TREACHEROUS:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
				return true;
			case PLAYER_ACTION_WISDOM:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
				return true;
			case PLAYER_ACTION_RAIDING_PARTY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
				return true;
			case PLAYER_ACTION_LOOK_INTO_MY_EYES:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
				return true;
			case PLAYER_ACTION_BALEFUL_HEX:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
				return true;
			case PLAYER_ACTION_BLACK_INK:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
				return true;
			case PLAYER_ACTION_CATCH_OF_THE_DAY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
				return true;
			case PLAYER_ACITON_THEN_I_STARTED_BLASTIN:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
				return true;
			default:
				return super.actionKeyPressed(pActionKey);
		}
	}


	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE,ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_TREACHEROUS,ClientAction.TREACHEROUS);
			put(IPlayerPopupMenuKeys.KEY_WISDOM,ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY,ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES,ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX,ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK,ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY,ClientAction.CATCH_OF_THE_DAY);
			put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN,ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

}

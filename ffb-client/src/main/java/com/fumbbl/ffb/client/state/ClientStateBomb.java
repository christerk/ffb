package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.BombLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.NetCommand;

import java.util.*;

/**
 * @author Kalimar
 */
public class ClientStateBomb extends ClientStateAwt<BombLogicModule> {

	private final RangeGridHandler fRangeGridHandler;

	protected ClientStateBomb(FantasyFootballClientAwt pClient) {
		super(pClient, new BombLogicModule(pClient));
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	public void initUI() {
		fRangeGridHandler.refreshSettings();
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);

		switch (result.getKind()) {
			case SELECT_ACTION:
				createAndShowPopupMenuForActingPlayer(result.getActionContext());
				break;
			case PERFORM:
				clickOnField(result.getCoordinate());
				break;
			default:
				break;
		}
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				getClient().getUserInterface().getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case PERFORM:
				UserInterface userInterface = getClient().getUserInterface();
				userInterface.refreshSideBars();
				return mouseOverField(result.getCoordinate());
			default:
				return false;
		}
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				userInterface.getFieldComponent().refresh();
				selectable = true;
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
				break;
			case DRAW:
				drawRangeRuler(result.getRangeRuler());
				break;
			default:
				break;
		}
		return selectable;
	}

	private void drawRangeRuler(RangeRuler rangeRuler) {
		UserInterface userInterface = getClient().getUserInterface();
		FieldComponent fieldComponent = userInterface.getFieldComponent();
		if (rangeRuler != null) {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
		} else {
			UtilClientCursor.setDefaultCursor(userInterface);
		}
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		fieldComponent.refresh();
	}

	@Override
	public void handleCommand(NetCommand pNetCommand) {
		fRangeGridHandler.refreshRangeGrid();
		super.handleCommand(pNetCommand);
	}

	@Override
	public void leaveState() {
		getClient().getUserInterface().getFieldComponent().getLayerRangeRuler().removeRangeRuler();
		getClient().getUserInterface().getFieldComponent().refresh();
		fRangeGridHandler.setShowRangeGrid(false);
		fRangeGridHandler.refreshRangeGrid();
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = super.influencedItemConfigs();
		Map<ClientAction, MenuItemConfig> hailMary = new HashMap<>();
		influences.put(Influences.IS_THROWING_HAIL_MARY, hailMary);
		hailMary.put(ClientAction.HAIL_MARY_PASS, new MenuItemConfig("Don't use Hail Mary Pass", IIconProperty.ACTION_TOGGLE_HAIL_MARY_BOMB, IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB));
		return influences;
	}

	@Override
	protected List<JMenuItem> uiOnlyMenuItems() {
		List<JMenuItem> menuItems = new ArrayList<>();
		menuItems.add(menuItem(new MenuItemConfig("Range Grid on/off", IIconProperty.ACTION_TOGGLE_RANGE_GRID, IPlayerPopupMenuKeys.KEY_RANGE_GRID)));
		return menuItems;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

		itemConfigs.put(ClientAction.HAIL_MARY_PASS, new MenuItemConfig("Use Hail Mary Pass", IIconProperty.ACTION_TOGGLE_HAIL_MARY_BOMB, IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB));
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		itemConfigs.put(ClientAction.TREACHEROUS, new MenuItemConfig("Treacherous", IIconProperty.ACTION_STAB, IPlayerPopupMenuKeys.KEY_TREACHEROUS));
		itemConfigs.put(ClientAction.WISDOM, new MenuItemConfig("Wisdom of the White Dwarf", IIconProperty.ACTION_WISDOM, IPlayerPopupMenuKeys.KEY_WISDOM));
		itemConfigs.put(ClientAction.RAIDING_PARTY, new MenuItemConfig("Raiding Party", IIconProperty.ACTION_RAIDING_PARTY, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY));
		itemConfigs.put(ClientAction.LOOK_INTO_MY_EYES, new MenuItemConfig("Look Into My Eyes", IIconProperty.ACTION_LOOK_INTO_MY_EYES, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES));
		itemConfigs.put(ClientAction.BALEFUL_HEX, new MenuItemConfig("Baleful Hex", IIconProperty.ACTION_BALEFUL_HEX, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX));
		itemConfigs.put(ClientAction.BLACK_INK, new MenuItemConfig("Black Ink", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_BLACK_INK));
		itemConfigs.put(ClientAction.CATCH_OF_THE_DAY, new MenuItemConfig("Catch of the Day", IIconProperty.ACTION_CATCH_OF_THE_DAY, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY));
		itemConfigs.put(ClientAction.THEN_I_STARTED_BLASTIN, new MenuItemConfig("\"Then I Started Blastin'!\"", IIconProperty.ACTION_STARTED_BLASTIN, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN));

		return itemConfigs;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB, ClientAction.HAIL_MARY_BOMB);
			put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, ClientAction.CATCH_OF_THE_DAY);
			put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = logicModule.getActingPlayer().getPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_RANGE_GRID:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RANGE_GRID);
				return true;
			case PLAYER_ACTION_HAIL_MARY_PASS:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB);
				return true;
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
	protected void postPerform(int menuKey) {
		super.postPerform(menuKey);
		if (menuKey == IPlayerPopupMenuKeys.KEY_RANGE_GRID) {
			if (logicModule.playerIsAboutToThrow()) {
				fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
				fRangeGridHandler.refreshRangeGrid();
			}
		}
	}
}

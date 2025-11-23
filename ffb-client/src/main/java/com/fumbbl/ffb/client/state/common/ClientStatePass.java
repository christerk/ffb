package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.RangeGridHandler;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.PassLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.UtilRangeRuler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStatePass extends AbstractClientStateMove<PassLogicModule> {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	public ClientStatePass(FantasyFootballClientAwt pClient) {
		super(pClient, new PassLogicModule(pClient));
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	@Override
	public void setUp() {
		super.setUp();
		fShowRangeRuler = true;
		fRangeGridHandler.refreshSettings();
	}

	public void clickOnPlayer(Player<?> player) {
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case HANDLED:
				userInterface.getFieldComponent().refresh();
				break;
			default:
				evaluateClick(result, player);
				break;
		}
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case DELEGATE:
				getDelegate(result).clickOnField(pCoordinate);
				break;
			case HANDLED:
				userInterface.getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		boolean selectable = false;
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case PREVIEW_THROW:
				drawRangeRuler(result.getCoordinate());
				break;
			default:
				selectable = true;
				FieldComponent fieldComponent = userInterface.getFieldComponent();
				fieldComponent.getLayerUnderPlayers().clearMovePath();
				fieldComponent.refresh();
				getClient().getClientData().setSelectedPlayer(pPlayer);
				userInterface.refreshSideBars();
				determineCursor(result);
				break;
		}
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();

		return selectable;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;

		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		determineCursor(result);
		switch (result.getKind()) {
			case PERFORM:
				userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
				userInterface.getFieldComponent().refresh();
				selectable = true;
				break;
			case DELEGATE:
				selectable = getDelegate(result).mouseOverField(pCoordinate);
				userInterface.getFieldComponent().refresh();
				break;
			case PREVIEW_THROW:
				drawRangeRuler(pCoordinate);
				break;
			default:
				break;
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

	@Override
	public void handleCommand(NetCommand pNetCommand) {
		fRangeGridHandler.refreshRangeGrid();
		super.handleCommand(pNetCommand);
	}

	@Override
	public void tearDown() {
		getClient().getUserInterface().getFieldComponent().getLayerRangeRuler().removeRangeRuler();
		getClient().getUserInterface().getFieldComponent().refresh();
		fRangeGridHandler.setShowRangeGrid(false);
		fRangeGridHandler.refreshRangeGrid();
		super.tearDown();
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = super.influencedItemConfigs();

		Map<ClientAction, MenuItemConfig> hailMary = new HashMap<>();
		influences.put(Influences.IS_THROWING_HAIL_MARY, hailMary);
		hailMary.put(ClientAction.HAIL_MARY_PASS, new MenuItemConfig("Don't use Hail Mary Pass", IIconProperty.ACTION_TOGGLE_HAIL_MARY_PASS, IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS));

		return influences;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {

		Player<?> selectedPlayer = getClient().getClientData().getSelectedPlayer();

		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

		itemConfigs.put(ClientAction.HAIL_MARY_PASS, new MenuItemConfig("Use Hail Mary Pass", IIconProperty.ACTION_TOGGLE_HAIL_MARY_PASS, IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS));
		itemConfigs.put(ClientAction.PASS, new MenuItemConfig("Pass Ball (any square)", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_PASS));
		itemConfigs.put(ClientAction.MOVE, new MenuItemConfig("Move", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_MOVE));
		itemConfigs.put(ClientAction.JUMP, new MenuItemConfig("Jump", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_JUMP));
		itemConfigs.put(ClientAction.BOUNDING_LEAP, new MenuItemConfig("Jump (Bounding Leap)", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP));
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		itemConfigs.put(ClientAction.WISDOM, new MenuItemConfig("Wisdom of the White Dwarf", IIconProperty.ACTION_WISDOM, IPlayerPopupMenuKeys.KEY_WISDOM));
		itemConfigs.put(ClientAction.RAIDING_PARTY, new MenuItemConfig("Raiding Party", IIconProperty.ACTION_RAIDING_PARTY, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY));
		itemConfigs.put(ClientAction.LOOK_INTO_MY_EYES, new MenuItemConfig("Look Into My Eyes", IIconProperty.ACTION_LOOK_INTO_MY_EYES, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES));
		itemConfigs.put(ClientAction.BALEFUL_HEX, new MenuItemConfig("Baleful Hex", IIconProperty.ACTION_BALEFUL_HEX, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX));
		itemConfigs.put(ClientAction.BLACK_INK, new MenuItemConfig("Black Ink", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_BLACK_INK));
		itemConfigs.put(ClientAction.CATCH_OF_THE_DAY, new MenuItemConfig("Catch of the Day", IIconProperty.ACTION_CATCH_OF_THE_DAY, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY));
		itemConfigs.put(ClientAction.THEN_I_STARTED_BLASTIN,
			new MenuItemConfig(selectedPlayer, NamedProperties.canBlastRemotePlayer, IIconProperty.ACTION_STARTED_BLASTIN, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN));

		return itemConfigs;
	}

	@Override
	protected void postPerform(int menuKey) {
		switch (menuKey) {
			case IPlayerPopupMenuKeys.KEY_RANGE_GRID:
				fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
				fRangeGridHandler.refreshRangeGrid();
				break;
			case IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS:
				if (logicModule.isHailMaryPassActionAvailable()) {
					// logic module sends command to deselect hmp so afterward we have to show the ruler again
					fShowRangeRuler = logicModule.actionIsHmp();
				}
				break;
			default:
				break;
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
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

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS, ClientAction.HAIL_MARY_PASS);
			put(IPlayerPopupMenuKeys.KEY_PASS, ClientAction.PASS);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, ClientAction.CATCH_OF_THE_DAY);
			put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}
}

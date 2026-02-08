package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2025.PuntLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStatePunt extends AbstractClientStateMove<PuntLogicModule> {

	public ClientStatePunt(FantasyFootballClientAwt pClient) {
		super(pClient, new PuntLogicModule(pClient));
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

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PUNT;
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;

		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		determineCursor(result);
		switch (result.getKind()) {
			case DELEGATE:
				selectable = getDelegate(result).mouseOverField(pCoordinate);
				userInterface.getFieldComponent().refresh();
				break;
			default:
				break;
		}

		return selectable;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

		itemConfigs.put(ClientAction.PUNT, new MenuItemConfig("Punt", IIconProperty.ACTION_PUNT, IPlayerPopupMenuKeys.KEY_PUNT));
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
		itemConfigs.put(ClientAction.FUMBLEROOSKIE, new MenuItemConfig("Fumblerooskie", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE));
		itemConfigs.put(ClientAction.AUTO_GAZE_ZOAT, new MenuItemConfig("\"Excuse Me, Are You a Zoat?\"", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT));
		itemConfigs.put(ClientAction.INCORPOREAL, new MenuItemConfig("Incorporeal", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_INCORPOREAL));

		return itemConfigs;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_PUNT, ClientAction.PUNT);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, ClientAction.CATCH_OF_THE_DAY);
			put(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, ClientAction.FUMBLEROOSKIE);
			put(IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT, ClientAction.AUTO_GAZE_ZOAT);
			put(IPlayerPopupMenuKeys.KEY_INCORPOREAL, ClientAction.INCORPOREAL);
		}};
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
	if (pActionKey == ActionKey.PLAYER_ACTION_PUNT) {
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_PUNT);
			return true;
		} else {
			return super.actionKeyPressed(pActionKey, menuIndex);
		}
	}
}

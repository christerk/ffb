package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.HandOverLogicModule;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateHandOver extends AbstractClientStateMove<HandOverLogicModule> {

	public ClientStateHandOver(FantasyFootballClientAwt pClient) {
		super(pClient, new HandOverLogicModule(pClient));
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate catcherPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition, pActionKey);
		Player<?> catcher = game.getFieldModel().getPlayer(catcherPosition);
		if (catcher != null) {
			InteractionResult result = logicModule.playerInteraction(catcher);
			switch (result.getKind()) {
				case HANDLED:
					return true;
				default:
					return false;
			}
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		determineCursor(logicModule.playerPeek(pPlayer));
		return true;
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case DELEGATE:
				getDelegate(result).mouseOverField(pCoordinate);
		}
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = super.influencedItemConfigs();
		Map<ClientAction, MenuItemConfig> handsOver = new HashMap<>();
		influences.put(Influences.HANDS_OVER_TO_ANYONE, handsOver);
		handsOver.put(ClientAction.HAND_OVER, new MenuItemConfig("Regular Hand Over / Move", IIconProperty.ACTION_HAND_OVER, IPlayerPopupMenuKeys.KEY_HAND_OVER));
		return influences;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

		itemConfigs.put(ClientAction.HAND_OVER, new MenuItemConfig("Hand Over Ball (any player)", IIconProperty.ACTION_HAND_OVER, IPlayerPopupMenuKeys.KEY_HAND_OVER));
		itemConfigs.put(ClientAction.JUMP, new MenuItemConfig("Jump", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_JUMP));
		itemConfigs.put(ClientAction.BOUNDING_LEAP, new MenuItemConfig("Jump (Bounding Leap)", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP));
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		itemConfigs.put(ClientAction.TREACHEROUS, new MenuItemConfig("Treacherous", IIconProperty.ACTION_STAB, IPlayerPopupMenuKeys.KEY_TREACHEROUS));
		itemConfigs.put(ClientAction.WISDOM, new MenuItemConfig("Wisdom of the White Dwarf", IIconProperty.ACTION_WISDOM, IPlayerPopupMenuKeys.KEY_WISDOM));
		itemConfigs.put(ClientAction.RAIDING_PARTY, new MenuItemConfig("Raiding Party", IIconProperty.ACTION_RAIDING_PARTY, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY));
		itemConfigs.put(ClientAction.LOOK_INTO_MY_EYES, new MenuItemConfig("Look Into My Eyes", IIconProperty.ACTION_LOOK_INTO_MY_EYES, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES));
		itemConfigs.put(ClientAction.BALEFUL_HEX, new MenuItemConfig("Baleful Hex", IIconProperty.ACTION_BALEFUL_HEX, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX));
		itemConfigs.put(ClientAction.PROJECTILE_VOMIT, new MenuItemConfig("Putrid Regurgitation", IIconProperty.ACTION_VOMIT, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT));
		itemConfigs.put(ClientAction.BLACK_INK, new MenuItemConfig("Black Ink", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_BLACK_INK));
		itemConfigs.put(ClientAction.CATCH_OF_THE_DAY, new MenuItemConfig("Catch of the Day", IIconProperty.ACTION_CATCH_OF_THE_DAY, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY));
		itemConfigs.put(ClientAction.THEN_I_STARTED_BLASTIN, new MenuItemConfig("\"Then I Started Blastin'!\"", IIconProperty.ACTION_STARTED_BLASTIN, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN));

		return itemConfigs;
	}
}

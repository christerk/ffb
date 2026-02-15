package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.SelectGazeTargetLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateSelectGazeTarget extends AbstractClientStateMove<SelectGazeTargetLogicModule> {

	public ClientStateSelectGazeTarget(FantasyFootballClientAwt pClient) {
		super(pClient, new SelectGazeTargetLogicModule(pClient));
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		InteractionResult result = logicModule.playerPeek(pPlayer);
		determineCursor(result);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();

		showShortestPath(game.getFieldModel().getPlayerCoordinate(pPlayer), fieldComponent, actingPlayer);

		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_GAZE;
	}

	@Override
	protected String invalidCursor() {
		return IIconProperty.CURSOR_INVALID_GAZE;
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();

		UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_GAZE);

		showShortestPath(pCoordinate, fieldComponent, actingPlayer);

		return true;
	}

	@Override
	public void clickOnField(FieldCoordinate pCoordinate) {
		// clicks on fields are ignored
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		switch (pActionKey) {
			case PLAYER_SELECT:
				clickOnPlayer(player);
				break;
			case PLAYER_ACTION_END_MOVE:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
				break;
			case PLAYER_ACTION_TREACHEROUS:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
				break;
			case PLAYER_ACTION_WISDOM:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
				break;
			case PLAYER_ACTION_RAIDING_PARTY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
				break;
			case PLAYER_ACTION_LOOK_INTO_MY_EYES:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
				break;
			case PLAYER_ACTION_BALEFUL_HEX:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
				return true;
			case PLAYER_ACTION_BLACK_INK:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
				return true;
			case PLAYER_ACTION_CATCH_OF_THE_DAY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
				return true;
			case PLAYER_ACTION_AUTO_GAZE_ZOAT:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT);
				return true;
			case PLAYER_ACTION_INCORPOREAL:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_INCORPOREAL);
				return true;
			default:
				actionHandled = handleResize(pActionKey);
				break;
		}
		return actionHandled;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, ClientAction.CATCH_OF_THE_DAY);
			put(IPlayerPopupMenuKeys.KEY_AUTO_GAZE_ZOAT, ClientAction.AUTO_GAZE_ZOAT);
			put(IPlayerPopupMenuKeys.KEY_INCORPOREAL, ClientAction.INCORPOREAL);
		}};
	}
}

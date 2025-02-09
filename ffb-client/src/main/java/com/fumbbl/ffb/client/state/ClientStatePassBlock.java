package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogInformation;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.PassBlockLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStatePassBlock extends AbstractClientStateMove<PassBlockLogicModule> {

	private DialogInformation fInfoDialog;

	protected ClientStatePassBlock(FantasyFootballClientAwt pClient) {
		super(pClient, new PassBlockLogicModule(pClient));
	}

	@Override
	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case SELECT_ACTION:
				createAndShowPopupMenuForPlayer(player, result.getActionContext());
				break;
			default:
				evaluateClick(result, player);
				break;
		}
	}

	@Override
	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case HANDLED:
				getClient().getGame().getFieldModel().clearMoveSquares();
				getClient().getUserInterface().getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
		}};
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = new HashMap<>();
		Map<ClientAction, MenuItemConfig> jump = new HashMap<>();
		influences.put(Influences.IS_JUMPING, jump);
		jump.put(ClientAction.JUMP, new MenuItemConfig("Don't Jump", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_JUMP));
		Map<ClientAction, MenuItemConfig> hasActed = new HashMap<>();
		influences.put(Influences.HAS_ACTED, hasActed);
		hasActed.put(ClientAction.END_MOVE, new MenuItemConfig("End Move", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		return influences;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

		itemConfigs.put(ClientAction.MOVE, new MenuItemConfig("Move", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_MOVE));
		itemConfigs.put(ClientAction.JUMP, new MenuItemConfig("Jump", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_JUMP));
		itemConfigs.put(ClientAction.BOUNDING_LEAP, new MenuItemConfig("Jump (Bounding Leap)", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP));
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));

		return itemConfigs;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?> selectedPlayer = getClient().getClientData().getSelectedPlayer();
		switch (pActionKey) {
			case PLAYER_SELECT:
				if (selectedPlayer != null) {
					clickOnPlayer(selectedPlayer);
				}
				break;
			case PLAYER_CYCLE_RIGHT:
				selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, true);
				if (selectedPlayer != null) {
					hideSelectSquare();
					FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
					showSelectSquare(selectedCoordinate);
					getClient().getClientData().setSelectedPlayer(selectedPlayer);
					userInterface.refreshSideBars();
				}
				break;
			case PLAYER_CYCLE_LEFT:
				selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, false);
				if (selectedPlayer != null) {
					hideSelectSquare();
					FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
					showSelectSquare(selectedCoordinate);
					getClient().getClientData().setSelectedPlayer(selectedPlayer);
					userInterface.refreshSideBars();
				}
				break;
			case PLAYER_ACTION_MOVE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MOVE);
				break;
			case PLAYER_ACTION_JUMP:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_JUMP);
				break;
			case PLAYER_ACTION_BOUNDING_LEAP:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
				return true;
			default:
				actionHandled = handleResize(pActionKey);
				break;
		}
		return actionHandled;
	}

	@Override
	public void postEndTurn() {

		if (logicModule.isTurnEnding()) {
			SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
			sideBarHome.refresh();
		} else {
			fInfoDialog = new DialogInformation(getClient(), "End Turn not possible",
				new String[]{"You cannot end the turn before the acting player has reached a valid destination!"},
				DialogInformation.OK_DIALOG, IIconProperty.GAME_REF);
			fInfoDialog.showDialog(pDialog -> fInfoDialog.hideDialog());
		}
	}

}

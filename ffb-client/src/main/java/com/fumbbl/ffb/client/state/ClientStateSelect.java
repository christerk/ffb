package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.SelectLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateSelect extends ClientStateAwt<SelectLogicModule> {

	protected ClientStateSelect(FantasyFootballClientAwt pClient) {
		super(pClient, new SelectLogicModule(pClient));
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case SELECT_ACTION:
				createAndShowPopupMenuForPlayer(pPlayer, result.getActionContext());
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
			put(IPlayerPopupMenuKeys.KEY_BLITZ, ClientAction.BLITZ);
			put(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH, ClientAction.FRENZIED_RUSH);
			put(IPlayerPopupMenuKeys.KEY_FOUL, ClientAction.FOUL);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_STAND_UP, ClientAction.STAND_UP);
			put(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ, ClientAction.STAND_UP_BLITZ);
			put(IPlayerPopupMenuKeys.KEY_HAND_OVER, ClientAction.HAND_OVER);
			put(IPlayerPopupMenuKeys.KEY_PASS, ClientAction.PASS);
			put(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, ClientAction.THROW_TEAM_MATE);
			put(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, ClientAction.KICK_TEAM_MATE);
			put(IPlayerPopupMenuKeys.KEY_RECOVER, ClientAction.RECOVER);
			put(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK, ClientAction.MULTIPLE_BLOCK);
			put(IPlayerPopupMenuKeys.KEY_BOMB, ClientAction.BOMB);
			put(IPlayerPopupMenuKeys.KEY_GAZE, ClientAction.GAZE);
			put(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT, ClientAction.GAZE_ZOAT);
			put(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING, ClientAction.SHOT_TO_NOTHING);
			put(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB, ClientAction.SHOT_TO_NOTHING_BOMB);
			put(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH, ClientAction.BEER_BARREL_BASH);
			put(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT, ClientAction.ALL_YOU_CAN_EAT);
			put(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK, ClientAction.KICK_EM_BLOCK);
			put(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ, ClientAction.KICK_EM_BLITZ);
			put(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE, ClientAction.THE_FLASHING_BLADE);
		}};
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer, ActionContext actionContext) {
		List<JMenuItem> menuItemList = menuBuilder.populateMenu(actionContext);
		if (!menuItemList.isEmpty()) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
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
			case PLAYER_ACTION_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BLOCK);
				break;
			case PLAYER_ACTION_MOVE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MOVE);
				break;
			case PLAYER_ACTION_BLITZ:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BLITZ);
				break;
			case PLAYER_ACTION_FRENZIED_RUSH:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
				break;
			case PLAYER_ACTION_FOUL:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FOUL);
				break;
			case PLAYER_ACTION_STAND_UP:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_STAND_UP);
				break;
			case PLAYER_ACTION_HAND_OVER:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_HAND_OVER);
				break;
			case PLAYER_ACTION_PASS:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_PASS);
				break;
			case PLAYER_ACTION_MULTIPLE_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
				break;
			case PLAYER_ACTION_GAZE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_GAZE);
				break;
			case PLAYER_ACTION_GAZE_ZOAT:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_GAZE_ZOAT);
				break;
			case PLAYER_ACTION_SHOT_TO_NOTHING:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING);
				break;
			case PLAYER_ACTION_SHOT_TO_NOTHING_BOMB:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB);
				break;
			case PLAYER_ACTION_BEER_BARREL_BASH:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH);
				break;
			case PLAYER_ACTION_ALL_YOU_CAN_EAT:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT);
				break;
			case PLAYER_ACTION_KICK_EM_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK);
				break;
			case PLAYER_ACTION_KICK_EM_BLITZ:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ);
				break;
			case PLAYER_ACTION_THE_FLASHING_BLADE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE);
				break;
			default:
				actionHandled = super.actionKeyPressed(pActionKey);
				break;
		}
		return actionHandled;
	}

	@Override
	public void postEndTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

}

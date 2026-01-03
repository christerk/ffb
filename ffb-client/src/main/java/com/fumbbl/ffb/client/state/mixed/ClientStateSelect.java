package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.mixed.SelectLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.*;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateSelect extends ClientStateAwt<SelectLogicModule> {

	public ClientStateSelect(FantasyFootballClientAwt pClient) {
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
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
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
			put(IPlayerPopupMenuKeys.KEY_VICIOUS_VINES, ClientAction.VICIOUS_VINES);
			put(IPlayerPopupMenuKeys.KEY_FURIOUS_OUTBURST, ClientAction.FURIOUS_OUTBURST);
		}};
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
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
			case PLAYER_ACTION_VICIOUS_VINES:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_VICIOUS_VINES);
				break;
			case PLAYER_ACTION_FURIOUS_OUTBURST:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FURIOUS_OUTBURST);
				break;
			default:
				actionHandled = super.actionKeyPressed(pActionKey, menuIndex);
				break;
		}
		return actionHandled;
	}

	@Override
	public void postEndTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

	private String blockActionLabel(List<Skill> blockActions) {
		List<String> actions = new ArrayList<>();
		actions.add("Block Action");
		blockActions.stream().map(Skill::getName).forEach(actions::add);
		return String.join("/", actions);
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {

		LinkedHashMap<ClientAction, MenuItemConfig> configs = new LinkedHashMap<>();

		configs.put(ClientAction.BLOCK, new MenuItemConfig(blockActionLabel(actionContext.getBlockAlternatives()), IIconProperty.ACTION_BLOCK, IPlayerPopupMenuKeys.KEY_BLOCK));
		configs.put(ClientAction.MULTIPLE_BLOCK, new MenuItemConfig("Multiple Block", IIconProperty.ACTION_MUTIPLE_BLOCK, IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK));
		configs.put(ClientAction.BOMB, new MenuItemConfig("Throw Bomb Action", IIconProperty.ACTION_BOMB, IPlayerPopupMenuKeys.KEY_BOMB));
		configs.put(ClientAction.SHOT_TO_NOTHING_BOMB, new MenuItemConfig("Shot To Nothing Bomb", IIconProperty.ACTION_BOMB, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB));
		configs.put(ClientAction.GAZE, new MenuItemConfig("Hypnotic Gaze", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_GAZE));
		configs.put(ClientAction.GAZE_ZOAT, new MenuItemConfig("Hypnotic Gaze (Zoat)", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_GAZE_ZOAT));
		configs.put(ClientAction.MOVE, new MenuItemConfig("Move Action", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_MOVE));
		configs.put(ClientAction.BLITZ, new MenuItemConfig("Blitz Action", IIconProperty.ACTION_BLITZ, IPlayerPopupMenuKeys.KEY_BLITZ));
		configs.put(ClientAction.FRENZIED_RUSH, new MenuItemConfig("Frenzied Rush Blitz", IIconProperty.ACTION_BLITZ, IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH));
		configs.put(ClientAction.FOUL, new MenuItemConfig("Foul Action", IIconProperty.ACTION_FOUL, IPlayerPopupMenuKeys.KEY_FOUL));
		configs.put(ClientAction.PASS, new MenuItemConfig("Pass Action", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_PASS));
		configs.put(ClientAction.SHOT_TO_NOTHING, new MenuItemConfig("Shot To Nothing", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING));
		configs.put(ClientAction.HAND_OVER, new MenuItemConfig("Hand Over Action", IIconProperty.ACTION_HAND_OVER, IPlayerPopupMenuKeys.KEY_HAND_OVER));
		configs.put(ClientAction.THROW_TEAM_MATE, new MenuItemConfig("Throw Team-Mate Action", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE));
		configs.put(ClientAction.KICK_TEAM_MATE, new MenuItemConfig("Kick Team-Mate Action", IIconProperty.ACTION_BLITZ, IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE));
		configs.put(ClientAction.BEER_BARREL_BASH, new MenuItemConfig("Beer Barrel Bash", IIconProperty.ACTION_BEER_BARREL_BASH, IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH));
		configs.put(ClientAction.ALL_YOU_CAN_EAT, new MenuItemConfig("All You Can Eat", IIconProperty.ACTION_ALL_YOU_CAN_EAT, IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT));
		configs.put(ClientAction.KICK_EM_BLOCK, new MenuItemConfig("Kick 'em while they are down! (Block)", IIconProperty.ACTION_KICK_EM_BLOCK, IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK));
		configs.put(ClientAction.KICK_EM_BLITZ, new MenuItemConfig("Kick 'em while they are down! (Blitz)", IIconProperty.ACTION_KICK_EM_BLITZ, IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ));
		configs.put(ClientAction.THE_FLASHING_BLADE, new MenuItemConfig("The Flashing Blade", IIconProperty.ACTION_THE_FLASHING_BLADE, IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE));
		configs.put(ClientAction.VICIOUS_VINES, new MenuItemConfig("Vicious Vines", IIconProperty.ACTION_VICIOUS_VINES, IPlayerPopupMenuKeys.KEY_VICIOUS_VINES));
		configs.put(ClientAction.FURIOUS_OUTBURST, new MenuItemConfig("Furious Outburst", IIconProperty.ACTION_FURIOUS_OUTBURST, IPlayerPopupMenuKeys.KEY_FURIOUS_OUTBURST));
		configs.put(ClientAction.RECOVER, new MenuItemConfig("Recover tackle zone & End Move", IIconProperty.ACTION_STAND_UP, IPlayerPopupMenuKeys.KEY_RECOVER));
		configs.put(ClientAction.STAND_UP_BLITZ, new MenuItemConfig("Stand Up & End Move (using Blitz)", IIconProperty.ACTION_STAND_UP, IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ));
		configs.put(ClientAction.STAND_UP, new MenuItemConfig("Stand Up & End Move", IIconProperty.ACTION_STAND_UP, IPlayerPopupMenuKeys.KEY_STAND_UP));

		return configs;
	}

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> configs = new HashMap<>();
		Map<ClientAction, MenuItemConfig> treacherous = new HashMap<>();
		configs.put(Influences.BALL_ACTIONS_DUE_TO_TREACHEROUS, treacherous);
		treacherous.put(ClientAction.PASS, new MenuItemConfig("Pass Action (Treacherous)", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_PASS));
		treacherous.put(ClientAction.SHOT_TO_NOTHING, new MenuItemConfig("Shot To Nothing (Treacherous)", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING));
		treacherous.put(ClientAction.HAND_OVER, new MenuItemConfig("Hand Over Action (Treacherous)", IIconProperty.ACTION_HAND_OVER, IPlayerPopupMenuKeys.KEY_HAND_OVER));
		return configs;
	}
}

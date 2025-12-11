package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.common.ClientStateBlockExtension;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.MenuItemConfig;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2020.BlockKindLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateSelectBlockKind extends ClientStateAwt<BlockKindLogicModule> {

	private final ClientStateBlockExtension extension = new ClientStateBlockExtension();

	public ClientStateSelectBlockKind(FantasyFootballClientAwt pClient) {
		super(pClient, new BlockKindLogicModule(pClient));
	}

	@Override
	public void setUp() {
		super.setUp();
		showMenu();
	}

	@Override
	public void clickOnField(FieldCoordinate pCoordinate) {
		showMenu();
	}

	@Override
	public void clickOnPlayer(Player<?> pPlayer) {
		showMenu();
	}

	private void showMenu() {
		Game game = getClient().getGame();
		if (game.isHomePlaying()) {
			InteractionResult result = logicModule.playerInteraction(null);
			switch (result.getKind()) {
				case SELECT_ACTION:
					createAndShowPopupMenuForPlayer(game.getDefender(), result.getActionContext());
					break;
				default:
					break;
			}
		}
	}


	@Override
	protected void prePerform(int menuKey) {
		Game game = getClient().getGame();
		if (game.isHomePlaying()) {
			getClient().getUserInterface().getFieldComponent().refresh();
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
			return new HashMap<Integer, ClientAction>() {{
				put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
				put(IPlayerPopupMenuKeys.KEY_STAB, ClientAction.STAB);
				put(IPlayerPopupMenuKeys.KEY_CHAINSAW, ClientAction.CHAINSAW);
				put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
				put(IPlayerPopupMenuKeys.KEY_BREATHE_FIRE, ClientAction.BREATHE_FIRE);
				put(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, ClientAction.GORED_BY_THE_BULL);
			}};
		}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		return extension.itemConfigs();
	}

	@Override
	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
		Game game = getClient().getGame();
		if (!game.isHomePlaying()) {
			return false;
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_STAB:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_STAB);
				break;
			case PLAYER_ACTION_CHAINSAW:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_CHAINSAW);
				break;
			case PLAYER_ACTION_PROJECTILE_VOMIT:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
				break;
			case PLAYER_ACTION_BLOCK:
			case PLAYER_ACTION_GORED:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_BLOCK);
				break;
			case PLAYER_ACTION_BREATHE_FIRE:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_BREATHE_FIRE);
				break;
			default:
				return super.actionKeyPressed(pActionKey);
		}
		return true;
	}
}

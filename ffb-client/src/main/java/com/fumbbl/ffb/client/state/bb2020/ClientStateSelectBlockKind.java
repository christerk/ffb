package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.BlockKindLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.Map;

public class ClientStateSelectBlockKind extends ClientStateAwt<BlockKindLogicModule> {

	public ClientStateSelectBlockKind(FantasyFootballClientAwt pClient) {
		super(pClient, new BlockKindLogicModule(pClient));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SELECT_BLOCK_KIND;
	}

	@Override
	public void enterState() {
		super.enterState();
		Game game = getClient().getGame();
		if (game.isHomePlaying()) {
			UtilClientStateBlocking.createAndShowBlockOptionsPopupMenu(this, game.getActingPlayer().getPlayer(), game.getDefender(), false);
		}
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		if (game.isHomePlaying()) {
			UtilClientStateBlocking.createAndShowBlockOptionsPopupMenu(this, game.getActingPlayer().getPlayer(), game.getDefender(), false);
		}
	}

	@Override
	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		if (game.isHomePlaying()) {
			UtilClientStateBlocking.createAndShowBlockOptionsPopupMenu(this, game.getActingPlayer().getPlayer(), game.getDefender(), false);
		}
	}

	@Override
	protected void prePerform() {
		Game game = getClient().getGame();
		if (game.isHomePlaying()) {
			getClient().getUserInterface().getFieldComponent().refresh();
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
			return new HashMap<Integer, ClientAction>() {{
				put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
				put(IPlayerPopupMenuKeys.KEY_STAB, ClientAction.STAB);
				put(IPlayerPopupMenuKeys.KEY_CHAINSAW, ClientAction.CHAINSAW);
				put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
				put(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, ClientAction.GORED_BY_THE_BULL);
			}};
		}

	@Override
	public boolean actionKeyPressed(ActionKey pActionKey) {
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
			default:
				return super.actionKeyPressed(pActionKey);
		}
		return true;
	}
}

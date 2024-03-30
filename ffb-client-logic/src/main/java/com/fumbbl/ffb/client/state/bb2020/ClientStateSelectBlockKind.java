package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

public class ClientStateSelectBlockKind extends ClientState {

	public ClientStateSelectBlockKind(FantasyFootballClient pClient) {
		super(pClient);
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
	public void leaveState() {
		super.leaveState();
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

	public void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			Game game = getClient().getGame();
			if (!game.isHomePlaying()) {
				return;
			}
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_BLOCK:
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, false, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAB:
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, true, false, false);
					break;
				case IPlayerPopupMenuKeys.KEY_CHAINSAW:
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, true, false);
					break;
				case IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT:
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, false, true);
					break;
				case IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL:
					Skill goredSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie);
					if (UtilClientStateBlocking.isGoredAvailable(this) && goredSkill != null) {
						getClient().getCommunication().sendUseSkill(goredSkill, true, actingPlayer.getPlayerId());
					}
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, false, false);
					break;
				default:
					break;
			}
		}
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
			default:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_BLOCK);
				break;
		}
		return true;
	}
}

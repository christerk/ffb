package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * @author Kalimar
 */
public class ClientStateBlitz extends ClientStateMove {

	private boolean putridRegurgitationActivated;
	protected ClientStateBlitz(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.BLITZ;
	}

	public void enterState() {
		super.enterState();
	}

	@Override
	public void leaveState() {
		super.leaveState();
		putridRegurgitationActivated = false;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				createAndShowPopupMenuForActingPlayer();
			} else {
				if (!actingPlayer.hasBlocked()) {
					UtilClientStateBlocking.showPopupOrBlockPlayer(this, pPlayer, true);
				} else if (putridRegurgitationActivated) {
					putridRegurgitationActivated = false;
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, false, true);
				}
			}
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isBlockable(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = UtilClientStateBlocking.actionKeyPressed(this, pActionKey, true);
		if (!actionHandled) {
			actionHandled = super.actionKeyPressed(pActionKey);
		}
		return actionHandled;
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			ClientCommunication communication = getClient().getCommunication();
			Skill putridSkill = pPlayer.getSkillWithProperty(NamedProperties.canUseVomitAfterBlock);
			if (putridRegurgitationActivated) {
				communication.sendUseSkill(putridSkill, false, pPlayer.getId());
				putridRegurgitationActivated = false;
			}
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					communication.sendActingPlayer(null, null, false);
					break;
				case IPlayerPopupMenuKeys.KEY_JUMP:
					if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_MOVE:
					if (actingPlayer.isSufferingBloodLust()) {
						getClient().getCommunication().sendActingPlayer(pPlayer, PlayerAction.BLITZ_MOVE, actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE:
					communication.sendUseFumblerooskie();
					break;
				case IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT:
					if (isPutridRegurgitationAvailable()) {
						putridRegurgitationActivated = true;
						communication.sendUseSkill(putridSkill, true, pPlayer.getId());
					}
					break;
				default:
					UtilClientStateBlocking.menuItemSelected(this, pPlayer, pMenuKey);
					break;
			}
		}
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates) {
		getClient().getCommunication().sendPlayerBlitzMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
	}

	@Override
	protected boolean isPutridRegurgitationAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return actingPlayer.hasBlocked() && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(game.getActingTeam()), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())));
	}
}

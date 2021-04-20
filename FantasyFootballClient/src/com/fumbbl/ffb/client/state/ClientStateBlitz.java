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
import com.fumbbl.ffb.util.UtilPlayer;

/**
 *
 * @author Kalimar
 */
public class ClientStateBlitz extends ClientStateMove {

	protected ClientStateBlitz(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.BLITZ;
	}

	public void enterState() {
		super.enterState();
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

//  public void handleNetCommand(NetCommand pNetCommand) {
//    switch (pNetCommand.getId()) {
//      case SERVER_MOVE:
//        getClient().getGame().getTurnData().setBlitzUsed(true);
//        if (UtilBlock.updateDiceDecorations(getClient().getGame())) {
//          getClient().getUserInterface().getFieldComponent().refresh();
//        }
//        break;
//      case SERVER_BLOCK:
//        getClient().getGame().getTurnData().setBlitzUsed(true);
//        break;
//    }
//  }

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = UtilClientStateBlocking.actionKeyPressed(this, pActionKey, true);
		if (!actionHandled) {
			actionHandled = super.actionKeyPressed(pActionKey);
		}
		return actionHandled;
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				communication.sendActingPlayer(null, null, false);
				break;
			case IPlayerPopupMenuKeys.KEY_JUMP:
				if (isJumpAvailableAsNextMove(game, actingPlayer,false)) {
					communication.sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_MOVE:
				if (actingPlayer.isSufferingBloodLust()) {
					getClient().getCommunication().sendActingPlayer(pPlayer, PlayerAction.BLITZ_MOVE, actingPlayer.isJumping());
				}
				break;
			default:
				UtilClientStateBlocking.menuItemSelected(this, pPlayer, pMenuKey);
				break;
			}
		}
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates){
		getClient().getCommunication().sendPlayerBlitzMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
	}
}

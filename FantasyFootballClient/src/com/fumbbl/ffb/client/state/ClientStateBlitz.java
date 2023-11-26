package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
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
						getClient().getCommunication().sendActingPlayer(pPlayer, moveAction(), actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE:
					communication.sendUseFumblerooskie();
					break;
				case IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL:
					if (isGoredAvailable()) {
						Skill goredSkill = pPlayer.getSkillWithProperty(NamedProperties.canAddBlockDie);
						communication.sendUseSkill(goredSkill, true, pPlayer.getId());
					}
					break;
				default:
					UtilClientStateBlocking.menuItemSelected(this, pPlayer, pMenuKey);
					break;
			}
		}
	}

	@Override
	protected boolean playerActivationUsed() {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel.getTargetSelectionState() == null) {
			return super.playerActivationUsed();
		}
		return fieldModel.getTargetSelectionState().isCommitted();
	}

	@Override
	protected boolean isGoredAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie)) {
			FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetSelectionState.getSelectedPlayerId()));
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			DiceDecoration diceDecoration = game.getFieldModel().getDiceDecoration(targetCoordinate);
			return diceDecoration != null && (diceDecoration.getNrOfDice() == 1 || diceDecoration.getNrOfDice() == 2) && targetCoordinate.isAdjacent(playerCoordinate);
		}

		return false;
	}

	@Override
	protected JMenuItem createGoredItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Gored By The Bull",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, 0));
		return menuItem;
	}

	protected PlayerAction moveAction() {
		return PlayerAction.BLITZ_MOVE;
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates) {
		getClient().getCommunication().sendPlayerBlitzMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
	}
}

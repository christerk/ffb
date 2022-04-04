package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.JMenuItem;
import java.util.ArrayList;
import java.util.List;

public class ClientStateThrowKeg extends ClientState {

	protected ClientStateThrowKeg(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.THROW_KEG;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			createAndShowPopupMenuForActingPlayer();
		} else {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			clickOnField(playerCoordinate);
		}
	}

	protected boolean mouseOverPlayer(Player<?> player) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		getClient().getClientData().setSelectedPlayer(player);
		userInterface.refreshSideBars();

		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate actingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		int distance = playerCoordinate.distanceInSteps(actingPlayerCoordinate);

		PlayerState playerState = game.getFieldModel().getPlayerState(player);

		if (distance <= 3 && playerState.getBase() == PlayerState.STANDING) {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_KEG);
		} else {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_KEG);
		}

		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_KEG);
		return super.mouseOverField(pCoordinate);
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (isEndPlayerActionAvailable()) {
			addEndActionLabel(iconCache, menuItemList, actingPlayer);
		}

		if (isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
		}
		if (isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		ClientCommunication communication = getClient().getCommunication();
		switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				if (isEndPlayerActionAvailable()) {
					communication.sendActingPlayer(null, null, false);
				}
				break;
			case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
				Skill skill = pPlayer.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
				communication.sendUseSkill(skill, true, pPlayer.getId());
				break;
			case IPlayerPopupMenuKeys.KEY_WISDOM:
				getClient().getCommunication().sendUseWisdom();
				break;
			default:
				break;
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_END_MOVE:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
				return true;
			case PLAYER_ACTION_TREACHEROUS:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
				return true;
			case PLAYER_ACTION_WISDOM:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
				return true;
			default:
				return super.actionKeyPressed(pActionKey);
		}
	}

	private boolean isEndPlayerActionAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !actingPlayer.hasActed();
	}
}

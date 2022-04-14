package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.JMenuItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientStateThrowKeg extends ClientState {

	protected ClientStateThrowKeg(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.THROW_KEG;
	}

	@Override
	public void enterState() {
		super.enterState();
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		MoveSquare[] squares = Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(player), FieldCoordinateBounds.FIELD,
			3, false)).map(fieldCoordinate -> new MoveSquare(fieldCoordinate, 0, 0)).toArray(MoveSquare[]::new);
		fieldModel.add(squares);
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	@Override
	public void leaveState() {
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		fieldModel.clearMoveSquares();
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	protected void clickOnPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			createAndShowPopupMenuForActingPlayer();
		} else {
			if (isValidTarget(player, game)) {
				getClient().getCommunication().sendThrowKeg(player);
			}
		}
	}

	protected boolean mouseOverPlayer(Player<?> player) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		getClient().getClientData().setSelectedPlayer(player);
		userInterface.refreshSideBars();
		if (isValidTarget(player, game)) {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_KEG);
		} else {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_INVALID_KEG);
		}

		return true;
	}

	private boolean isValidTarget(Player<?> player, Game game) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate actingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		int distance = playerCoordinate.distanceInSteps(actingPlayerCoordinate);

		PlayerState playerState = game.getFieldModel().getPlayerState(player);


		return distance <= 3 && playerState.getBase() == PlayerState.STANDING && player.getTeam() != game.getActingTeam();
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
		if (isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		ClientCommunication communication = getClient().getCommunication();
		switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				if (isEndPlayerActionAvailable()) {
					communication.sendActingPlayer(null, null, false);
				}
				break;
			case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
				if (isTreacherousAvailable(player)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
					communication.sendUseSkill(skill, true, player.getId());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_WISDOM:
				if (isWisdomAvailable(player)) {
					getClient().getCommunication().sendUseWisdom();
				}
				break;
			case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
				if (isRaidingPartyAvailable(player)) {
					Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
					getClient().getCommunication().sendUseSkill(raidingSkill, true, player.getId());
				}
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
			case PLAYER_ACTION_RAIDING_PARTY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
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

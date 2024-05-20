package com.fumbbl.ffb.client.state.bb2020;

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
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientStateThrowKeg extends ClientState {

	public ClientStateThrowKeg(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.THROW_KEG;
	}

	@Override
	public void initUI() {
		super.initUI();
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
			addEndActionLabel(iconCache, menuItemList);
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
		if (isLookIntoMyEyesAvailable(actingPlayer)) {
			menuItemList.add(createLookIntoMyEyesItem(iconCache));
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		if (isBlackInkAvailable(actingPlayer)) {
			menuItemList.add(createBlackInkItem(iconCache));
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			menuItemList.add(createCatchOfTheDayItem(iconCache));
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
					communication.sendUseWisdom();
				}
				break;
			case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
				if (isRaidingPartyAvailable(player)) {
					Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
					communication.sendUseSkill(raidingSkill, true, player.getId());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES:
				if (isLookIntoMyEyesAvailable(player)) {
					UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
						.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, player.getId()));
				}
				break;
			case IPlayerPopupMenuKeys.KEY_BALEFUL_HEX:
				if (isBalefulHexAvailable(player)) {
					Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
					communication.sendUseSkill(balefulSkill, true, player.getId());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_BLACK_INK:
				if (isBlackInkAvailable(player)) {
					Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
					communication.sendUseSkill(blackInkSkill, true, player.getId());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY:
				if (isCatchOfTheDayAvailable(player)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
					communication.sendUseSkill(skill, true, player.getId());
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
			case PLAYER_ACTION_LOOK_INTO_MY_EYES:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
				return true;
			case PLAYER_ACTION_BALEFUL_HEX:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
				return true;
			case PLAYER_ACTION_BLACK_INK:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
				return true;
			case PLAYER_ACTION_CATCH_OF_THE_DAY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
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

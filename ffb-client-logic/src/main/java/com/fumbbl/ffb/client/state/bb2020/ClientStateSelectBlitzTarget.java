package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientStateMove;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClientStateSelectBlitzTarget extends ClientStateMove {

	public ClientStateSelectBlitzTarget(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_BLITZ_TARGET;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer.equals(actingPlayer.getPlayer()) && isSpecialAbilityAvailable(actingPlayer)) {
			createAndShowPopupMenuForActingPlayer();
		} else if (pPlayer.equals(actingPlayer.getPlayer()) || (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer))) {
			getClient().getCommunication().sendTargetSelected(pPlayer.getId());
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isValidBlitzTarget(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_BLOCK);
		}

		showShortestPath(game.getFieldModel().getPlayerCoordinate(pPlayer), game, fieldComponent, actingPlayer);

		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();

		UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_INVALID_BLOCK);

		showShortestPath(pCoordinate, game, fieldComponent, actingPlayer);

		return true;
	}

	protected void createAndShowPopupMenuForActingPlayer() {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();
		String endMoveActionLabel = "Deselect Player";
		JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), endMoveActionLabel,
			createMenuIcon(iconCache, IIconProperty.ACTION_END_MOVE), RenderContext.ON_PITCH);
		endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
		endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
		menuItemList.add(endMoveAction);

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
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			menuItemList.add(createThenIStartedBlastinItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());
	}


	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		switch (pActionKey) {
			case PLAYER_SELECT:
				createAndShowPopupMenuForActingPlayer();
				break;
			case PLAYER_ACTION_END_MOVE:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
				break;
			case PLAYER_ACTION_TREACHEROUS:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
				break;
			case PLAYER_ACTION_WISDOM:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
				break;
			case PLAYER_ACTION_RAIDING_PARTY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
				break;
			case PLAYER_ACTION_LOOK_INTO_MY_EYES:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
				break;
			case PLAYER_ACTION_BALEFUL_HEX:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
				return true;
			case PLAYER_ACTION_BLACK_INK:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
				return true;
			case PLAYER_ACTION_CATCH_OF_THE_DAY:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
				return true;
			case PLAYER_ACITON_THEN_I_STARTED_BLASTIN:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
				return true;
			default:
				actionHandled = false;
				break;
		}
		return actionHandled;
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (player != null) {
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					getClient().getCommunication().sendTargetSelected(player.getId());
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
						getClient().getCommunication().sendUseSkill(raidingSkill, true, player.getId());
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

				case IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN:
					if (isThenIStartedBlastinAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		// clicks on fields are ignored
	}
}

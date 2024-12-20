package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
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

/**
 * @author Kalimar
 */
public class ClientStateFoul extends ClientStateMove {

	protected ClientStateFoul(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.FOUL;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			if (actingPlayer.isSufferingBloodLust()) {
				createAndShowPopupMenuForBloodLustPlayer();
			} else {
				super.clickOnPlayer(pPlayer);
			}
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				createAndShowPopupMenuForActingPlayer();
			} else {
				playerSelected(pPlayer);
			}
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate defenderPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition,
			pActionKey);
		Player<?> defender = game.getFieldModel().getPlayer(defenderPosition);
		if (defender != null) {
			actionHandled = playerSelected(defender);
		} else {
			actionHandled = super.actionKeyPressed(pActionKey);
		}

		return actionHandled;
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		Game game = getClient().getGame();
		if (UtilPlayer.isFoulable(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_FOUL);
		}
		return true;
	}

	private boolean playerSelected(Player<?> defender) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean doFoul = UtilPlayer.isFoulable(game, defender);
		if (doFoul) {
			if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.providesFoulingAlternative)) {
				createAndShowBlockOptionsPopupMenu(actingPlayer.getPlayer(), defender);
			} else {
				foul(defender, false);
			}
		}
		return doFoul;
	}

	private void foul(Player<?> defender, boolean usingChainsaw) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		getClient().getCommunication().sendFoul(actingPlayer.getPlayerId(), defender, usingChainsaw);
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (player != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					communication.sendActingPlayer(null, null, false);
					break;
				case IPlayerPopupMenuKeys.KEY_JUMP:
					if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(player, actingPlayer.getPlayerAction(),
							!actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_MOVE:
					if (actingPlayer.isSufferingBloodLust()) {
						communication.sendActingPlayer(player, PlayerAction.MOVE, actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_FOUL:
					foul(player, false);
					break;
				case IPlayerPopupMenuKeys.KEY_CHAINSAW:
					foul(player, true);
					break;
				case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
					if (isTreacherousAvailable(actingPlayer)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_WISDOM:
					if (isWisdomAvailable(actingPlayer)) {
						communication.sendUseWisdom();
					}
					break;
				case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
					if (isRaidingPartyAvailable(actingPlayer)) {
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
					if (isBalefulHexAvailable(actingPlayer)) {
						Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
						communication.sendUseSkill(balefulSkill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BLACK_INK:
					if (isBlackInkAvailable(actingPlayer)) {
						Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
						communication.sendUseSkill(blackInkSkill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY:
					if (isCatchOfTheDayAvailable(actingPlayer)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP:
					isBoundingLeapAvailable(game, actingPlayer).ifPresent(skill ->
						communication.sendUseSkill(skill, true, actingPlayer.getPlayerId()));
					break;
				case IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN:
					if (isThenIStartedBlastinAvailable(actingPlayer)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				default:
					break;
			}
		}
	}

	private void createAndShowBlockOptionsPopupMenu(Player<?> attacker, Player<?> defender) {
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		if (attacker.hasSkillProperty(NamedProperties.providesChainsawFoulingAlternative)) {
			JMenuItem chainsawAction = new JMenuItem(dimensionProvider(), "Chainsaw",
				createMenuIcon(iconCache, IIconProperty.ACTION_CHAINSAW));
			chainsawAction.setMnemonic(IPlayerPopupMenuKeys.KEY_CHAINSAW);
			chainsawAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CHAINSAW, 0));
			menuItemList.add(chainsawAction);
		}
		JMenuItem foulAction = new JMenuItem(dimensionProvider(), "Foul Opponent",
			createMenuIcon(iconCache, IIconProperty.ACTION_FOUL));
		foulAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FOUL);
		foulAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FOUL, 0));
		menuItemList.add(foulAction);
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(defender);
	}

	protected void createAndShowPopupMenuForBloodLustPlayer() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.isSufferingBloodLust()) {
			UserInterface userInterface = getClient().getUserInterface();
			IconCache iconCache = userInterface.getIconCache();
			userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
			List<JMenuItem> menuItemList = new ArrayList<>();
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
				createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
			JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), "End Move",
				createMenuIcon(iconCache, IIconProperty.ACTION_END_MOVE));
			endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
			endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
			menuItemList.add(endMoveAction);
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(actingPlayer.getPlayer());
		}
	}

}

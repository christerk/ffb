package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kalimar
 */
public class UtilClientStateBlocking {

	public static boolean actionKeyPressed(ClientState pClientState, ActionKey pActionKey, boolean pDoBlitz) {
		boolean actionHandled;
		Game game = pClientState.getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_BLOCK:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_BLOCK);
				actionHandled = true;
				break;
			case PLAYER_ACTION_STAB:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_STAB);
				actionHandled = true;
				break;
			case PLAYER_ACTION_CHAINSAW:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_CHAINSAW);
				actionHandled = true;
				break;
			case PLAYER_ACTION_PROJECTILE_VOMIT:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
				actionHandled = true;
				break;
			case PLAYER_ACTION_TREACHEROUS:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
				actionHandled = true;
				break;
			case PLAYER_ACTION_WISDOM:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_WISDOM);
				actionHandled = true;
				break;
			case PLAYER_ACTION_RAIDING_PARTY:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
				actionHandled = true;
				break;
			case PLAYER_ACTION_BALEFUL_HEX:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
				actionHandled = true;
				break;
			case PLAYER_ACTION_GORED:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
				return true;
			case PLAYER_ACTION_BLACK_INK:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
				return true;
			case PLAYER_ACITON_THEN_I_STARTED_BLASTIN:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
				return true;
			case PLAYER_ACTION_BREATHE_FIRE:
				menuItemSelected(pClientState, player, IPlayerPopupMenuKeys.KEY_BREATHE_FIRE);
				return true;
			default:
				FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(player);
				FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(playerPosition,
					pActionKey);
				Player<?> defender = game.getFieldModel().getPlayer(moveCoordinate);
				actionHandled = showPopupOrBlockPlayer(pClientState, defender, pDoBlitz);
				break;
		}
		return actionHandled;
	}

	public static boolean menuItemSelected(ClientState pClientState, Player<?> pPlayer, int pMenuKey) {
		boolean handled = false;
		if (pPlayer != null) {
			Game game = pClientState.getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = pClientState.getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_BLOCK:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, false, false, false, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAB:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, true, false, false, false);
					break;
				case IPlayerPopupMenuKeys.KEY_CHAINSAW:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, false, true, false, false);
					break;
				case IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, false, false, true, false);
					break;
				case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
					Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
					communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
					break;
				case IPlayerPopupMenuKeys.KEY_WISDOM:
					communication.sendUseWisdom();
					break;
				case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
					Skill raidingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
					communication.sendUseSkill(raidingSkill, true, actingPlayer.getPlayerId());
					break;
				case IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES:
					UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canStealBallFromOpponent)
						.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, actingPlayer.getPlayerId()));
					break;
				case IPlayerPopupMenuKeys.KEY_BALEFUL_HEX:
					Skill balefulSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
					communication.sendUseSkill(balefulSkill, true, actingPlayer.getPlayerId());
					break;
				case IPlayerPopupMenuKeys.KEY_BLACK_INK:
					Skill blackInk = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically);
					communication.sendUseSkill(blackInk, true, actingPlayer.getPlayerId());
					break;
				case IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL:
					if (isGoredAvailable(pClientState)) {
						UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canAddBlockDie).ifPresent(goredSkill ->
							communication.sendUseSkill(goredSkill, true, actingPlayer.getPlayerId()));
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BREATHE_FIRE:
					handled = true;
					block(pClientState, actingPlayer.getPlayerId(), pPlayer, false, false, false, true);
					break;
				case IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN:
					if (pClientState.isThenIStartedBlastinAvailable(actingPlayer)) {
						Skill blastinSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
						communication.sendUseSkill(blastinSkill, true, actingPlayer.getPlayerId());
					}
					break;
				default:
					break;
			}
		}
		return handled;
	}

	public static boolean showPopupOrBlockPlayer(ClientState pClientState, Player<?> pDefender, boolean pDoBlitz) {
		if (pDefender == null) {
			return false;
		}
		boolean handled = false;
		Game game = pClientState.getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		// rooted players can not move but still spend movement for the blitz action
		if (UtilPlayer.isBlockable(game, pDefender) && (!pDoBlitz || playerState.isRooted() || UtilPlayer.hasMoveLeft(game, false))) {
			handled = true;
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pDefender);
			if (UtilCards.hasUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.providesBlockAlternative)
				|| (isGoredAvailable(pClientState) && pDoBlitz)) {
				createAndShowBlockOptionsPopupMenu(pClientState, actingPlayer.getPlayer(), pDefender, false);
			} else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
				block(pClientState, actingPlayer.getPlayerId(), pDefender, false, false, false, false);
			} else {
				handled = false;
			}
		}
		return handled;
	}

	public static void createAndShowBlockOptionsPopupMenu(ClientState pClientState, Player<?> attacker, Player<?> defender, boolean multiBlock) {
		IconCache iconCache = pClientState.getClient().getUserInterface().getIconCache();
		DimensionProvider dimensionProvider = pClientState.dimensionProvider();
		List<JMenuItem> menuItemList = new ArrayList<>();
		if (attacker.hasSkillProperty(NamedProperties.canPerformArmourRollInsteadOfBlock)) {
			JMenuItem stabAction = new JMenuItem(dimensionProvider, "Stab Opponent",
				pClientState.createMenuIcon(iconCache, IIconProperty.ACTION_STAB), RenderContext.ON_PITCH);
			stabAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAB);
			stabAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAB, 0));
			menuItemList.add(stabAction);
		}
		if (attacker.hasSkillProperty(NamedProperties.providesChainsawBlockAlternative) && !multiBlock) {
			JMenuItem chainsawAction = new JMenuItem(dimensionProvider, "Chainsaw",
					pClientState.createMenuIcon(iconCache, IIconProperty.ACTION_CHAINSAW), RenderContext.ON_PITCH);
			chainsawAction.setMnemonic(IPlayerPopupMenuKeys.KEY_CHAINSAW);
			chainsawAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CHAINSAW, 0));
			menuItemList.add(chainsawAction);
		}
		Optional<Skill> vomitSkill = UtilCards.getUnusedSkillWithProperty(attacker, NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail);
		if (vomitSkill.isPresent() && !multiBlock) {
			JMenuItem projectileVomit = new JMenuItem(dimensionProvider, vomitSkill.get().getName(),
					pClientState.createMenuIcon(iconCache, IIconProperty.ACTION_VOMIT), RenderContext.ON_PITCH);
			projectileVomit.setMnemonic(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
			projectileVomit.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, 0));
			menuItemList.add(projectileVomit);
		}

		Optional<Skill> fireSkill = UtilCards.getUnusedSkillWithProperty(attacker, NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover);
		if (fireSkill.isPresent() && !multiBlock) {
			JMenuItem breatheFire = new JMenuItem(dimensionProvider, fireSkill.get().getName(),
					pClientState.createMenuIcon(iconCache, IIconProperty.ACTION_BREATHE_FIRE), RenderContext.ON_PITCH);
			breatheFire.setMnemonic(IPlayerPopupMenuKeys.KEY_BREATHE_FIRE);
			breatheFire.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BREATHE_FIRE, 0));
			menuItemList.add(breatheFire);
		}

		if (isGoredAvailable(pClientState)) {
			menuItemList.add(createGoredItem(pClientState));
		}

		JMenuItem blockAction = new JMenuItem(dimensionProvider, "Block Opponent",
				pClientState.createMenuIcon(iconCache, IIconProperty.ACTION_BLOCK), RenderContext.ON_PITCH);
		blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
		blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
		menuItemList.add(blockAction);
		pClientState.createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		pClientState.showPopupMenuForPlayer(defender);
	}

	public static void block(ClientState pClientState, String pActingPlayerId, Player<?> pDefender, boolean pUsingStab,
													 boolean usingChainsaw, boolean usingVomit, boolean usingBreatheFire) {
		pClientState.getClient().getUserInterface().getFieldComponent().refresh();
		pClientState.getClient().getCommunication().sendBlock(pActingPlayerId, pDefender, pUsingStab, usingChainsaw, usingVomit, usingBreatheFire);
	}

	public static boolean isGoredAvailable(ClientState pClientState) {
		Game game = pClientState.getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie)) {
			FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetSelectionState.getSelectedPlayerId()));
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			DiceDecoration diceDecoration = game.getFieldModel().getDiceDecoration(targetCoordinate);
			Player<?> defender = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
			boolean opponentCanMove = UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.canMoveBeforeBeingBlocked);
			return diceDecoration != null
				&& (diceDecoration.getNrOfDice() == 1 || diceDecoration.getNrOfDice() == 2 || (diceDecoration.getNrOfDice() == 3 && opponentCanMove))
				&& targetCoordinate.isAdjacent(playerCoordinate);
		}

		return false;
	}

	private static JMenuItem createGoredItem(ClientState pClientState) {
		IconCache iconCache = pClientState.getClient().getUserInterface().getIconCache();
		DimensionProvider dimensionProvider = pClientState.dimensionProvider();
		JMenuItem menuItem = new JMenuItem(dimensionProvider, "Gored By The Bull",
				pClientState.createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL, 0));
		return menuItem;
	}
}

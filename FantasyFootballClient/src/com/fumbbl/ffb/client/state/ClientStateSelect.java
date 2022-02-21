package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ClientStateSelect extends ClientState {

	protected ClientStateSelect(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void enterState() {
		super.enterState();
		getClient().getGame().setDefenderId(null);
		getClient().getClientData().clearBlockDiceResult();
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_PLAYER;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (game.getTeamHome().hasPlayer(pPlayer) && playerState.isActive()) {
			createAndShowPopupMenuForPlayer(pPlayer);
		}
	}

	public void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_BLOCK:
					communication.sendActingPlayer(pPlayer, PlayerAction.BLOCK, false);
					break;
				case IPlayerPopupMenuKeys.KEY_BLITZ:
					communication.sendActingPlayer(pPlayer, PlayerAction.BLITZ_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH:
					communication.sendActingPlayer(pPlayer, PlayerAction.BLITZ_MOVE, false);
					Skill skill = pPlayer.getSkillWithProperty(NamedProperties.canGainFrenzyForBlitz);
					communication.sendUseSkill(skill, true, pPlayer.getId());
					break;
				case IPlayerPopupMenuKeys.KEY_FOUL:
					communication.sendActingPlayer(pPlayer, PlayerAction.FOUL_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_MOVE:
					communication.sendActingPlayer(pPlayer, PlayerAction.MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAND_UP:
					communication.sendActingPlayer(pPlayer, PlayerAction.STAND_UP, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ:
					communication.sendActingPlayer(pPlayer, PlayerAction.STAND_UP_BLITZ, false);
					break;
				case IPlayerPopupMenuKeys.KEY_HAND_OVER:
					communication.sendActingPlayer(pPlayer, PlayerAction.HAND_OVER_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_PASS:
					communication.sendActingPlayer(pPlayer, PlayerAction.PASS_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE:
					communication.sendActingPlayer(pPlayer, PlayerAction.THROW_TEAM_MATE_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE:
					communication.sendActingPlayer(pPlayer, PlayerAction.KICK_TEAM_MATE_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_RECOVER:
					communication.sendActingPlayer(pPlayer, PlayerAction.REMOVE_CONFUSION, false);
					break;
				case IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK:
					communication.sendActingPlayer(pPlayer, PlayerAction.MULTIPLE_BLOCK, false);
					break;
				case IPlayerPopupMenuKeys.KEY_BOMB:
					if (isThrowBombActionAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.THROW_BOMB, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_GAZE:
					communication.sendActingPlayer(pPlayer, PlayerAction.GAZE_MOVE, false);
					break;
				default:
					break;
			}
		}
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		if (isBlockActionAvailable(pPlayer)) {
			JMenuItem blockAction = new JMenuItem("Block Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLOCK)));
			blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
			blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
			menuItemList.add(blockAction);
		}
		if (isMultiBlockActionAvailable(pPlayer)) {
			JMenuItem multiBlockAction = new JMenuItem("Multiple Block",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MUTIPLE_BLOCK)));
			multiBlockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
			multiBlockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK, 0));
			menuItemList.add(multiBlockAction);
		}
		if (isThrowBombActionAvailable(pPlayer)) {
			JMenuItem moveAction = new JMenuItem("Throw Bomb Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BOMB)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOMB);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOMB, 0));
			menuItemList.add(moveAction);
		}
		if (isHypnoticGazeActionAvailable(true, pPlayer)) {
			JMenuItem hypnoticGazeAction = new JMenuItem("Hypnotic Gaze",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE)));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (isMoveActionAvailable(pPlayer)) {
			JMenuItem moveAction = new JMenuItem("Move Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if (isBlitzActionAvailable(pPlayer)) {
			JMenuItem blitzAction = new JMenuItem("Blitz Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
			blitzAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLITZ);
			blitzAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLITZ, 0));
			menuItemList.add(blitzAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainFrenzyForBlitz)) {
				JMenuItem blitzWithFrenzyAction = new JMenuItem("Frenzied Rush Blitz",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
				blitzWithFrenzyAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
				blitzWithFrenzyAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH, 0));
				menuItemList.add(blitzWithFrenzyAction);
			}
		}
		if (isFoulActionAvailable(pPlayer)) {
			JMenuItem foulAction = new JMenuItem("Foul Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_FOUL)));
			foulAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FOUL);
			foulAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FOUL, 0));
			menuItemList.add(foulAction);
		}
		if (isPassActionAvailable(pPlayer)) {
			JMenuItem passAction = new JMenuItem("Pass Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
		}
		if (isHandOverActionAvailable(pPlayer)) {
			JMenuItem handOverAction = new JMenuItem("Hand Over Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HAND_OVER)));
			handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
			handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
			menuItemList.add(handOverAction);
		}
		if (isThrowTeamMateActionAvailable(pPlayer)) {
			JMenuItem throwTeamMateAction = new JMenuItem("Throw Team-Mate Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			throwTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE);
			throwTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, 0));
			menuItemList.add(throwTeamMateAction);
		}
		if (isKickTeamMateActionAvailable(pPlayer)) {
			JMenuItem kickTeamMateAction = new JMenuItem("Kick Team-Mate Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
			kickTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE);
			kickTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, 0));
			menuItemList.add(kickTeamMateAction);
		}
		if (isRecoverFromConfusionActionAvailable(pPlayer)) {
			JMenuItem confusionAction = new JMenuItem("Recover from Confusion & End Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
			confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
			menuItemList.add(confusionAction);
		}
		if (isRecoverFromGazeActionAvailable(pPlayer)) {
			JMenuItem confusionAction = new JMenuItem("Recover from Gaze & End Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
			confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
			menuItemList.add(confusionAction);
		}
		if (isStandUpActionAvailable(pPlayer)
			&& pPlayer.hasSkillProperty(NamedProperties.enableStandUpAndEndBlitzAction)
			&& !game.getTurnData().isBlitzUsed()) {
			JMenuItem standUpAction = new JMenuItem("Stand Up & End Move (using Blitz)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			standUpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ);
			standUpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ, 0));
			menuItemList.add(standUpAction);
		}
		if (isStandUpActionAvailable(pPlayer)) {
			JMenuItem standUpAction = new JMenuItem("Stand Up & End Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			standUpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP);
			standUpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP, 0));
			menuItemList.add(standUpAction);
		}
		if (menuItemList.size() > 0) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?> selectedPlayer = getClient().getClientData().getSelectedPlayer();
		switch (pActionKey) {
			case PLAYER_SELECT:
				if (selectedPlayer != null) {
					createAndShowPopupMenuForPlayer(selectedPlayer);
				}
				break;
			case PLAYER_CYCLE_RIGHT:
				selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, true);
				if (selectedPlayer != null) {
					hideSelectSquare();
					FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
					showSelectSquare(selectedCoordinate);
					getClient().getClientData().setSelectedPlayer(selectedPlayer);
					userInterface.refreshSideBars();
				}
				break;
			case PLAYER_CYCLE_LEFT:
				selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, false);
				if (selectedPlayer != null) {
					hideSelectSquare();
					FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
					showSelectSquare(selectedCoordinate);
					getClient().getClientData().setSelectedPlayer(selectedPlayer);
					userInterface.refreshSideBars();
				}
				break;
			case PLAYER_ACTION_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BLOCK);
				break;
			case PLAYER_ACTION_MOVE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MOVE);
				break;
			case PLAYER_ACTION_BLITZ:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BLITZ);
				break;
			case PLAYER_ACTION_FRENZIED_RUSH:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
				break;
			case PLAYER_ACTION_FOUL:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FOUL);
				break;
			case PLAYER_ACTION_STAND_UP:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_STAND_UP);
				break;
			case PLAYER_ACTION_HAND_OVER:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_HAND_OVER);
				break;
			case PLAYER_ACTION_PASS:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_PASS);
				break;
			case PLAYER_ACTION_MULTIPLE_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
				break;
			case PLAYER_ACTION_GAZE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_GAZE);
				break;
			default:
				actionHandled = false;
				break;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		getClient().getCommunication().sendEndTurn();
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

	private boolean isBlockActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive() && !pPlayer.hasSkillProperty(NamedProperties.preventRegularBlockAction)
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& pPlayer.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 0);
		}
		return false;
	}

	private boolean isMultiBlockActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive()
			&& mechanic.areSpecialBlockActionsAllowed(game.getTurnMode())
			&& ((UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canBlockMoreThanOnce)
			&& !UtilCards.hasSkillToCancelProperty(pPlayer, NamedProperties.canBlockMoreThanOnce))
			|| (UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canBlockTwoAtOnce)
			&& !UtilCards.hasSkillToCancelProperty(pPlayer, NamedProperties.canBlockTwoAtOnce)))
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& pPlayer.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 1);
		}
		return false;
	}

	private boolean isThrowBombActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null)
			&& mechanic.isBombActionAllowed(game.getTurnMode())
			&& !game.getTurnData().isBombUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& !playerState.isProne()
			&& pPlayer.hasSkillProperty(NamedProperties.enableThrowBombAction));
	}

	private boolean isMoveActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isAbleToMove());
	}

	private boolean isBlitzActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isBlitzUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED) && (playerState != null)
			&& playerState.isActive() && (playerState.isAbleToMove() || playerState.isRooted())
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularBlitzAction));
	}

	private boolean isFoulActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.isFoulActionAllowed(game.getTurnMode())
			&& playerState.isActive() && (!game.getTurnData().isFoulUsed() || pPlayer.hasSkillProperty(NamedProperties.allowsAdditionalFoul))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularFoulAction)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = game.getFieldModel().getPlayerState(opponent);
				if (opponentState.canBeFouled()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPassActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& UtilPlayer.isBallAvailable(game, pPlayer) && (playerState != null)
			&& (playerState.isAbleToMove() || UtilPlayer.hasBall(game, pPlayer))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularPassAction));
	}

	private boolean isHandOverActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isHandOverUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& UtilPlayer.isBallAvailable(game, pPlayer) && (playerState != null)
			&& (playerState.isAbleToMove() || UtilPlayer.hasBall(game, pPlayer))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularHandOverAction));
	}

	private boolean isThrowTeamMateActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState == null) || pPlayer.hasSkillProperty(NamedProperties.preventThrowTeamMateAction)) {
			return false;
		}

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		Player<?>[] teamPlayers = pPlayer.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeThrown(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = ArrayTool.isProvided(mechanic.findThrowableTeamMates(game, pPlayer));

		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.canThrow(pPlayer) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	private boolean isKickTeamMateActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (!gameMechanic.isKickTeamMateActionAllowed(game.getTurnMode()) || playerState == null || pPlayer.hasSkillProperty(NamedProperties.preventKickTeamMateAction)) {
			return false;
		}
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		Player<?>[] teamPlayers = pPlayer.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeKicked(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = false;
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		Player<?>[] adjacentTeamPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(game, pPlayer.getTeam(),
			playerCoordinate, false);
		for (Player<?> adjacentTeamPlayer : adjacentTeamPlayers) {
			if (mechanic.canBeKicked(game, adjacentTeamPlayer)) {
				rightStuffAdjacent = true;
				break;
			}
		}

		return (mechanic.isKtmAvailable(game.getTurnData())
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& pPlayer.hasSkillProperty(NamedProperties.canKickTeamMates) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	private boolean isStandUpActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && (playerState.getBase() == PlayerState.PRONE) && playerState.isActive()
			&& !pPlayer.hasSkillProperty(NamedProperties.preventStandUpAction));
	}

	private boolean isRecoverFromConfusionActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isConfused() && playerState.isActive()
			&& (playerState.getBase() != PlayerState.PRONE)
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRecoverFromConcusionAction));
	}

	private boolean isRecoverFromGazeActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isHypnotized() && (playerState.getBase() != PlayerState.PRONE)
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRecoverFromGazeAction));
	}

}

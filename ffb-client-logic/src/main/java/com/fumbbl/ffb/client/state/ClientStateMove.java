package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ClientStateMove extends ClientState {

	protected ClientStateMove(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.MOVE;
	}

	protected boolean isJumpAvailableAsNextMove(Game game, ActingPlayer actingPlayer, boolean jumping) {
		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		return mechanic.isAvailableAsNextMove(game, actingPlayer, jumping);
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		FieldCoordinate fromCoordinate = null;
		if (actingPlayer != null) {
			fromCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		}
		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		if (moveSquare != null && (actingPlayer == null || !actingPlayer.isJumping() || mechanic.isValidJump(game, actingPlayer.getPlayer(), fromCoordinate, pCoordinate))) {
			setCustomCursor(moveSquare);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
			String automoveProperty = getClient().getProperty(CommonProperty.SETTING_AUTOMOVE);
			if ((actingPlayer != null) && (actingPlayer.getPlayerAction() != null)
				&& actingPlayer.getPlayerAction().isMoving() && ArrayTool.isProvided(game.getFieldModel().getMoveSquares())
				&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
				&& (game.getTurnMode() != TurnMode.PASS_BLOCK) && (game.getTurnMode() != TurnMode.KICKOFF_RETURN)
				&& (game.getTurnMode() != TurnMode.SWARMING)
				&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventAutoMove)) {
				FieldCoordinate[] shortestPath = PathFinderWithPassBlockSupport.getShortestPath(game, pCoordinate);
				if (ArrayTool.isProvided(shortestPath)) {
					fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
					fieldComponent.refresh();
				}
			}
		}
		return super.mouseOverField(pCoordinate);
	}

	private void setCustomCursor(MoveSquare pMoveSquare) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pMoveSquare.isGoingForIt() && (pMoveSquare.isDodging() && !actingPlayer.isJumping())) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI_DODGE);
		} else if (pMoveSquare.isGoingForIt()) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI);
		} else if (pMoveSquare.isDodging() && !actingPlayer.isJumping()) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_DODGE);
		} else {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_MOVE);
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(playerCoordinate);
		if (moveSquare != null) {
			setCustomCursor(moveSquare);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
			if (fieldComponent.getLayerUnderPlayers().clearMovePath()) {
				fieldComponent.refresh();
			}
		}
		return super.mouseOverPlayer(pPlayer);
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		FieldCoordinate[] movePath = fieldComponent.getLayerUnderPlayers().getMovePath();
		if (ArrayTool.isProvided(movePath) || (moveSquare != null)) {
			if (ArrayTool.isProvided(movePath)) {
				if (fieldComponent.getLayerUnderPlayers().clearMovePath()) {
					fieldComponent.refresh();
				}
				movePlayer(movePath);
			} else {
				movePlayer(pCoordinate);
			}
		}
	}

	protected boolean isSpecialAbilityAvailable(ActingPlayer actingPlayer) {
		return isTreacherousAvailable(actingPlayer)
			|| isWisdomAvailable(actingPlayer)
			|| isRaidingPartyAvailable(actingPlayer)
			|| isLookIntoMyEyesAvailable(actingPlayer)
			|| isBalefulHexAvailable(actingPlayer)
			|| isPutridRegurgitationAvailable()
			|| isGoredAvailable()
			|| isCatchOfTheDayAvailable(actingPlayer)
			|| isBlackInkAvailable(actingPlayer);
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate position = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (pPlayer == actingPlayer.getPlayer()) {
			JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
			if (actingPlayer.hasActed() || mechanic.canJump(game, pPlayer, position)
				|| pPlayer.hasSkillProperty(NamedProperties.inflictsConfusion)
				|| isSpecialAbilityAvailable(actingPlayer)
				|| (pPlayer.hasSkillProperty(NamedProperties.canDropBall) && UtilPlayer.hasBall(game, pPlayer))
				|| ((actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) && UtilPlayer.hasBall(game, pPlayer))
				|| ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER_MOVE) && UtilPlayer.hasBall(game, pPlayer))
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)
				|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE)) {
				createAndShowPopupMenuForActingPlayer();
			} else {
				getClient().getCommunication().sendActingPlayer(null, null, false);
			}
		} else {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			MoveSquare moveSquare = game.getFieldModel().getMoveSquare(playerCoordinate);
			if (moveSquare != null) {
				movePlayer(playerCoordinate);
			}
		}
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					if (isEndPlayerActionAvailable()) {
						communication.sendActingPlayer(null, null, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_JUMP:
					if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_HAND_OVER:
					if (UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
						if (PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction()) {
							communication.sendActingPlayer(pPlayer, PlayerAction.HAND_OVER, actingPlayer.isJumping());
						} else if (PlayerAction.HAND_OVER == actingPlayer.getPlayerAction()) {
							communication.sendActingPlayer(pPlayer, PlayerAction.HAND_OVER_MOVE, actingPlayer.isJumping());
						}
					}
					break;
				case IPlayerPopupMenuKeys.KEY_PASS:
					if (PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction()
						&& UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
						communication.sendActingPlayer(pPlayer, PlayerAction.PASS, actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE:
					communication.sendActingPlayer(pPlayer, PlayerAction.THROW_TEAM_MATE, actingPlayer.isJumping());
					break;
				case IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE:
					communication.sendActingPlayer(pPlayer, PlayerAction.KICK_TEAM_MATE, actingPlayer.isJumping());
					break;
				case IPlayerPopupMenuKeys.KEY_MOVE:
					if (PlayerAction.GAZE == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(pPlayer, PlayerAction.MOVE, actingPlayer.isJumping());
					}
					if (PlayerAction.PASS == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(pPlayer, PlayerAction.PASS_MOVE, actingPlayer.isJumping());
					}
					if (PlayerAction.THROW_TEAM_MATE == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(pPlayer, PlayerAction.THROW_TEAM_MATE_MOVE, actingPlayer.isJumping());
					}
					if (PlayerAction.KICK_TEAM_MATE == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(pPlayer, PlayerAction.KICK_TEAM_MATE_MOVE, actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_GAZE:
					if (isHypnoticGazeActionAvailable(false, actingPlayer.getPlayer(), NamedProperties.inflictsConfusion)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.GAZE, actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE:
					if (isFumblerooskieAvailable()) {
						communication.sendUseFumblerooskie();
					}
					break;
				case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
					if (isTreacherousAvailable(actingPlayer)) {
						Skill skill = pPlayer.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(skill, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_WISDOM:
					if (isWisdomAvailable(actingPlayer)) {
						communication.sendUseWisdom();
					}
					break;
				case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
					if (isRaidingPartyAvailable(actingPlayer)) {
						Skill raidingSkill = pPlayer.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
						communication.sendUseSkill(raidingSkill, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES:
					if (isLookIntoMyEyesAvailable(pPlayer)) {
						UtilCards.getUnusedSkillWithProperty(pPlayer, NamedProperties.canStealBallFromOpponent)
							.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, pPlayer.getId()));
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BALEFUL_HEX:
					if (isBalefulHexAvailable(actingPlayer)) {
						Skill balefulSkill = pPlayer.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
						communication.sendUseSkill(balefulSkill, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT:
					if (isPutridRegurgitationAvailable()) {
						Skill putridSkill = pPlayer.getSkillWithProperty(NamedProperties.canUseVomitAfterBlock);
						communication.sendUseSkill(putridSkill, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BLACK_INK:
					if (isBlackInkAvailable(actingPlayer)) {
						Skill blackInkSkill = pPlayer.getSkillWithProperty(NamedProperties.canGazeAutomatically);
						communication.sendUseSkill(blackInkSkill, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY:
					if (isCatchOfTheDayAvailable(actingPlayer)) {
						Skill skill = pPlayer.getSkillWithProperty(NamedProperties.canGetBallOnGround);
						communication.sendUseSkill(skill, true, pPlayer.getId());
					}
					break;
				default:
					break;
			}
		}
	}

	protected boolean showGridForKTM(Game game, ActingPlayer actingPlayer) {
		return false;
	}

	protected void createAndShowPopupMenuForActingPlayer() {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
			JMenuItem passAction = new JMenuItem(dimensionProvider(), "Pass Ball (any square)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
		}
		if (((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
			|| showGridForKTM(game, actingPlayer)
			|| ((PlayerAction.THROW_TEAM_MATE_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer(), true))) {
			JMenuItem toggleRangeGridAction = new JMenuItem(dimensionProvider(), "Range Grid on/off",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_RANGE_GRID)));
			toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
			menuItemList.add(toggleRangeGridAction);
		}
		if (PlayerAction.GAZE == actingPlayer.getPlayerAction()) {
			menuItemList.add(createMoveMenuItem(iconCache));
		}
		if (isJumpAvailableAsNextMove(game, actingPlayer, true)) {
			if (actingPlayer.isJumping()) {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			} else {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			}
		}
		if (isHypnoticGazeActionAvailable(false, actingPlayer.getPlayer(), NamedProperties.inflictsConfusion)) {
			JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider(), "Hypnotic Gaze",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE)));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (isFumblerooskieAvailable()) {
			JMenuItem fumblerooskieAction = new JMenuItem(dimensionProvider(), "Fumblerooskie",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			fumblerooskieAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE);
			fumblerooskieAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, 0));
			menuItemList.add(fumblerooskieAction);
		}
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
		if (isPutridRegurgitationAvailable()) {
			menuItemList.add(createPutridRegurgitationItem(iconCache));
		}
		if (isGoredAvailable()) {
			menuItemList.add(createGoredItem(iconCache));
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

	protected JMenuItem createMoveMenuItem(IconCache iconCache) {
		JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
		moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
		moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
		return moveAction;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(player);
		FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(playerPosition, pActionKey);
		if (moveCoordinate != null) {
			MoveSquare[] moveSquares = game.getFieldModel().getMoveSquares();
			for (MoveSquare moveSquare : moveSquares) {
				if (moveSquare.getCoordinate().equals(moveCoordinate)) {
					movePlayer(moveCoordinate);
					break;
				}
			}
		} else {
			switch (pActionKey) {
				case PLAYER_SELECT:
					createAndShowPopupMenuForActingPlayer();
					break;
				case PLAYER_ACTION_HAND_OVER:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_HAND_OVER);
					break;
				case PLAYER_ACTION_PASS:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_PASS);
					break;
				case PLAYER_ACTION_JUMP:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_JUMP);
					break;
				case PLAYER_ACTION_END_MOVE:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
					break;
				case PLAYER_ACTION_GAZE:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_GAZE);
					break;
				case PLAYER_ACTION_FUMBLEROOSKIE:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE);
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
				case PLAYER_ACTION_PROJECTILE_VOMIT:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
					return true;
				case PLAYER_ACTION_GORED:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
					return true;
				case PLAYER_ACTION_BLACK_INK:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
					return true;
				case PLAYER_ACTION_CATCH_OF_THE_DAY:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
					return true;
				default:
					actionHandled = false;
					break;
			}
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		getClient().getCommunication().sendEndTurn(game.getTurnMode());
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

	protected void movePlayer(FieldCoordinate pCoordinate) {
		if (pCoordinate == null) {
			return;
		}
		movePlayer(new FieldCoordinate[]{pCoordinate});
	}

	protected void movePlayer(FieldCoordinate[] pCoordinates) {
		if (!ArrayTool.isProvided(pCoordinates)) {
			return;
		}
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate coordinateFrom = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (coordinateFrom == null) {
			return;
		}

		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());

		if (actingPlayer.isJumping() && !mechanic.isValidJump(game, actingPlayer.getPlayer(), coordinateFrom, pCoordinates[pCoordinates.length - 1])) {
			return;
		}

		getClient().getGame().getFieldModel().clearMoveSquares();
		getClient().getUserInterface().getFieldComponent().refresh();
		sendCommand(actingPlayer, coordinateFrom, pCoordinates);
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates) {
		getClient().getCommunication().sendPlayerMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
	}

	private boolean isEndPlayerActionAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (!actingPlayer.hasActed()
			|| !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.forceFullMovement)
			|| (actingPlayer.getCurrentMove() >= actingPlayer.getPlayer().getMovementWithModifiers()));
	}

	private boolean isFumblerooskieAvailable() {
		ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();

		return (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canDropBall)
			&& actingPlayer.getPlayerAction() != null
			&& actingPlayer.getPlayerAction().allowsFumblerooskie()
			&& UtilPlayer.hasBall(getClient().getGame(), actingPlayer.getPlayer()));
	}

	protected boolean isPutridRegurgitationAvailable() {
		return false;
	}

	protected JMenuItem createPutridRegurgitationItem(IconCache iconCache) {
		return null;
	}

	protected boolean isGoredAvailable() {
		return false;
	}

	protected JMenuItem createGoredItem(IconCache iconCache) {
		return null;
	}

	protected void showShortestPath(FieldCoordinate pCoordinate, Game game, FieldComponent fieldComponent,
																	ActingPlayer actingPlayer) {
		String automoveProperty = getClient().getProperty(CommonProperty.SETTING_AUTOMOVE);
		if (actingPlayer != null
			&& actingPlayer.getPlayerAction() != null
			&& actingPlayer.getPlayerAction().isMoving()
			&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
			&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.preventAutoMove)
		) {

			FieldCoordinate[] shortestPath;

			Player<?> playerInTarget = game.getFieldModel().getPlayer(pCoordinate);

			if (actingPlayer.isStandingUp()
				&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canStandUpForFree)) {
				actingPlayer.setCurrentMove(Math.min(Constant.MINIMUM_MOVE_TO_STAND_UP,
					actingPlayer.getPlayer().getMovementWithModifiers()));
				actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto
				// go-for-it
			}

			if (playerInTarget != null && playerInTarget.getTeam() != actingPlayer.getPlayer().getTeam()) {
				shortestPath = PathFinderWithPassBlockSupport.getShortestPathToPlayer(game, playerInTarget);
			} else {
				shortestPath = PathFinderWithPassBlockSupport.getShortestPath(game, pCoordinate);
			}
			if (ArrayTool.isProvided(shortestPath)) {
				fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
				fieldComponent.refresh();
			}
		}
	}

}

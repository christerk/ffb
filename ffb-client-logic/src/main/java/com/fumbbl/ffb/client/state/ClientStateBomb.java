package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilRangeRuler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ClientStateBomb extends ClientState {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	protected ClientStateBomb(FantasyFootballClient pClient) {
		super(pClient);
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	public ClientStateId getId() {
		return ClientStateId.BOMB;
	}

	public void enterState() {
		super.enterState();
		fShowRangeRuler = true;
		fRangeGridHandler.refreshSettings();
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

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, pCoordinate, false);
		if ((PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) || (passingDistance != null)) {
			game.setPassCoordinate(pCoordinate);
			getClient().getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
			game.getFieldModel().setRangeRuler(null);
			getClient().getUserInterface().getFieldComponent().refresh();
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		return mouseOverField(playerCoordinate);
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;
		if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
			game.getFieldModel().setRangeRuler(null);
			userInterface.getFieldComponent().refresh();
			selectable = true;
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
		} else {
			drawRangeRuler(pCoordinate);
		}
		return selectable;
	}

	private void drawRangeRuler(FieldCoordinate pCoordinate) {
		RangeRuler rangeRuler;
		Game game = getClient().getGame();
		if (fShowRangeRuler && (game.getPassCoordinate() == null)) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			UserInterface userInterface = getClient().getUserInterface();
			FieldComponent fieldComponent = userInterface.getFieldComponent();
			rangeRuler = UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), pCoordinate, false);
			game.getFieldModel().setRangeRuler(rangeRuler);
			if (rangeRuler != null) {
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
			} else {
				UtilClientCursor.setDefaultCursor(userInterface);
			}
			fieldComponent.getLayerUnderPlayers().clearMovePath();
			fieldComponent.refresh();
		}
	}

	@Override
	public void handleCommand(NetCommand pNetCommand) {
		fRangeGridHandler.refreshRangeGrid();
		super.handleCommand(pNetCommand);
	}

	@Override
	public void leaveState() {
		getClient().getUserInterface().getFieldComponent().getLayerRangeRuler().removeRangeRuler();
		getClient().getUserInterface().getFieldComponent().refresh();
		fRangeGridHandler.setShowRangeGrid(false);
		fRangeGridHandler.refreshRangeGrid();
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (isHailMaryPassActionAvailable()) {
			String text = (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) ? "Don't use Hail Mary Pass"
				: "Use Hail Mary Pass";
			JMenuItem hailMaryBombAction = new JMenuItem(dimensionProvider(), text,
				createMenuIcon(iconCache, IIconProperty.ACTION_TOGGLE_HAIL_MARY_BOMB));
			hailMaryBombAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB);
			hailMaryBombAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB, 0));
			menuItemList.add(hailMaryBombAction);
		}

		if (isRangeGridAvailable()) {
			JMenuItem toggleRangeGridAction = new JMenuItem(dimensionProvider(), "Range Grid on/off",
				createMenuIcon(iconCache, IIconProperty.ACTION_TOGGLE_RANGE_GRID));
			toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
			menuItemList.add(toggleRangeGridAction);
		}

		if (isEndTurnActionAvailable()) {
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
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			menuItemList.add(createThenIStartedBlastinItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ClientCommunication communication = getClient().getCommunication();
		switch (pMenuKey) {
			case IPlayerPopupMenuKeys.KEY_END_MOVE:
				if (isEndTurnActionAvailable()) {
					communication.sendActingPlayer(null, null, false);
				}
				break;
			case IPlayerPopupMenuKeys.KEY_RANGE_GRID:
				if (isRangeGridAvailable()) {
					fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
					fRangeGridHandler.refreshRangeGrid();
				}
				break;
			case IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB:
				if (isHailMaryPassActionAvailable()) {
					if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
						communication.sendActingPlayer(player, PlayerAction.THROW_BOMB, actingPlayer.isJumping());
						fShowRangeRuler = true;
					} else {
						communication.sendActingPlayer(player, PlayerAction.HAIL_MARY_BOMB, actingPlayer.isJumping());
						fShowRangeRuler = false;
					}
					if (!fShowRangeRuler && (game.getFieldModel().getRangeRuler() != null)) {
						game.getFieldModel().setRangeRuler(null);
					}
				}
				break;
			case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
				if (isTreacherousAvailable(actingPlayer)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
					communication.sendUseSkill(skill, true, player.getId());
				}
				break;
			case IPlayerPopupMenuKeys.KEY_WISDOM:
				if (isWisdomAvailable(actingPlayer)) {
					getClient().getCommunication().sendUseWisdom();
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

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == null) {
			return false;
		}
		Player<?> player = getClient().getGame().getActingPlayer().getPlayer();
		switch (pActionKey) {
			case PLAYER_ACTION_RANGE_GRID:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RANGE_GRID);
				return true;
			case PLAYER_ACTION_HAIL_MARY_PASS:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB);
				return true;
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
			case PLAYER_ACITON_THEN_I_STARTED_BLASTIN:
				menuItemSelected(player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
				return true;
			default:
				return super.actionKeyPressed(pActionKey);
		}
	}

	private boolean isHailMaryPassActionAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)
			&& !(game.getFieldModel().getWeather().equals(Weather.BLIZZARD)));
	}

	private boolean isRangeGridAvailable() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB);
	}

	private boolean isEndTurnActionAvailable() {
		Game game = getClient().getGame();
		return !game.getTurnMode().isBombTurn();
	}

}

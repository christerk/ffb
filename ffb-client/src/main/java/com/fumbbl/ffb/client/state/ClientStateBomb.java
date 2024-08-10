package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BombLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.UtilCards;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateBomb extends ClientStateAwt<BombLogicModule> {

	private final RangeGridHandler fRangeGridHandler;

	protected ClientStateBomb(FantasyFootballClientAwt pClient) {
		super(pClient, new BombLogicModule(pClient));
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	public ClientStateId getId() {
		return ClientStateId.BOMB;
	}

	public void initUI() {
		fRangeGridHandler.refreshSettings();
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);

		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			case PERFORM:
				clickOnField(result.getCoordinate());
				break;
			default:
				break;
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				getClient().getUserInterface().getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case PERFORM:
				UserInterface userInterface = getClient().getUserInterface();
				userInterface.refreshSideBars();
				return mouseOverField(result.getCoordinate());
			default:
				return false;
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				userInterface.getFieldComponent().refresh();
				selectable = true;
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
				break;
			case DRAW:
				drawRangeRuler(result.getRangeRuler());
				break;
			default:
				break;
		}
		return selectable;
	}

	private void drawRangeRuler(RangeRuler rangeRuler) {
		UserInterface userInterface = getClient().getUserInterface();
		FieldComponent fieldComponent = userInterface.getFieldComponent();
		if (rangeRuler != null) {
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BOMB);
		} else {
			UtilClientCursor.setDefaultCursor(userInterface);
		}
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		fieldComponent.refresh();
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
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_HAIL_MARY_BOMB)));
			hailMaryBombAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB);
			hailMaryBombAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAIL_MARY_BOMB, 0));
			menuItemList.add(hailMaryBombAction);
		}

		if (isRangeGridAvailable()) {
			JMenuItem toggleRangeGridAction = new JMenuItem(dimensionProvider(), "Range Grid on/off",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_RANGE_GRID)));
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
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
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
						communication.sendActingPlayer(pPlayer, PlayerAction.THROW_BOMB, actingPlayer.isJumping());
						fShowRangeRuler = true;
					} else {
						communication.sendActingPlayer(pPlayer, PlayerAction.HAIL_MARY_BOMB, actingPlayer.isJumping());
						fShowRangeRuler = false;
					}
					if (!fShowRangeRuler && (game.getFieldModel().getRangeRuler() != null)) {
						game.getFieldModel().setRangeRuler(null);
					}
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
					getClient().getCommunication().sendUseWisdom();
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

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return null;
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

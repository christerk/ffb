package com.fumbbl.ffb.client.state;

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
import com.fumbbl.ffb.client.state.logic.PassLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.UtilPlayer;
import com.fumbbl.ffb.util.UtilRangeRuler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kalimar
 */
public class ClientStatePass extends AbstractClientStateMove<PassLogicModule> {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	protected ClientStatePass(FantasyFootballClientAwt pClient) {
		super(pClient, new PassLogicModule(pClient));
		fRangeGridHandler = new RangeGridHandler(pClient, false);
	}

	@Override
	public void initUI() {
		super.initUI();
		fShowRangeRuler = true;
		fRangeGridHandler.refreshSettings();
	}

	public void clickOnPlayer(Player<?> player) {
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.playerInteraction(player);
		switch (result.getKind()) {
			case HANDLED:
				userInterface.getFieldComponent().refresh();
				break;
			default:
				evaluateClick(result, player);
				break;
		}
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case DELEGATE:
				getDelegate(result).clickOnField(pCoordinate);
				break;
			case HANDLED:
				userInterface.getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		boolean selectable = false;
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case DRAW:
				drawRangeRuler(result.getCoordinate());
				break;
			default:
				selectable = true;
				FieldComponent fieldComponent = userInterface.getFieldComponent();
				fieldComponent.getLayerUnderPlayers().clearMovePath();
				fieldComponent.refresh();
				getClient().getClientData().setSelectedPlayer(pPlayer);
				userInterface.refreshSideBars();
				determineCursor(result);
				break;
		}
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();

		return selectable;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();
		boolean selectable = false;

		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		determineCursor(result);
		switch (result.getKind()) {
			case PERFORM:
				userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
				userInterface.getFieldComponent().refresh();
				selectable = true;
				break;
			case DELEGATE:
				selectable = getDelegate(result).mouseOverField(pCoordinate);
				break;
			case DRAW:
				drawRangeRuler(pCoordinate);
				break;
			default:
				break;
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
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
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

		if ((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && !actingPlayer.hasPassed()) {
			JMenuItem passAction = new JMenuItem(dimensionProvider(), "Pass Ball (any square)",
				createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
		}

		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && !actingPlayer.hasPassed()
			&& !game.getFieldModel().getWeather().equals(Weather.BLIZZARD)) {
			String text = (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) ? "Don't use Hail Mary Pass"
				: "Use Hail Mary Pass";
			JMenuItem hailMaryPassAction = new JMenuItem(dimensionProvider(), text,
				createMenuIcon(iconCache, IIconProperty.ACTION_TOGGLE_HAIL_MARY_PASS));
			hailMaryPassAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS);
			hailMaryPassAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS, 0));
			menuItemList.add(hailMaryPassAction);
		}

		if (logicModule.isJumpAvailableAsNextMove(game, actingPlayer, false)) {
			JMenuItem jumpAction;
			JMenuItem specialJumpAction = null;
			if (actingPlayer.isJumping()) {
				jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
			} else {
				jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					createMenuIcon(iconCache, IIconProperty.ACTION_JUMP));
				Optional<Skill> boundingLeap = logicModule.isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					specialJumpAction = new JMenuItem(dimensionProvider(),
						"Jump (" + boundingLeap.get().getName() + ")",
						createMenuIcon(iconCache, IIconProperty.ACTION_JUMP));
					specialJumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
					specialJumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, 0));
				}
			}
			jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
			jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
			menuItemList.add(jumpAction);
			if (specialJumpAction != null) {
				menuItemList.add(specialJumpAction);
			}
		}

		if (!actingPlayer.hasPassed()) {
			JMenuItem toggleRangeGridAction = new JMenuItem(dimensionProvider(), "Range Grid on/off",
				createMenuIcon(iconCache, IIconProperty.ACTION_TOGGLE_RANGE_GRID));
			toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
			menuItemList.add(toggleRangeGridAction);
		}

		if (!actingPlayer.hasPassed()) {
			if (!actingPlayer.isSufferingAnimosity()) {
				JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
					createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
				moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
				moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
				menuItemList.add(moveAction);
			}
		}

		if (logicModule.isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}

		if (logicModule.isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (logicModule.isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		if (logicModule.isBlackInkAvailable(actingPlayer)) {
			menuItemList.add(createBlackInkItem(iconCache));
		}
		if (logicModule.isCatchOfTheDayAvailable(actingPlayer)) {
			menuItemList.add(createCatchOfTheDayItem(iconCache));
		}
		if (logicModule.isThenIStartedBlastinAvailable(actingPlayer)) {
			menuItemList.add(createThenIStartedBlastinItem(iconCache));
		}
		addEndActionLabel(iconCache, menuItemList);

		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

	@Override
	protected void postPerform(int menuKey) {
		switch (menuKey) {
			case IPlayerPopupMenuKeys.KEY_RANGE_GRID:
				fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
				fRangeGridHandler.refreshRangeGrid();
				break;
			case IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS:
				if (logicModule.hmpAvailable()) {
					// logic module sends command to deselect hmp so afterward we have to show the ruler again
					fShowRangeRuler = logicModule.actionIsHmp();
				}
				break;
			default:
				break;
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == ActionKey.PLAYER_ACTION_RANGE_GRID) {
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			return true;
		} else if (pActionKey == ActionKey.PLAYER_ACTION_HAIL_MARY_PASS) {
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_HAIL_MARY_PASS);
			return true;
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}

}

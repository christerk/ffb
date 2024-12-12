package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.HandOverLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kalimar
 */
public class ClientStateHandOver extends AbstractClientStateMove<HandOverLogicModule> {

	protected ClientStateHandOver(FantasyFootballClientAwt pClient) {
		super(pClient, new HandOverLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.HAND_OVER;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case SUPER:
				super.clickOnPlayer(pPlayer);
				break;
			default:
				break;
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate catcherPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition, pActionKey);
		Player<?> catcher = game.getFieldModel().getPlayer(catcherPosition);
		if (catcher != null) {
			InteractionResult result = logicModule.playerInteraction(catcher);
			switch (result.getKind()) {
				case HANDLED:
					return true;
				default:
					return false;
			}
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		determineCursor(logicModule.playerPeek(pPlayer));
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		determineCursor(logicModule.fieldPeek(pCoordinate));
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	protected void createAndShowPopupMenuForActingPlayer() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (logicModule.ballInHand()) {
			if ((PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction())) {
				JMenuItem handOverAction = new JMenuItem(dimensionProvider(), "Hand Over Ball (any player)",
					createMenuIcon(iconCache, IIconProperty.ACTION_HAND_OVER));
				handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
				handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
				menuItemList.add(handOverAction);
			} else if (PlayerAction.HAND_OVER == actingPlayer.getPlayerAction()) {
				JMenuItem handOverAction = new JMenuItem(dimensionProvider(), "Regular Hand Over / Move",
					createMenuIcon(iconCache, IIconProperty.ACTION_HAND_OVER));
				handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
				handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
				menuItemList.add(handOverAction);
			}
		}

		if (logicModule.isJumpAvailableAsNextMove(game, actingPlayer, true)) {
			if (actingPlayer.isJumping()) {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			} else {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					createMenuIcon(iconCache, IIconProperty.ACTION_JUMP));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
				Optional<Skill> boundingLeap = logicModule.isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					JMenuItem specialJumpAction = new JMenuItem(dimensionProvider(),
						"Jump (" + boundingLeap.get().getName() + ")",
						createMenuIcon(iconCache, IIconProperty.ACTION_JUMP));
					specialJumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
					specialJumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, 0));
					menuItemList.add(specialJumpAction);
				}
			}
		}

		addEndActionLabel(iconCache, menuItemList);

		if (logicModule.isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
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
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());

	}

}

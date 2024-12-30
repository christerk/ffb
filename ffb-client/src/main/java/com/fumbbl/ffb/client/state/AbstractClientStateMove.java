package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kalimar
 */
public abstract class AbstractClientStateMove<T extends MoveLogicModule> extends ClientStateAwt<T> {

	protected AbstractClientStateMove(FantasyFootballClientAwt pClient, T logicModule) {
		super(pClient, logicModule);
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				setCustomCursor(result.getMoveSquare());
				break;
			case RESET:
				Game game = getClient().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				fieldComponent.getLayerUnderPlayers().drawMovePath(result.getPath(), actingPlayer.getCurrentMove());
				fieldComponent.refresh();
				break;
			default:
				break;
		}
		return super.mouseOverField(pCoordinate);
	}

	private void setCustomCursor(MoveSquare pMoveSquare) {
		MoveSquare.Kind kind = logicModule.kind(pMoveSquare);
		switch (kind) {
			case RUSH_DODGE:
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI_DODGE);
				break;
			case RUSH:
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI);
				break;
			case DODGE:
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_DODGE);
				break;
			case MOVE:
				UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_MOVE);
				break;
			default:
				break;
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
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

	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case HANDLED:
				FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
				if (fieldComponent.getLayerUnderPlayers().clearMovePath()) {
					fieldComponent.refresh();
				}
				playerWasMoved();
				break;
			default:
				break;
		}
	}


	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		evaluateClick(result, pPlayer);
	}

	protected void evaluateClick(InteractionResult result, Player<?> player) {
		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForActingPlayer();
				break;
			case PERFORM:
				playerWasMoved();
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_END_MOVE, ClientAction.END_MOVE);
			put(IPlayerPopupMenuKeys.KEY_JUMP, ClientAction.JUMP);
			put(IPlayerPopupMenuKeys.KEY_HAND_OVER, ClientAction.HAND_OVER);
			put(IPlayerPopupMenuKeys.KEY_PASS, ClientAction.PASS);
			put(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, ClientAction.THROW_TEAM_MATE);
			put(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, ClientAction.KICK_TEAM_MATE);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_GAZE, ClientAction.GAZE);
			put(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, ClientAction.FUMBLEROOSKIE);
			put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, ClientAction.CATCH_OF_THE_DAY);
			put(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, ClientAction.BOUNDING_LEAP);
			put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

	protected void createAndShowPopupMenuForActingPlayer() {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		List<JMenuItem> menuItemList = new ArrayList<>();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (logicModule.isPassAnySquareAvailable(actingPlayer, game)) {
			JMenuItem passAction = new JMenuItem(dimensionProvider(), "Pass Ball (any square)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS, dimensionProvider())));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
		}
		if (logicModule.isRangeGridAvailable(actingPlayer, game)) {
			JMenuItem toggleRangeGridAction = new JMenuItem(dimensionProvider(), "Range Grid on/off",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_RANGE_GRID, dimensionProvider())));
			toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
			menuItemList.add(toggleRangeGridAction);
		}
		if (logicModule.isMoveAvailable(actingPlayer)) {
			menuItemList.add(createMoveMenuItem(iconCache));
		}
		if (logicModule.isJumpAvailableAsNextMove(game, actingPlayer, true)) {
			if (actingPlayer.isJumping()) {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE, dimensionProvider())));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			} else {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP, dimensionProvider())));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);

				Optional<Skill> boundingLeap = logicModule.isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					JMenuItem specialJumpAction = new JMenuItem(dimensionProvider(),
						"Jump (" + boundingLeap.get().getName() + ")",
						new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP, dimensionProvider())));
					specialJumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
					specialJumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, 0));
					menuItemList.add(specialJumpAction);
				}
			}
		}
		if (logicModule.isHypnoticGazeActionAvailable(false, actingPlayer.getPlayer(), NamedProperties.inflictsConfusion)) {
			JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider(), "Hypnotic Gaze",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE, dimensionProvider())));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (logicModule.isFumblerooskieAvailable()) {
			JMenuItem fumblerooskieAction = new JMenuItem(dimensionProvider(), "Fumblerooskie",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS, dimensionProvider())));
			fumblerooskieAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE);
			fumblerooskieAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE, 0));
			menuItemList.add(fumblerooskieAction);
		}
		if (logicModule.isEndPlayerActionAvailable()) {
			addEndActionLabel(iconCache, menuItemList);
		}
		if (logicModule.isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
		}
		if (logicModule.isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}
		if (logicModule.isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (logicModule.isLookIntoMyEyesAvailable(actingPlayer)) {
			menuItemList.add(createLookIntoMyEyesItem(iconCache));
		}
		if (logicModule.isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		if (logicModule.isPutridRegurgitationAvailable()) {
			menuItemList.add(createPutridRegurgitationItem(iconCache));
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

	protected JMenuItem createMoveMenuItem(IconCache iconCache) {
		JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE, dimensionProvider())));
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
					clickOnField(moveCoordinate);
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
				case PLAYER_ACTION_BLACK_INK:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
					return true;
				case PLAYER_ACTION_CATCH_OF_THE_DAY:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
					return true;
				case PLAYER_ACTION_BOUNDING_LEAP:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
					return true;
				case PLAYER_ACITON_THEN_I_STARTED_BLASTIN:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
					return true;
				default:
					actionHandled = super.actionKeyPressed(pActionKey);
					break;
			}
		}
		return actionHandled;
	}

	@Override
	public void postEndTurn() {
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

	protected void playerWasMoved() {
		getClient().getGame().getFieldModel().clearMoveSquares();
		getClient().getUserInterface().getFieldComponent().refresh();
	}

	protected JMenuItem createPutridRegurgitationItem(@SuppressWarnings("unused") IconCache iconCache) {
		return null;
	}

	protected void showShortestPath(FieldCoordinate pCoordinate, FieldComponent fieldComponent,
																	ActingPlayer actingPlayer) {
		FieldCoordinate[] shortestPath = logicModule.findShortestPath(pCoordinate);
		if (ArrayTool.isProvided(shortestPath)) {
			fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
			fieldComponent.refresh();
		}
	}
}



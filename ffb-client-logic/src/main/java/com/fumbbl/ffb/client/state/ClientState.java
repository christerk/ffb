package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.ui.GameMenuBar;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientMarker;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.net.INetCommandHandler;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kalimar
 */
public abstract class ClientState implements INetCommandHandler, MouseListener, MouseMotionListener, ActionListener {

	private static final Set<String> ALLOW_RIGHT_CLICK_ON_PLAYER = new HashSet<String>() {{
		add(IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE);
		add(IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU);
	}};

	private final FantasyFootballClient fClient;

	private FieldCoordinate fSelectSquareCoordinate;

	private boolean fClickable;

	private JPopupMenu fPopupMenu;

	private Player<?> fPopupMenuPlayer;

	public ClientState(FantasyFootballClient pClient) {
		fClient = pClient;
		setClickable(true);
	}

	public abstract ClientStateId getId();

	public void enterState() {
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getDialogManager().updateDialog();
		UtilClientCursor.setDefaultCursor(userInterface);
	}

	public void leaveState() {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
	}

	// Interface Methods

	// MouseMotionListener
	public void mouseDragged(MouseEvent pMouseEvent) {
	}

	// MouseListener
	public void mouseClicked(MouseEvent pMouseEvent) {
	}

	// MouseListener
	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	// MouseListener
	public void mousePressed(MouseEvent pMouseEvent) {
		if (getClient().getCurrentMouseButton() != MouseEvent.NOBUTTON || pMouseEvent.getID() == MouseEvent.MOUSE_WHEEL) {
			return;
		}
		getClient().setCurrentMouseButton(pMouseEvent.getButton());
	}

	public void handleCommand(NetCommand pNetCommand) {
	}

	// Helper Methods

	public FantasyFootballClient getClient() {
		return fClient;
	}

	protected FieldCoordinate getFieldCoordinate(MouseEvent pMouseEvent) {
		FieldCoordinate coordinate = null;
		int x = pMouseEvent.getX();
		int y = pMouseEvent.getY();
		DimensionProvider dimensionProvider = fClient.getUserInterface().getDimensionProvider();
		Dimension field = dimensionProvider.dimension(Component.FIELD, RenderContext.UI);
		if ((x > 0) && (x < field.width) && (y > 0) && (y < field.height)) {
			coordinate = new FieldCoordinate((int) ((x / dimensionProvider().getLayoutSettings().getScale()) / dimensionProvider.unscaledFieldSquare()),
				(int) ((y / dimensionProvider.getLayoutSettings().getScale()) / dimensionProvider.unscaledFieldSquare()));
			coordinate = getClient().getUserInterface().getDimensionProvider().mapToGlobal(coordinate);
		}
		return coordinate;
	}

	public void showSelectSquare(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			fSelectSquareCoordinate = pCoordinate;
			drawSelectSquare(fSelectSquareCoordinate, new Color(0.0f, 0.0f, 1.0f, 0.2f));
		}
	}

	protected void drawSelectSquare(FieldCoordinate pCoordinate, Color pColor) {
		if (pCoordinate != null) {
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();

			DimensionProvider dimensionProvider = fClient.getUserInterface().getDimensionProvider();
			Dimension dimension = dimensionProvider.mapToLocal(pCoordinate);
			int x = dimension.width + 1;
			int y = dimension.height + 1;
			Rectangle bounds = new Rectangle(x, y, dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH) - 2, dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH) - 2);

			Graphics2D g2d = fieldComponent.getImage().createGraphics();
			g2d.setPaint(pColor);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2d.dispose();
			fieldComponent.repaint(bounds);
		}
	}

	public void hideSelectSquare() {
		if (fSelectSquareCoordinate != null) {
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
			DimensionProvider dimensionProvider = fClient.getUserInterface().getDimensionProvider();
			Dimension dimension = dimensionProvider.mapToLocal(fSelectSquareCoordinate);
			Rectangle bounds = new Rectangle(dimension.width, dimension.height, dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH), dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH));
			fieldComponent.refresh(bounds);
			fSelectSquareCoordinate = null;
		}
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		if (isSelectable()) {
			FieldCoordinate coordinate = getFieldCoordinate(pMouseEvent);
			if ((coordinate == null) || !FieldCoordinateBounds.FIELD.isInBounds(coordinate)) {
				hideSelectSquare();
			} else {
				if (!coordinate.equals(fSelectSquareCoordinate)) {
					hideSelectSquare();
					boolean selectable;
					Game game = getClient().getGame();
					Player<?> player = game.getFieldModel().getPlayer(coordinate);
					if (player != null) {
						selectable = mouseOverPlayer(player);
					} else {
						selectable = mouseOverField(coordinate);
					}
					if (selectable) {
						showSelectSquare(coordinate);
					}
				}
			}
		}
	}

	public void mouseExited(MouseEvent pMouseEvent) {
		hideSelectSquare();
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
		if (getClient().getCurrentMouseButton() != pMouseEvent.getButton()) {
			return;
		}
		getClient().setCurrentMouseButton(MouseEvent.NOBUTTON);
		FieldCoordinate coordinate = getFieldCoordinate(pMouseEvent);
		if ((getClient().getGame() != null) && (coordinate != null)) {
			Player<?> player = getClient().getGame().getFieldModel().getPlayer(coordinate);
			if (pMouseEvent.isShiftDown()) {
				hideSelectSquare();
				if (player != null) {
					int offsetX = 1, offsetY = 1;
					DimensionProvider dimensionProvider = fClient.getUserInterface().getDimensionProvider();

					if (dimensionProvider.isPitchPortrait()) {
						offsetX = -1;
					}

					Dimension dimension = dimensionProvider.mapToLocal(coordinate.getX() + offsetX, coordinate.getY() + offsetY, false);
					UtilClientMarker.showMarkerPopup(getClient(), player, dimension.width, dimension.height);

				} else {
					UtilClientMarker.showMarkerPopup(getClient(), coordinate);
				}
			} else {
				if (isClickable()) {
					hideSelectSquare();
					String rightClickProperty = getClient().getProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION);
					if (getClient().getGame().getActingPlayer().getPlayer() != null
						&& pMouseEvent.getButton() == MouseEvent.BUTTON3 && IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON.equals(rightClickProperty)) {
						if (getClient().getGame().getTurnMode().allowEndPlayerAction()) {
							if (getClient().getGame().getFieldModel() != null) {
								getClient().getGame().getFieldModel().setRangeRuler(null);
								getClient().getUserInterface().getFieldComponent().refresh();
							}
							getClient().getCommunication().sendActingPlayer(null, null, false);
						}
					} else if (player != null && (pMouseEvent.getButton() != MouseEvent.BUTTON3 || ALLOW_RIGHT_CLICK_ON_PLAYER.contains(rightClickProperty))) {
						clickOnPlayer(player);
					} else if (pMouseEvent.getButton() != MouseEvent.BUTTON3 || IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE.equals(rightClickProperty)) {
						clickOnField(coordinate);
					}
				}
			}
		}
	}

	public void createPopupMenu(JMenuItem[] pMenuItems) {
		fPopupMenu = new JPopupMenu();
		for (JMenuItem pMenuItem : pMenuItems) {
			pMenuItem.addActionListener(this);
			fPopupMenu.add(pMenuItem);
		}
	}

	public void showPopupMenuForPlayer(Player<?> pPlayer) {
		if ((pPlayer != null) && (fPopupMenu != null)) {
			fPopupMenuPlayer = pPlayer;
			FieldCoordinate coordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(fPopupMenuPlayer);
			if (coordinate != null) {
				hideSelectSquare();
				int offsetX = 1, offsetY = 1;
				DimensionProvider dimensionProvider = fClient.getUserInterface().getDimensionProvider();

				if (dimensionProvider.isPitchPortrait()) {
					offsetX = -1;
				}
				Dimension dimension = dimensionProvider.mapToLocal(coordinate.getX() + offsetX, coordinate.getY() + offsetY, false);
				fPopupMenu.show(fClient.getUserInterface().getFieldComponent(), dimension.width, dimension.height);
			}
		}
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		JMenuItem menuItem = (JMenuItem) (pActionEvent.getSource());
		menuItemSelected(fPopupMenuPlayer, menuItem.getMnemonic());
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		if (getClient().getClientData().getSelectedPlayer() != pPlayer) {
			getClient().getClientData().setSelectedPlayer(pPlayer);
			getClient().getUserInterface().refreshSideBars();
		}
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		if (getClient().getClientData().getSelectedPlayer() != null) {
			getClient().getClientData().setSelectedPlayer(null);
			getClient().getUserInterface().refreshSideBars();
		}
		return true;
	}

	protected void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
	}

	public void setClickable(boolean pClickable) {
		fClickable = pClickable;
	}

	public boolean isClickable() {
		return (fClickable && getClient().getUserInterface().getDialogManager().isDialogHidden() && (fPopupMenu == null || !fPopupMenu.isVisible()));
	}

	public boolean isSelectable() {
		return fPopupMenu == null || !fPopupMenu.isVisible();
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		GameMenuBar gameMenuBar = fClient.getUserInterface().getGameMenuBar();
		switch (pActionKey) {
			case RESIZE_LARGER:
				gameMenuBar.increaseScaling();
				return true;
			case RESIZE_RESET:
				gameMenuBar.resetScaling();
				return true;
			case RESIZE_SMALLER:
			case RESIZE_SMALLER2:
				gameMenuBar.decreaseScaling();
				return true;
			default:
				break;
		}
		return false;
	}

	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		return false;
	}

	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		return false;
	}

	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		return false;
	}

	public void endTurn() {
	}

	protected boolean isHypnoticGazeActionAvailable(boolean declareAtStart, Player<?> player, ISkillProperty property) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		return ((mechanic.declareGazeActionAtStart() == declareAtStart)
			&& mechanic.isGazeActionAllowed(game.getTurnMode(), actingPlayer.getPlayerAction())
			&& UtilPlayer.canGaze(game, player, property));
	}

	protected boolean isTreacherousAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isTreacherousAvailable(actingPlayer.getPlayer());
	}

	protected boolean isTreacherousAvailable(Player<?> player) {
		Game game = getClient().getGame();
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canStabTeamMateForBall)
			&& Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getActingTeam(), game.getFieldModel().getPlayerCoordinate(player)))
			.anyMatch(adjacentPlayer -> UtilPlayer.hasBall(game, adjacentPlayer));
	}

	protected JMenuItem createTreacherousItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Treacherous",
				createMenuIcon(iconCache, IIconProperty.ACTION_STAB), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_TREACHEROUS);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_TREACHEROUS, 0));
		return menuItem;
	}

	public ImageIcon createMenuIcon(IconCache iconCache, String iconProperty) {
		return new ImageIcon(iconCache.getIconByProperty(iconProperty, RenderContext.ON_PITCH));
	}

	protected boolean isCatchOfTheDayAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isCatchOfTheDayAvailable(actingPlayer.getPlayer());
	}

	protected boolean isCatchOfTheDayAvailable(Player<?> player) {
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		FieldCoordinate ballCoordinate = game.getFieldModel().getBallCoordinate();

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGetBallOnGround)
			&& game.getFieldModel().isBallMoving() && playerCoordinate.distanceInSteps(ballCoordinate) <= 3;
	}

	protected JMenuItem createCatchOfTheDayItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Catch of the Day",
				createMenuIcon(iconCache, IIconProperty.ACTION_CATCH_OF_THE_DAY), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, 0));
		return menuItem;
	}


	protected boolean isWisdomAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isWisdomAvailable(actingPlayer.getPlayer());
	}

	protected boolean isWisdomAvailable(Player<?> player) {
		Game game = getClient().getGame();

		Set<Skill> ownedSkills = player.getSkillsIncludingTemporaryOnes();

		boolean canGainSkill = Constant.getGrantAbleSkills(game.getFactory(FactoryType.Factory.SKILL)).stream()
			.map(SkillWithValue::getSkill)
			.anyMatch(skillClass -> !ownedSkills.contains(skillClass));

		return canGainSkill && Arrays.stream(UtilPlayer.findAdjacentPlayersWithTacklezones(game, player.getTeam(),
				game.getFieldModel().getPlayerCoordinate(player), false))
			.anyMatch(teamMate -> teamMate.hasSkillProperty(NamedProperties.canGrantSkillsToTeamMates) && !teamMate.isUsed(NamedProperties.canGrantSkillsToTeamMates));
	}

	protected JMenuItem createWisdomItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Wisdom of the White Dwarf",
			createMenuIcon(iconCache, IIconProperty.ACTION_WISDOM), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_WISDOM);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_WISDOM, 0));
		return menuItem;
	}

	protected void addEndActionLabel(IconCache iconCache, List<JMenuItem> menuItemList) {
		String endMoveActionLabel = playerActivationUsed() ? "End Action" : deselectPlayerLabel();
		JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), endMoveActionLabel,
			createMenuIcon(iconCache, IIconProperty.ACTION_END_MOVE), RenderContext.ON_PITCH);
		endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
		endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
		menuItemList.add(endMoveAction);
	}

	protected String deselectPlayerLabel() {
		return "Deselect Player";
	}

	protected boolean playerActivationUsed() {
		return getClient().getGame().getActingPlayer().hasActed();
	}

	protected boolean isRaidingPartyAvailable(ActingPlayer player) {
		return !player.hasActed() && isRaidingPartyAvailable(player.getPlayer());
	}

	protected boolean isRaidingPartyAvailable(Player<?> player) {
		Game game = getClient().getGame();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canMoveOpenTeamMate)
			&& Arrays.stream(game.getActingTeam().getPlayers()).anyMatch(
			teamMate -> {
				FieldCoordinate teamMateCoordinate = fieldModel.getPlayerCoordinate(teamMate);
				Player<?>[] adjacentPlayersWithTacklezones = UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(game.getActingTeam()), teamMateCoordinate, false);
				FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(teamMateCoordinate, FieldCoordinateBounds.FIELD,
					1, false);
				return fieldModel.getPlayerState(teamMate).getBase() == PlayerState.STANDING
					&& teamMateCoordinate.distanceInSteps(playerCoordinate) <= 5
					&& !ArrayTool.isProvided(adjacentPlayersWithTacklezones)
					&& Arrays.stream(adjacentCoordinates).anyMatch(adjacentCoordinate -> {
					List<Player<?>> playersOnSquare = fieldModel.getPlayers(adjacentCoordinate);
					return (playersOnSquare == null || playersOnSquare.isEmpty())
						&& Arrays.stream(fieldModel.findAdjacentCoordinates(adjacentCoordinate, FieldCoordinateBounds.FIELD,
						1, false)).anyMatch(fieldCoordinate -> {
						List<Player<?>> players = game.getFieldModel().getPlayers(fieldCoordinate);
						return players != null && !players.isEmpty() && !game.getActingTeam().hasPlayer(players.get(0));
					});
				});
			}
		);
	}

	protected JMenuItem createRaidingPartyItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Raiding Party",
			createMenuIcon(iconCache, IIconProperty.ACTION_RAIDING_PARTY), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, 0));
		return menuItem;
	}

	protected boolean isLookIntoMyEyesAvailable(ActingPlayer actingPlayer) {
		PlayerState oldPlayerState = actingPlayer.getOldPlayerState();
		boolean hadTackleZone = oldPlayerState != null && oldPlayerState.hasTacklezones();
		return !actingPlayer.hasActed() && hadTackleZone && isLookIntoMyEyesAvailable(actingPlayer.getPlayer());
	}

	protected boolean isLookIntoMyEyesAvailable(Player<?> player) {
		Game game = getClient().getGame();
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
			&& Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(player.getTeam()), game.getFieldModel().getPlayerCoordinate(player)))
			.anyMatch(opponent -> UtilPlayer.hasBall(game, opponent));
	}

	protected JMenuItem createLookIntoMyEyesItem(IconCache iconCache) {
		JMenuItem lookItem = new JMenuItem(dimensionProvider(), "Look Into My Eyes",
			createMenuIcon(iconCache, IIconProperty.ACTION_LOOK_INTO_MY_EYES), RenderContext.ON_PITCH);
		lookItem.setMnemonic(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
		lookItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, 0));
		return lookItem;
	}

	protected boolean isBalefulHexAvailable(ActingPlayer player) {
		return !player.hasActed() && isBalefulHexAvailable(player.getPlayer());
	}

	protected boolean isBalefulHexAvailable(Player<?> player) {
		Game game = getClient().getGame();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canMakeOpponentMissTurn)
			&& Arrays.stream(game.getOtherTeam(game.getActingTeam()).getPlayers()).anyMatch(
			opponent -> fieldModel.getPlayerCoordinate(opponent).distanceInSteps(playerCoordinate) <= 5
		);
	}

	protected JMenuItem createBalefulHexItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Baleful Hex",
			createMenuIcon(iconCache, IIconProperty.ACTION_BALEFUL_HEX), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, 0));
		return menuItem;
	}

	protected boolean isBlackInkAvailable(ActingPlayer player) {
		return !player.hasActed() && !player.isStandingUp() && isBlackInkAvailable(player.getPlayer());
	}

	protected boolean isBlackInkAvailable(Player<?> player) {
		Game game = getClient().getGame();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGazeAutomatically)
			&& ArrayTool.isProvided(UtilPlayer
			.findAdjacentStandingOrPronePlayers(game, game.getOtherTeam(game.getActingTeam()), playerCoordinate));
	}

	protected JMenuItem createBlackInkItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(dimensionProvider(), "Black Ink",
			createMenuIcon(iconCache, IIconProperty.ACTION_GAZE), RenderContext.ON_PITCH);
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BLACK_INK);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLACK_INK, 0));
		return menuItem;
	}

	public boolean isThenIStartedBlastinAvailable(ActingPlayer player) {
		return !player.hasActed() && isThenIStartedBlastinAvailable(player.getPlayer());
	}

	protected boolean isThenIStartedBlastinAvailable(Player<?> player) {
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canBlastRemotePlayer) &&
			Arrays.stream(game.getOtherTeam(game.getActingTeam()).getPlayers()).anyMatch(
			opponent -> fieldModel.getPlayerCoordinate(opponent).distanceInSteps(playerCoordinate) <= 3);
	}

	protected JMenuItem createThenIStartedBlastinItem(IconCache iconCache) {
		JMenuItem blastinItem = new JMenuItem(dimensionProvider(), "\"Then I Started Blastin'!\"",
			createMenuIcon(iconCache, IIconProperty.ACTION_STARTED_BLASTIN), RenderContext.ON_PITCH);
		blastinItem.setMnemonic(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
		blastinItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, 0));
		return blastinItem;
	}


	public DimensionProvider dimensionProvider() {
		return getClient().getUserInterface().getDimensionProvider();
	}

}

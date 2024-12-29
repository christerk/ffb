package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.GameMenuBar;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientMarker;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.INetCommandHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

/**
 * @author Kalimar
 */
public abstract class ClientStateAwt<T extends LogicModule> extends ClientState<T, FantasyFootballClientAwt> implements INetCommandHandler, MouseListener, MouseMotionListener, ActionListener {

	private static final Set<String> ALLOW_RIGHT_CLICK_ON_PLAYER = new HashSet<String>() {{
		add(IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE);
		add(IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU);
	}};

	private boolean fClickable;
	private final UiDimensionProvider uiDimensionProvider;
	private final PitchDimensionProvider pitchDimensionProvider;
	private JPopupMenu fPopupMenu;

	private Player<?> fPopupMenuPlayer;

	public ClientStateAwt(FantasyFootballClientAwt pClient, T logicModule) {
		super(pClient, logicModule);
		setClickable(true);
		uiDimensionProvider = pClient.getUserInterface().getUiDimensionProvider();
		pitchDimensionProvider = pClient.getUserInterface().getPitchDimensionProvider();
	}

	public abstract ClientStateId getId();

	public void initUI() {
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

	// Helper Methods

	protected FieldCoordinate getFieldCoordinate(MouseEvent pMouseEvent) {
		FieldCoordinate coordinate = null;
		int x = pMouseEvent.getX();
		int y = pMouseEvent.getY();
		Dimension field = uiDimensionProvider.dimension(Component.FIELD);
		if ((x > 0) && (x < field.width) && (y > 0) && (y < field.height)) {
			coordinate = new FieldCoordinate((int) ((x / (pitchDimensionProvider.getLayoutSettings().getScale() * pitchDimensionProvider.getLayoutSettings().getLayout().getPitchScale())) / pitchDimensionProvider.unscaledFieldSquare()),
				(int) ((y / (pitchDimensionProvider.getLayoutSettings().getScale() * pitchDimensionProvider.getLayoutSettings().getLayout().getPitchScale())) / pitchDimensionProvider.unscaledFieldSquare()));
			coordinate = pitchDimensionProvider.mapToGlobal(coordinate);
		}
		return coordinate;
	}

	protected void drawSelectSquare() {
		drawSelectSquare(fSelectSquareCoordinate, new Color(0.0f, 0.0f, 1.0f, 0.2f));
	}

	protected void drawSelectSquare(FieldCoordinate pCoordinate, Color pColor) {
		if (pCoordinate != null) {
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();

			Dimension dimension = pitchDimensionProvider.mapToLocal(pCoordinate);
			int x = dimension.width + 1;
			int y = dimension.height + 1;
			Rectangle bounds = new Rectangle(x, y, pitchDimensionProvider.fieldSquareSize() - 2, pitchDimensionProvider.fieldSquareSize() - 2);

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
			Dimension dimension = pitchDimensionProvider.mapToLocal(fSelectSquareCoordinate);
			Rectangle bounds = new Rectangle(dimension.width, dimension.height, pitchDimensionProvider.fieldSquareSize(), pitchDimensionProvider.fieldSquareSize());
			fieldComponent.refresh(bounds);
			super.hideSelectSquare();
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
					Optional<Player<?>> player = logicModule.getPlayer(coordinate);
					selectable = player.map(this::mouseOverPlayer).orElseGet(() -> mouseOverField(coordinate));
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
			Optional<Player<?>> player = logicModule.getPlayer(coordinate);
			if (pMouseEvent.isShiftDown()) {
				hideSelectSquare();
				if (player.isPresent()) {
					int offsetX = 1, offsetY = 1;

					if (pitchDimensionProvider.isPitchPortrait()) {
						offsetX = -1;
					}

					Dimension dimension = pitchDimensionProvider.mapToLocal(coordinate.getX() + offsetX, coordinate.getY() + offsetY, false);
					UtilClientMarker.showMarkerPopup(getClient(), player.get(), dimension.width, dimension.height);

				} else {
					UtilClientMarker.showMarkerPopup(getClient(), coordinate);
				}
			} else {
				if (isClickable()) {
					hideSelectSquare();
					String rightClickProperty = getClient().getProperty(CommonProperty.SETTING_RIGHT_CLICK_END_ACTION);
					if (logicModule.getActingPlayer().getPlayer() != null
						&& pMouseEvent.getButton() == MouseEvent.BUTTON3 && IClientPropertyValue.SETTING_RIGHT_CLICK_END_ACTION_ON.equals(rightClickProperty)) {
						if (logicModule.endPlayerActivation()) {
							getClient().getUserInterface().getFieldComponent().refresh();
						}
					} else if (player.isPresent() && (pMouseEvent.getButton() != MouseEvent.BUTTON3 || ALLOW_RIGHT_CLICK_ON_PLAYER.contains(rightClickProperty))) {
						clickOnPlayer(player.get());
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
			FieldCoordinate coordinate = logicModule.getCoordinate(fPopupMenuPlayer);
			if (coordinate != null) {
				hideSelectSquare();
				int offsetX = 1, offsetY = 1;

				if (pitchDimensionProvider.isPitchPortrait()) {
					offsetX = -1;
				}
				Dimension dimension = pitchDimensionProvider.mapToLocal(coordinate.getX() + offsetX, coordinate.getY() + offsetY, false);
				fPopupMenu.show(getClient().getUserInterface().getFieldComponent(), dimension.width, dimension.height);
			}
		}
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		JMenuItem menuItem = (JMenuItem) (pActionEvent.getSource());
		menuItemSelected(fPopupMenuPlayer, menuItem.getMnemonic());
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
	}

	protected void clickOnPlayer(@SuppressWarnings("unused") Player<?> pPlayer) {
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		if (getClient().getClientData().getSelectedPlayer() != pPlayer) {
			getClient().getClientData().setSelectedPlayer(pPlayer);
			getClient().getUserInterface().refreshSideBars();
		}
		return true;
	}

	protected boolean mouseOverField(@SuppressWarnings("unused") FieldCoordinate pCoordinate) {
		resetSidebars();
		return true;
	}

	protected void resetSidebars() {
		if (getClient().getClientData().getSelectedPlayer() != null) {
			getClient().getClientData().setSelectedPlayer(null);
			getClient().getUserInterface().refreshSideBars();
		}
	}

	public final void menuItemSelected(Player<?> player, int pMenuKey) {
		prePerform(pMenuKey);
		ClientAction action = actionMapping().get(pMenuKey);
		if (action != null) {
			logicModule.perform(player, action);
		}
		postPerform(pMenuKey);
	}

	protected abstract Map<Integer, ClientAction> actionMapping();

	protected Map<Integer, ClientAction> genericBlockMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
			put(IPlayerPopupMenuKeys.KEY_STAB, ClientAction.STAB);
			put(IPlayerPopupMenuKeys.KEY_CHAINSAW, ClientAction.CHAINSAW);
			put(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, ClientAction.PROJECTILE_VOMIT);
			put(IPlayerPopupMenuKeys.KEY_TREACHEROUS, ClientAction.TREACHEROUS);
			put(IPlayerPopupMenuKeys.KEY_WISDOM, ClientAction.WISDOM);
			put(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, ClientAction.RAIDING_PARTY);
			put(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, ClientAction.LOOK_INTO_MY_EYES);
			put(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, ClientAction.BALEFUL_HEX);
			put(IPlayerPopupMenuKeys.KEY_BLACK_INK, ClientAction.BLACK_INK);
			put(IPlayerPopupMenuKeys.KEY_BREATHE_FIRE, ClientAction.BREATHE_FIRE);
			put(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, ClientAction.THEN_I_STARTED_BLASTIN);
		}};
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

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean actionKeyPressed(ActionKey pActionKey) {
		GameMenuBar gameMenuBar = getClient().getUserInterface().getGameMenuBar();
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

	public boolean isInitDragAllowed(@SuppressWarnings("unused") FieldCoordinate pCoordinate) {
		return false;
	}

	public boolean isDragAllowed(@SuppressWarnings("unused") FieldCoordinate pCoordinate) {
		return false;
	}

	public boolean isDropAllowed(@SuppressWarnings("unused") FieldCoordinate pCoordinate) {
		return false;
	}


	protected JMenuItem createTreacherousItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(pitchDimensionProvider, "Treacherous",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAB, pitchDimensionProvider)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_TREACHEROUS);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_TREACHEROUS, 0));
		return menuItem;
	}

	protected JMenuItem createCatchOfTheDayItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(pitchDimensionProvider, "Catch of the Day",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_CATCH_OF_THE_DAY, pitchDimensionProvider)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY, 0));
		return menuItem;
	}

	protected JMenuItem createWisdomItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(pitchDimensionProvider, "Wisdom of the White Dwarf",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_WISDOM, pitchDimensionProvider)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_WISDOM);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_WISDOM, 0));
		return menuItem;
	}

	protected void addEndActionLabel(IconCache iconCache, List<JMenuItem> menuItemList) {
		String endMoveActionLabel = logicModule.playerActivationUsed() ? "End Action" : deselectPlayerLabel();
		JMenuItem endMoveAction = new JMenuItem(pitchDimensionProvider, endMoveActionLabel,
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE, pitchDimensionProvider)));
		endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
		endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
		menuItemList.add(endMoveAction);
	}

	protected JMenuItem createRaidingPartyItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(pitchDimensionProvider, "Raiding Party",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_RAIDING_PARTY, pitchDimensionProvider)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RAIDING_PARTY, 0));
		return menuItem;
	}

	protected JMenuItem createLookIntoMyEyesItem(IconCache iconCache) {
		JMenuItem lookItem = new JMenuItem(pitchDimensionProvider, "Look Into My Eyes",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_LOOK_INTO_MY_EYES, pitchDimensionProvider)));
		lookItem.setMnemonic(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
		lookItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES, 0));
		return lookItem;
	}

	protected JMenuItem createBalefulHexItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(pitchDimensionProvider, "Baleful Hex",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BALEFUL_HEX, pitchDimensionProvider)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BALEFUL_HEX, 0));
		return menuItem;
	}

	protected JMenuItem createBlackInkItem(IconCache iconCache) {
		JMenuItem menuItem = new JMenuItem(pitchDimensionProvider, "Black Ink",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE, pitchDimensionProvider)));
		menuItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BLACK_INK);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLACK_INK, 0));
		return menuItem;
	}

	protected JMenuItem createThenIStartedBlastinItem(IconCache iconCache) {
		JMenuItem blastinItem = new JMenuItem(pitchDimensionProvider, "\"Then I Started Blastin'!\"",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STARTED_BLASTIN, pitchDimensionProvider)));
		blastinItem.setMnemonic(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
		blastinItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN, 0));
		return blastinItem;
	}

	public ImageIcon createMenuIcon(IconCache iconCache, String iconProperty) {
		return new ImageIcon(iconCache.getIconByProperty(iconProperty, pitchDimensionProvider));
	}

	protected DimensionProvider dimensionProvider() {
		return pitchDimensionProvider;
	}

	protected String deselectPlayerLabel() {
		return "Deselect Player";
	}

	protected void determineCursor(InteractionResult result) {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		evaluateCursorResult(result).ifPresent(property ->
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), property));
	}

	private Optional<String> evaluateCursorResult(InteractionResult result) {
		switch (result.getKind()) {
			case PERFORM:
				return Optional.ofNullable(validCursor());
			case INVALID:
				return Optional.of(invalidCursor());
			default:
				return Optional.empty();
		}
	}

	protected String validCursor() {
		return null;
	}

	protected String invalidCursor() {
		return null;
	}

}

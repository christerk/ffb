package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.CoordinateConverter;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.GameMenuBar;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientMarker;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.INetCommandHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public abstract class ClientStateAwt<T extends LogicModule> extends ClientState<T, FantasyFootballClientAwt> implements INetCommandHandler, MouseListener, MouseMotionListener, ActionListener {

	private static final Set<String> ALLOW_RIGHT_CLICK_ON_PLAYER = new HashSet<String>() {{
		add(IClientPropertyValue.SETTING_RIGHT_CLICK_LEGACY_MODE);
		add(IClientPropertyValue.SETTING_RIGHT_CLICK_OPENS_CONTEXT_MENU);
	}};

	private boolean fClickable;
	private final PitchDimensionProvider pitchDimensionProvider;
	private JPopupMenu fPopupMenu;
	private final CoordinateConverter coordinateConverter;

	private Player<?> fPopupMenuPlayer;

	public ClientStateAwt(FantasyFootballClientAwt pClient, T logicModule) {
		super(pClient, logicModule);
		setClickable(true);
		pitchDimensionProvider = pClient.getUserInterface().getPitchDimensionProvider();
		coordinateConverter = pClient.getUserInterface().getCoordinateConverter();
	}

	public void setUp() {
		super.setUp();
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getDialogManager().updateDialog();
		UtilClientCursor.setDefaultCursor(userInterface);
	}

	public void tearDown() {
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		super.tearDown();
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
			FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(pMouseEvent);
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
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(pMouseEvent);
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

	public void clickOnField(FieldCoordinate pCoordinate) {
	}

	public void clickOnPlayer(@SuppressWarnings("unused") Player<?> pPlayer) {
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		if (getClient().getClientData().getSelectedPlayer() != pPlayer) {
			getClient().getClientData().setSelectedPlayer(pPlayer);
			getClient().getUserInterface().refreshSideBars();
		}
		return true;
	}

	public boolean mouseOverField(@SuppressWarnings("unused") FieldCoordinate pCoordinate) {
		resetSidebars();
		return true;
	}

	protected ClientStateAwt<? extends LogicModule> getDelegate(InteractionResult result) {
		return getClient().getClientState(result.getDelegate());
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
		return handleResize(pActionKey);
	}

	protected boolean handleResize(ActionKey pActionKey) {
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

	public ImageIcon createMenuIcon(IconCache iconCache, String iconProperty) {
		return new ImageIcon(iconCache.getIconByProperty(iconProperty, pitchDimensionProvider));
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

	protected JMenuItem menuItem(MenuItemConfig config) {
		JMenuItem item = new JMenuItem(pitchDimensionProvider, config.getTitle(),
			createMenuIcon(getClient().getUserInterface().getIconCache(), config.getIconProperty()));
		item.setMnemonic(config.getKeyEvent());
		item.setAccelerator(KeyStroke.getKeyStroke(config.getKeyEvent(), 0));
		return item;
	}

	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {
		return new LinkedHashMap<>();
	}

	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		return new HashMap<>();
	}

	protected List<JMenuItem> menuItems(ActionContext actionContext) {
		Map<ClientAction, MenuItemConfig> configs = new HashMap<>(itemConfigs(actionContext));

		influencedItemConfigs().entrySet().stream()
			.filter(entry -> actionContext.getInfluences().contains(entry.getKey())) // filter for influences present in current context
			.flatMap(entry -> entry.getValue().entrySet().stream() // only allow actions defined in influence to be affected
				.filter(influenceEntry -> entry.getKey().getInfluencedActions().contains(influenceEntry.getKey())))
			.filter(entry -> configs.containsKey(entry.getKey()))
			.forEach(entry -> configs.put(entry.getKey(), entry.getValue()));

		return actionContext.getActions().stream().map(configs::get).map(this::menuItem).collect(Collectors.toList());
	}

	protected void createAndShowPopupMenuForPlayer(Player<?> pPlayer, ActionContext actionContext) {
		createAndShowPopupMenuForPlayer(pPlayer, actionContext, new ArrayList<>());
	}

	protected void createAndShowPopupMenuForPlayer(Player<?> pPlayer, ActionContext actionContext, List<JMenuItem> prepopulated) {
		List<JMenuItem> menuItemList = new ArrayList<>();
		menuItemList.addAll(prepopulated);
		menuItemList.addAll(menuItems(actionContext));
		if (!menuItemList.isEmpty()) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	protected List<JMenuItem> uiOnlyMenuItems() {
		return new ArrayList<>();
	}

	protected void createAndShowPopupMenuForActingPlayer(ActionContext actionContext) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();

		ActingPlayer actingPlayer = game.getActingPlayer();
		List<JMenuItem> menuItemList = new ArrayList<>(uiOnlyMenuItems());
		createAndShowPopupMenuForPlayer(actingPlayer.getPlayer(), actionContext, menuItemList);
	}

}

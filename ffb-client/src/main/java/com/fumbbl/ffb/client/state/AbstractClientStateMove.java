package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.*;

/**
 * @author Kalimar
 */
public abstract class AbstractClientStateMove<T extends MoveLogicModule> extends ClientStateAwt<T> {

	protected AbstractClientStateMove(FantasyFootballClientAwt pClient, T logicModule) {
		super(pClient, logicModule);
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		return evaluateHover(result);
	}

	protected boolean evaluateHover(InteractionResult result) {
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		switch (result.getKind()) {
			case PERFORM:
				if (result.getMoveSquare() != null) {
					setCustomCursor(result.getMoveSquare());
				} else if (ArrayTool.isProvided(result.getPath())) {
					UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_MOVE);
					Game game = getClient().getGame();
					ActingPlayer actingPlayer = game.getActingPlayer();
					fieldComponent.getLayerUnderPlayers().drawMovePath(result.getPath(), actingPlayer.getCurrentMove());
					fieldComponent.refresh();
				}
				break;
			case RESET:
				UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
				break;
			default:
				break;
		}
		return super.mouseOverField(null);
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


	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		evaluateClick(result, player);
	}

	protected void evaluateClick(InteractionResult result, Player<?> player) {
		switch (result.getKind()) {
			case SELECT_ACTION:
				createAndShowPopupMenuForActingPlayer(result.getActionContext());
				break;
			case PERFORM:
				playerWasMoved();
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
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

	@Override
	protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
		Map<Influences, Map<ClientAction, MenuItemConfig>> influences = new HashMap<>();
		Map<ClientAction, MenuItemConfig> jump = new HashMap<>();
		influences.put(Influences.IS_JUMPING, jump);
		jump.put(ClientAction.JUMP, new MenuItemConfig("Don't Jump", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_JUMP));
		Map<ClientAction, MenuItemConfig> hasActed = new HashMap<>();
		influences.put(Influences.HAS_ACTED, hasActed);
		hasActed.put(ClientAction.END_MOVE, new MenuItemConfig("End Action", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		Map<ClientAction, MenuItemConfig> putrid = new HashMap<>();
		influences.put(Influences.VOMIT_DUE_TO_PUTRID_REGURGITATION, putrid);
		putrid.put(ClientAction.PROJECTILE_VOMIT, new MenuItemConfig("Putrid Regurgitation", IIconProperty.ACTION_VOMIT, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT));
		return influences;
	}

	@Override
	protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs(ActionContext actionContext) {

		Player<?> selectedPlayer = getClient().getClientData().getSelectedPlayer();

		LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

		itemConfigs.put(ClientAction.PASS, new MenuItemConfig("Pass Ball (any square)", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_PASS));
		itemConfigs.put(ClientAction.MOVE, new MenuItemConfig("Move", IIconProperty.ACTION_MOVE, IPlayerPopupMenuKeys.KEY_MOVE));
		itemConfigs.put(ClientAction.JUMP, new MenuItemConfig("Jump", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_JUMP));
		itemConfigs.put(ClientAction.BOUNDING_LEAP, new MenuItemConfig("Jump (Bounding Leap)", IIconProperty.ACTION_JUMP, IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP));
		itemConfigs.put(ClientAction.GAZE, new MenuItemConfig("Hypnotic Gaze", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_GAZE));
		itemConfigs.put(ClientAction.FUMBLEROOSKIE, new MenuItemConfig("Fumblerooskie", IIconProperty.ACTION_PASS, IPlayerPopupMenuKeys.KEY_FUMBLEROOSKIE));
		itemConfigs.put(ClientAction.END_MOVE, new MenuItemConfig("Deselect Player", IIconProperty.ACTION_END_MOVE, IPlayerPopupMenuKeys.KEY_END_MOVE));
		itemConfigs.put(ClientAction.TREACHEROUS, new MenuItemConfig("Treacherous", IIconProperty.ACTION_STAB, IPlayerPopupMenuKeys.KEY_TREACHEROUS));
		itemConfigs.put(ClientAction.WISDOM, new MenuItemConfig("Wisdom of the White Dwarf", IIconProperty.ACTION_WISDOM, IPlayerPopupMenuKeys.KEY_WISDOM));
		itemConfigs.put(ClientAction.RAIDING_PARTY, new MenuItemConfig("Raiding Party", IIconProperty.ACTION_RAIDING_PARTY, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY));
		itemConfigs.put(ClientAction.LOOK_INTO_MY_EYES, new MenuItemConfig("Look Into My Eyes", IIconProperty.ACTION_LOOK_INTO_MY_EYES, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES));
		itemConfigs.put(ClientAction.BALEFUL_HEX, new MenuItemConfig("Baleful Hex", IIconProperty.ACTION_BALEFUL_HEX, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX));
		itemConfigs.put(ClientAction.PROJECTILE_VOMIT, new MenuItemConfig("Putrid Regurgitation", IIconProperty.ACTION_VOMIT, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT));
		itemConfigs.put(ClientAction.BLACK_INK, new MenuItemConfig("Black Ink", IIconProperty.ACTION_GAZE, IPlayerPopupMenuKeys.KEY_BLACK_INK));
		itemConfigs.put(ClientAction.CATCH_OF_THE_DAY, new MenuItemConfig("Catch of the Day", IIconProperty.ACTION_CATCH_OF_THE_DAY, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY));
		itemConfigs.put(ClientAction.THEN_I_STARTED_BLASTIN,
			new MenuItemConfig(selectedPlayer, NamedProperties.canBlastRemotePlayer, IIconProperty.ACTION_STARTED_BLASTIN, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN));

		return itemConfigs;
	}

	@Override
	protected List<JMenuItem> uiOnlyMenuItems() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		List<JMenuItem> menuItems = new ArrayList<>();
		if (logicModule.performsRangeGridAction(actingPlayer, game)) {
			menuItems.add(menuItem(new MenuItemConfig("Range Grid on/off", IIconProperty.ACTION_TOGGLE_RANGE_GRID, IPlayerPopupMenuKeys.KEY_RANGE_GRID)));
		}
		return menuItems;
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
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
					clickOnPlayer(player);
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
				case PLAYER_ACTION_THEN_I_STARTED_BLASTIN:
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

	protected void showShortestPath(FieldCoordinate pCoordinate, FieldComponent fieldComponent,
																	ActingPlayer actingPlayer) {
		FieldCoordinate[] shortestPath = logicModule.findShortestPath(pCoordinate);
		if (ArrayTool.isProvided(shortestPath)) {
			fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
			fieldComponent.refresh();
		}
	}
}



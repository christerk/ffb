package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
import com.fumbbl.ffb.client.state.logic.ThrowTeamMateLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilRangeRuler;

/**
 *
 * @author Kalimar
 */
public class ClientStateThrowTeamMate extends AbstractClientStateMove<ThrowTeamMateLogicModule> {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	protected ClientStateThrowTeamMate(FantasyFootballClientAwt pClient) {
		super(pClient, new ThrowTeamMateLogicModule(pClient));
		fRangeGridHandler = new RangeGridHandler(pClient, true);
	}

	@Override
	public void setUp() {
		super.setUp();
		markThrowablePlayers();
		fRangeGridHandler.refreshSettings();
	}

	public void clickOnPlayer(Player<?> player) {
		UserInterface userInterface = getClient().getUserInterface();

		InteractionResult result = logicModule.playerInteraction(player);

		switch (result.getKind()) {
			case PERFORM:
				fShowRangeRuler = true;
				break;
			case HANDLED:
				fShowRangeRuler = false;
				markThrowablePlayers();
				userInterface.getFieldComponent().refresh();
				break;
			default:
				super.evaluateClick(result, player);
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
				fShowRangeRuler = false;
				userInterface.getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case PREVIEW_THROW:
				drawRangeRuler(pCoordinate);
				// fall through
			case RESET:
				resetSidebars();
				return true;
			default:
				return evaluateHover(result);
		}
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();

		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case PREVIEW_THROW:
				drawRangeRuler(game.getFieldModel().getPlayerCoordinate(pPlayer));
				break;
			default:
				determineCursor(result);
				break;
		}
		userInterface.refreshSideBars();
		return true;
	}

	private void drawRangeRuler(FieldCoordinate pCoordinate) {
		RangeRuler rangeRuler = null;
		if (fShowRangeRuler) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			UserInterface userInterface = getClient().getUserInterface();
			FieldComponent fieldComponent = userInterface.getFieldComponent();
			if (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE) {
				rangeRuler = UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), pCoordinate, true);
			}
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

	private void markThrowablePlayers() {
		Game game = getClient().getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?>[] throwablePlayers = mechanic.findThrowableTeamMates(game, actingPlayer.getPlayer());
		if ((game.getDefender() == null) && ArrayTool.isProvided(throwablePlayers)) {
			userInterface.getFieldComponent().getLayerRangeRuler().markPlayers(throwablePlayers,
					FieldLayerRangeRuler.COLOR_THROWABLE_PLAYER);
		} else {
			userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		}
		userInterface.getFieldComponent().refresh();
	}

	@Override
	protected void postPerform(int menuKey) {
		switch (menuKey) {
			case IPlayerPopupMenuKeys.KEY_RANGE_GRID:
				fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
				fRangeGridHandler.refreshRangeGrid();
				break;
			default:
				break;
		}
	}

	@Override
	public void handleCommand(NetCommand pNetCommand) {
		fRangeGridHandler.refreshRangeGrid();
		super.handleCommand(pNetCommand);
	}

	@Override
	public void tearDown() {
		fRangeGridHandler.setShowRangeGrid(false);
		fRangeGridHandler.refreshRangeGrid();
		// clear marked players
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
		super.tearDown();
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == ActionKey.PLAYER_ACTION_RANGE_GRID) {
			menuItemSelected(null, IPlayerPopupMenuKeys.KEY_RANGE_GRID);
			return true;
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}

}

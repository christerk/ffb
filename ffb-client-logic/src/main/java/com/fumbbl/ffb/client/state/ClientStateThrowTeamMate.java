package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
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
public class ClientStateThrowTeamMate extends ClientStateMove {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	protected ClientStateThrowTeamMate(FantasyFootballClient pClient) {
		super(pClient);
		fRangeGridHandler = new RangeGridHandler(pClient, true);
	}

	public ClientStateId getId() {
		return ClientStateId.THROW_TEAM_MATE;
	}

	public void enterState() {
		super.enterState();
		markThrowablePlayers();
		fRangeGridHandler.refreshSettings();
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			if ((game.getDefender() == null) && canBeThrown(pPlayer)) {
				fShowRangeRuler = true;
				getClient().getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(), pPlayer.getId());
			}
			if (game.getDefender() != null) {
				fShowRangeRuler = false;
				markThrowablePlayers();
				game.getFieldModel().setRangeRuler(null);
				userInterface.getFieldComponent().refresh();
				getClient().getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(),
						game.getFieldModel().getPlayerCoordinate(pPlayer));
			}
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		if (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE) {
			super.clickOnField(pCoordinate);
		} else {
			fShowRangeRuler = false;
			game.getFieldModel().setRangeRuler(null);
			userInterface.getFieldComponent().refresh();
			getClient().getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(), pCoordinate);
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		if ((game.getDefender() != null) && (game.getPassCoordinate() == null)) {
			drawRangeRuler(pCoordinate);
		}
		return super.mouseOverField(pCoordinate);
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		if ((game.getDefender() == null) && (game.getPassCoordinate() == null)) {
			if (canBeThrown(pPlayer)) {
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
			} else {
				UtilClientCursor.setDefaultCursor(userInterface);
			}
		}
		if ((game.getDefender() != null) && (game.getPassCoordinate() == null)) {
			drawRangeRuler(game.getFieldModel().getPlayerCoordinate(pPlayer));
		}
		getClient().getClientData().setSelectedPlayer(pPlayer);
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

	private boolean canBeThrown(Player<?> pPlayer) {
		Game game = getClient().getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		// added a check so you could not throw the opponents players, maybe this should
		// be in the server-check?
		return mechanic.canThrow(actingPlayer.getPlayer())
			&& mechanic.canBeThrown(game, pPlayer)
			&& catcherCoordinate.isAdjacent(throwerCoordinate);
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
	public void handleCommand(NetCommand pNetCommand) {
		fRangeGridHandler.refreshRangeGrid();
		super.handleCommand(pNetCommand);
	}

	@Override
	public void leaveState() {
		fRangeGridHandler.setShowRangeGrid(false);
		fRangeGridHandler.refreshRangeGrid();
		// clear marked players
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (pMenuKey == IPlayerPopupMenuKeys.KEY_RANGE_GRID) {
			fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
			fRangeGridHandler.refreshRangeGrid();
		} else {
			super.menuItemSelected(player, pMenuKey);
		}
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

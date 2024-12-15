package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.RangeGridHandler;
import com.fumbbl.ffb.client.state.logic.bb2020.KickTeamMateLikeThrowLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilRangeRuler;

public class ClientStateKickTeamMateLikeThrow extends AbstractClientStateMove<KickTeamMateLikeThrowLogicModule> {

	private boolean fShowRangeRuler;
	private final RangeGridHandler fRangeGridHandler;

	public ClientStateKickTeamMateLikeThrow(FantasyFootballClientAwt pClient) {
		super(pClient, new KickTeamMateLikeThrowLogicModule(pClient));
		fRangeGridHandler = new RangeGridHandler(pClient, true);
	}

	public ClientStateId getId() {
		return ClientStateId.KICK_TEAM_MATE_THROW;
	}

	public void enterState() {
		super.enterState();
		markThrowablePlayers();
		fRangeGridHandler.refreshSettings();
	}


	protected void clickOnPlayer(Player<?> pPlayer) {
		UserInterface userInterface = getClient().getUserInterface();

		InteractionResult result = logicModule.playerInteraction(pPlayer);

		switch (result.getKind()) {
			case SUPER:
				super.clickOnPlayer(pPlayer);
				break;
			case PERFORM:
				fShowRangeRuler = true;
				break;
			case HANDLED:
				fShowRangeRuler = false;
				markThrowablePlayers();
				userInterface.getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		UserInterface userInterface = getClient().getUserInterface();

		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case SUPER:
				super.clickOnField(pCoordinate);
				break;
			case HANDLED:
				fShowRangeRuler = false;
				userInterface.getFieldComponent().refresh();
				break;
			default:
				break;
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case SUPER:
				return super.mouseOverField(pCoordinate);
			case SUPER_DRAW:
				drawRangeRuler(pCoordinate);
				return super.mouseOverField(pCoordinate);
			default:
				return false;
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();

		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case DRAW:
				drawRangeRuler(game.getFieldModel().getPlayerCoordinate(pPlayer));
				break;
			default:
				determineCursor(result);
				break;
		}
		userInterface.refreshSideBars();
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	private void drawRangeRuler(FieldCoordinate pCoordinate) {
		RangeRuler rangeRuler = null;
		if (fShowRangeRuler) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			UserInterface userInterface = getClient().getUserInterface();
			FieldComponent fieldComponent = userInterface.getFieldComponent();
			if (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE) {
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?>[] throwablePlayers = logicModule.findKickablePlayers(game, actingPlayer.getPlayer());
		if (ArrayTool.isProvided(throwablePlayers)) {
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

	public boolean actionKeyPressed(ActionKey pActionKey) {
		if (pActionKey == ActionKey.PLAYER_ACTION_RANGE_GRID) {
			fRangeGridHandler.setShowRangeGrid(!fRangeGridHandler.isShowRangeGrid());
			fRangeGridHandler.refreshRangeGrid();
			return true;
		} else {
			return super.actionKeyPressed(pActionKey);
		}
	}
}

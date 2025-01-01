package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.dialog.DialogTeamSetupParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandTeamSetupList;

import java.util.Collections;
import java.util.Set;

/**
 * @author Kalimar
 */
public class SetupLogicModule extends LogicModule {


	public SetupLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SETUP;
	}

	@Override
	public void postInit() {
		super.postInit();
		client.getClientData().clear();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	public void requestSetups() {
		client.getCommunication().sendTeamSetupLoad(null);
	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(useTurnMode() ? client.getGame().getTurnMode() : null, client.getGame().getTeamHome(), client.getGame().getFieldModel());
	}

	protected boolean useTurnMode() {
		return false;
	}

	public InteractionResult handleCommand(NetCommand pNetCommand, boolean loadDialog) {
		Game game = client.getGame();
		if (pNetCommand.getId() == NetCommandId.SERVER_TEAM_SETUP_LIST) {
			ServerCommandTeamSetupList setupListCommand = (ServerCommandTeamSetupList) pNetCommand;
			game.setDialogParameter(new DialogTeamSetupParameter(loadDialog, setupListCommand.getSetupNames()));
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public boolean squareIsEmpty(FieldCoordinate pCoordinate) {
		return client.getGame().getFieldModel().getPlayer(pCoordinate) == null;
	}

	public boolean squareIsValidForSetup(FieldCoordinate pCoordinate) {
		return FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate)
			|| (pCoordinate.getX() == FieldCoordinate.RSV_HOME_X);
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in setup context");
	}

}

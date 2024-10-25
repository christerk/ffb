package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
import com.fumbbl.ffb.client.state.ClientStateBlock;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientStateStab extends ClientStateBlock {
	private Player<?>[] targets;
	private List<String> targetIds;
	public ClientStateStab(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void enterState() {
		super.enterState();
		targets = findTargets();
		markTargets();
		targetIds = Arrays.stream(targets).map(Player::getId).collect(Collectors.toList());
	}

	@Override
	public void leaveState() {
		super.leaveState();
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
	}

	private Player<?>[] findTargets() {
		Game game = getClient().getGame();
		Player<?> player = game.getActingPlayer().getPlayer();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		return UtilPlayer.findAdjacentBlockablePlayers(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player));
	}

	private void markTargets() {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		if ((game.getDefender() == null) && ArrayTool.isProvided(targets)) {
			userInterface.getFieldComponent().getLayerRangeRuler().markPlayers(targets, FieldLayerRangeRuler.COLOR_THROWABLE_PLAYER);
		} else {
			userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		}
		userInterface.getFieldComponent().refresh();
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.STAB;
	}

	@Override
	protected void block(Player<?> player) {
		ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
		UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), player, true, false, false, false);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> player) {
		super.mouseOverPlayer(player);
		if (targetIds.contains(player.getId())) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLADE);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}
}

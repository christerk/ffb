package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
public class PuntLogicModule extends MoveLogicModule {

	public PuntLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PUNT;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		}
		return InteractionResult.ignore();
	}

	@Override
	protected boolean actionAvailable(Player<?> player, ActingPlayer actingPlayer, JumpMechanic mechanic, Game game,
		FieldCoordinate position) {
		return actingPlayer.hasActed()
			|| (actingPlayer.getPlayerAction() == PlayerAction.PUNT_MOVE && UtilPlayer.hasBall(game, player))
			|| actingPlayer.getPlayerAction() == PlayerAction.PUNT;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.PUNT_MOVE) {
			return InteractionResult.delegate(super.getId());
		} else {
			if ((PlayerAction.PUNT == actingPlayer.getPlayerAction()) && UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
				if (game.getFieldModel().getMoveSquare(pCoordinate) != null) {
					client.getCommunication().sendFieldCoordinate(pCoordinate);
					return InteractionResult.handled();
				}
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.PUNT_MOVE) {
			return InteractionResult.delegate(super.getId());
		}
		if (game.getFieldModel().getMoveSquare(pCoordinate) != null) {
			return InteractionResult.perform();
		}
		return InteractionResult.ignore();
	}


	@Override
	public Set<ClientAction> availableActions() {
		Set<ClientAction> actions = super.availableActions();
		actions.add(ClientAction.PUNT);
		return actions;
	}


	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ClientCommunication communication = client.getCommunication();
		switch (action) {
			case PUNT:
				communication.sendActingPlayer(player, PlayerAction.PUNT, actingPlayer.isJumping());
				break;
			default:
				super.performAvailableAction(player, action);
				break;
		}
	}


	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		Game game = client.getGame();

		if ((PlayerAction.PUNT_MOVE == actingPlayer.getPlayerAction()) &&
			UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
			actionContext.add(ClientAction.PUNT);
		}

		if (PlayerAction.PUNT == actingPlayer.getPlayerAction() && UtilPlayer.hasMoveLeft(game, actingPlayer.isJumping())) {
			actionContext.add(ClientAction.MOVE);
		}

		if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
			actionContext.add(ClientAction.JUMP);
			if (actingPlayer.isJumping()) {
				actionContext.add(Influences.IS_JUMPING);
			} else {
				Optional<Skill> boundingLeap = isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					actionContext.add(ClientAction.BOUNDING_LEAP);
				}
			}
		}

		if (isWisdomAvailable(actingPlayer)) {
			actionContext.add(ClientAction.WISDOM);
		}

		if (isRaidingPartyAvailable(actingPlayer)) {
			actionContext.add(ClientAction.RAIDING_PARTY);
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BALEFUL_HEX);
		}
		if (isBlackInkAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BLACK_INK);
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			actionContext.add(ClientAction.CATCH_OF_THE_DAY);
		}
		if (isFumblerooskieAvailable()) {
			actionContext.add(ClientAction.FUMBLEROOSKIE);
		}
		if (isZoatGazeAvailable(actingPlayer)) {
			actionContext.add(ClientAction.AUTO_GAZE_ZOAT);
		}
		actionContext.add(ClientAction.END_MOVE);
		if (actingPlayer.hasActed()) {
			actionContext.add(Influences.HAS_ACTED);
		}
		if (isIncorporealAvailable(actingPlayer)) {
			actionContext.add(ClientAction.INCORPOREAL);
			if (actingPlayer.getPlayer().hasActiveEnhancement(NamedProperties.canAvoidDodging)) {
				actionContext.add(Influences.INCORPOREAL_ACTIVE);
			}
		}
		return actionContext;
	}

}

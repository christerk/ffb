package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
public class PassLogicModule extends MoveLogicModule {

	public PassLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PASS;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			if (!actingPlayer.hasPassed() && (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()
				|| (UtilPlayer.hasBall(game, actingPlayer.getPlayer())
				&& ((PlayerAction.PASS == actingPlayer.getPlayerAction()) || canPlayerGetPass(player))))) {
				game.setPassCoordinate(game.getFieldModel().getPlayerCoordinate(player));
				client.getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
				game.getFieldModel().setRangeRuler(null);
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) {
			return InteractionResult.delegate(super.getId());
		} else {
			if ((PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction())
				|| UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
				game.setPassCoordinate(pCoordinate);
				client.getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
				game.getFieldModel().setRangeRuler(null);
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		client.getClientData().setSelectedPlayer(pPlayer);
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((PlayerAction.HAIL_MARY_PASS != actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
			FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			if ((PlayerAction.PASS == actingPlayer.getPlayerAction()) || canPlayerGetPass(pPlayer)) {
				return InteractionResult.previewThrow().with(catcherCoordinate);
			}
		} else {
			game.getFieldModel().setRangeRuler(null);
			if (actionIsHmp()) {
				return InteractionResult.perform();
			} else {
				return InteractionResult.reset();
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actionIsHmp()) {
			game.getFieldModel().setRangeRuler(null);
			return InteractionResult.perform();
		} else if (actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) {
			game.getFieldModel().setRangeRuler(null);
			return InteractionResult.delegate(super.getId());
		} else {
			return InteractionResult.previewThrow().with(pCoordinate);
		}
	}

	public boolean canPlayerGetPass(Player<?> pCatcher) {
		boolean canGetPass = false;
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((pCatcher != null) && (actingPlayer.getPlayer() != null)) {
			PlayerState catcherState = game.getFieldModel().getPlayerState(pCatcher);
			canGetPass = ((catcherState != null)
				&& catcherState.hasTacklezones() && (game.getTeamHome() == pCatcher.getTeam())
				&& (!actingPlayer.isSufferingAnimosity() || actingPlayer.getRace().equals(pCatcher.getRace())));
		}
		return canGetPass;
	}

	@Override
	public Set<ClientAction> availableActions() {
		Set<ClientAction> actions = super.availableActions();
		actions.add(ClientAction.HAIL_MARY_PASS);
		return actions;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ClientCommunication communication = client.getCommunication();
		switch (action) {
			case HAIL_MARY_PASS:
				if (isHailMaryPassActionAvailable()) {
					if (actionIsHmp()) {
						communication.sendActingPlayer(player, PlayerAction.PASS, actingPlayer.isJumping());
					} else {
						communication.sendActingPlayer(player, PlayerAction.HAIL_MARY_PASS, actingPlayer.isJumping());
						game.getFieldModel().setRangeRuler(null);
					}
				}
				break;
			default:
				super.performAvailableAction(player, action);
				break;
		}
	}

	public boolean actionIsHmp() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction();
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		Game game = client.getGame();

		if (isPassAnySquareAvailable(actingPlayer, game) && !actingPlayer.hasPassed()) {
			actionContext.add(ClientAction.PASS);
		}

		if (isHailMaryPassActionAvailable() && UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && !actingPlayer.hasPassed()) {
			if (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()) {
				actionContext.add(Influences.IS_THROWING_HAIL_MARY);
			}
			actionContext.add(ClientAction.HAIL_MARY_PASS);
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

		if (!actingPlayer.hasPassed() && !actingPlayer.isSufferingAnimosity()
			&& (actingPlayer.getPlayerAction() == PlayerAction.PASS || actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_PASS)) {
			actionContext.add(ClientAction.MOVE);
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
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			actionContext.add(ClientAction.THEN_I_STARTED_BLASTIN);
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
		return actionContext;
	}

	public boolean performsRangeGridAction(ActingPlayer actingPlayer, Game game) {
		return !actingPlayer.hasPassed();
	}
}

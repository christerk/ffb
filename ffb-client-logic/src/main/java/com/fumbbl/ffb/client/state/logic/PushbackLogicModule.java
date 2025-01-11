package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class PushbackLogicModule extends LogicModule {

	public PushbackLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PUSHBACK;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Pushback pushback = findPushback(findUnlockedPushbackSquare(pCoordinate));
		if (pushback != null) {
			client.getCommunication().sendPushback(pushback);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(pPlayer);
		Pushback pushback = findPushback(findUnlockedPushbackSquare(playerCoordinate));
		if (pushback != null) {
			client.getCommunication().sendPushback(pushback);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		return fieldPeek(getCoordinate(player));
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		PushbackSquare[] pushbackSquares = client.getGame().getFieldModel().getPushbackSquares();
		for (PushbackSquare pushbackSquare : pushbackSquares) {
			if (pCoordinate.equals(pushbackSquare.getCoordinate())) {
				if (pushbackSquare.isHomeChoice() && !pushbackSquare.isSelected() && !pushbackSquare.isLocked()) {
					pushbackSquare.setSelected(true);
					return InteractionResult.handled().with(pushbackSquare);
				}
			} else {
				if (pushbackSquare.isSelected() && !pushbackSquare.isLocked()) {
					pushbackSquare.setSelected(false);
					return InteractionResult.handled().with(pushbackSquare);
				}
			}
		}
		return InteractionResult.ignore();
	}

	private PushbackSquare findUnlockedPushbackSquare(FieldCoordinate pCoordinate) {
		PushbackSquare unlockedPushbackSquare = null;
		PushbackSquare[] pushbackSquares = client.getGame().getFieldModel().getPushbackSquares();
		for (PushbackSquare pushbackSquare : pushbackSquares) {
			if (!pushbackSquare.isLocked() && pushbackSquare.getCoordinate().equals(pCoordinate)
				&& pushbackSquare.isHomeChoice()) {
				unlockedPushbackSquare = pushbackSquare;
				break;
			}
		}
		return unlockedPushbackSquare;
	}

	public boolean pushbackTo(Direction moveDirection) {
		PushbackSquare pushbackSquare = null;
		PushbackSquare[] pushbackSquares = client.getGame().getFieldModel().getPushbackSquares();
		for (PushbackSquare square : pushbackSquares) {
			if (!square.isLocked() && (square.getDirection() == moveDirection)) {
				pushbackSquare = square;
				break;
			}
		}
		Pushback pushback = findPushback(pushbackSquare);
		if (pushback != null) {
			client.getCommunication().sendPushback(pushback);
			return true;
		}
		return false;
	}

	private Pushback findPushback(PushbackSquare pPushbackSquare) {
		Pushback pushback = null;
		if (pPushbackSquare != null) {
			FieldCoordinate fromSquare = null;
			FieldCoordinate toSquare = pPushbackSquare.getCoordinate();
			if (toSquare != null) {
				switch (pPushbackSquare.getDirection()) {
				case NORTH:
					fromSquare = toSquare.add(0, 1);
					break;
				case NORTHEAST:
					fromSquare = toSquare.add(-1, 1);
					break;
				case EAST:
					fromSquare = toSquare.add(-1, 0);
					break;
				case SOUTHEAST:
					fromSquare = toSquare.add(-1, -1);
					break;
				case SOUTH:
					fromSquare = toSquare.add(0, -1);
					break;
				case SOUTHWEST:
					fromSquare = toSquare.add(1, -1);
					break;
				case WEST:
					fromSquare = toSquare.add(1, 0);
					break;
				case NORTHWEST:
					fromSquare = toSquare.add(1, 1);
					break;
				}
			}
			Player<?> pushedPlayer = client.getGame().getFieldModel().getPlayer(fromSquare);
			if ((fromSquare != null) && (pushedPlayer != null)) {
				pushback = new Pushback(pushedPlayer.getId(), toSquare);
			}
		}
		return pushback;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in pushback context");
	}

}

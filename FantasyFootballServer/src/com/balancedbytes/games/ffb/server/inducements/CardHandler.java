package com.balancedbytes.games.ffb.server.inducements;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public abstract class CardHandler implements INamedObject {
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	protected abstract CardHandlerKey handlerKey();

	public boolean isResponsible(Card card) {
		return card.handlerKey().isPresent() && card.handlerKey().get() == handlerKey();
	}

	public void activate(Card card, IStep step, Player<?> player) {}

	public void deactivate(Card card, IStep step, Player<?> player) {}

	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		return false;
	}
}

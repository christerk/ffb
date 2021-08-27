package com.fumbbl.ffb.server.inducements;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.step.IStep;

public abstract class CardHandler implements INamedObject {
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	protected abstract CardHandlerKey handlerKey();

	public boolean isResponsible(Card card) {
		return card.handlerKey().isPresent() && card.handlerKey().get() == handlerKey();
	}

	public boolean activate(Card card, IStep step, Player<?> player) { return true; }

	public void deactivate(Card card, IStep step, Player<?> player) {}

	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		return true;
	}
}

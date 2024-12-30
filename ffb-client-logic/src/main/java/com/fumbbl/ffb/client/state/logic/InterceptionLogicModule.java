package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilPassing;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class InterceptionLogicModule extends LogicModule {

	private Skill interceptionSkill;
	public InterceptionLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.INTERCEPTION;
	}

	@Override
	public void postInit() {
		interceptionSkill = null;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		if (isInterceptor(pPlayer)) {
			client.getCommunication().sendInterceptorChoice(pPlayer, interceptionSkill);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}


	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (isInterceptor(pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
	}

	private boolean isInterceptor(Player<?> pPlayer) {
		boolean isInterceptor = false;

		if (interceptionSkill == null || pPlayer.hasUnused(interceptionSkill)) {

			Game game = client.getGame();
			Player<?>[] interceptors = UtilPassing.findInterceptors(game, game.getThrower(), game.getPassCoordinate());
			for (Player<?> interceptor : interceptors) {
				if (interceptor == pPlayer) {
					isInterceptor = true;
					break;
				}
			}
		}
		return isInterceptor;
	}

	public void setInterceptionSkill(Skill interceptionSkill) {
		this.interceptionSkill = interceptionSkill;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}
}

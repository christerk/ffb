package com.fumbbl.ffb.client.state.logic.plugin.mixed;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2016)
public class BlockLogicExtensionPlugin extends com.fumbbl.ffb.client.state.logic.plugin.BlockLogicExtensionPlugin {

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.singleton(ClientAction.THEN_I_STARTED_BLASTIN);
	}

	@Override
	public void performAvailableAction(ClientAction action, ActingPlayer actingPlayer,
		BlockLogicExtension logicModule, ClientCommunication communication, Player<?> defender) {
		switch (action) {
			case THEN_I_STARTED_BLASTIN:
				if (logicModule.isThenIStartedBlastinAvailable(actingPlayer)) {
					Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
					communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
				}
				break;
			default:
				break;
		}

	}

	@Override
	public ActionContext actionContext(ActingPlayer actingPlayer, ActionContext actionContext,
		BlockLogicExtension logicModule) {

		if (logicModule.isThenIStartedBlastinAvailable(actingPlayer)) {
			actionContext.add(ClientAction.THEN_I_STARTED_BLASTIN);
		}

		return actionContext;
	}

	@Override
	public boolean playerCanNotMove(PlayerState playerState) {
		return playerState.isRooted();
	}

	@Override
	public ActionContext blockActionContext(ActingPlayer actingPlayer, boolean multiBlock, ActionContext actionContext,
		BlockLogicExtension logicModule) {
		return actionContext;
	}
}

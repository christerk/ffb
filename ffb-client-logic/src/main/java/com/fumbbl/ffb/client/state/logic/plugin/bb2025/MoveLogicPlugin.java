package com.fumbbl.ffb.client.state.logic.plugin.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class MoveLogicPlugin extends com.fumbbl.ffb.client.state.logic.plugin.MoveLogicPlugin {

	@Override
	public Type getType() {
		return Type.MOVE;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.singleton(ClientAction.INCORPOREAL);
	}

	@Override
	public void performAvailableAction(ClientAction action, ActingPlayer actingPlayer,
		MoveLogicModule logicModule, ClientCommunication communication) {

		switch (action) {
			case INCORPOREAL:
				if (logicModule.isIncorporealAvailable(actingPlayer)) {
					Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canAvoidDodging);
					boolean incorporealActive = actingPlayer.getPlayer().hasActiveEnhancement(skill);
					communication.sendUseSkill(skill, !incorporealActive, actingPlayer.getPlayer().getId());
				}
				break;
			default:
				break;
		}
	}

	@Override
	public ActionContext actionContext(ActingPlayer actingPlayer, ActionContext actionContext,
		MoveLogicModule logicModule) {

		if (logicModule.isIncorporealAvailable(actingPlayer)) {
			actionContext.add(ClientAction.INCORPOREAL);
			if (actingPlayer.getPlayer().hasActiveEnhancement(NamedProperties.canAvoidDodging)) {
				actionContext.add(Influences.INCORPOREAL_ACTIVE);
			}
		}
		return actionContext;
	}
}

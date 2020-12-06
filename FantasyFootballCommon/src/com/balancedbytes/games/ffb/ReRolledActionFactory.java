package com.balancedbytes.games.ffb;

import java.util.Map;

import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class ReRolledActionFactory implements INamedObjectFactory {

	static ReRolledActions rerolledActions;

	public ReRolledActionFactory() {
		rerolledActions = new ReRolledActions();
	}

	public ReRolledAction forName(String pName) {
		return rerolledActions.values().get(pName.toLowerCase());
	}

	public ReRolledAction forSkill(Skill pSkill) {
		for (Map.Entry<String, ReRolledAction> entry : rerolledActions.values().entrySet()) {
			ReRolledAction action = entry.getValue();
			if (pSkill == action.getSkill()) {
				return action;
			}
		}
		return null;
	}

}

package com.fumbbl.ffb.factory;

import java.util.Map;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.RE_ROLLED_ACTION)
@RulesCollection(Rules.COMMON)
public class ReRolledActionFactory implements INamedObjectFactory {

	static ReRolledActions rerolledActions;

	public ReRolledActionFactory() {
		rerolledActions = new ReRolledActions();
	}

	public ReRolledAction forName(String pName) {
		return rerolledActions.values().get(pName.toLowerCase());
	}

	public ReRolledAction forSkill(Game game, Skill pSkill) {
		for (Map.Entry<String, ReRolledAction> entry : rerolledActions.values().entrySet()) {
			ReRolledAction action = entry.getValue();
			if (pSkill == action.getSkill(game.getRules().getSkillFactory())) {
				return action;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}

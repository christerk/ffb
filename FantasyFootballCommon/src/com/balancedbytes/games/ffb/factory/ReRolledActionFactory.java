package com.balancedbytes.games.ffb.factory;

import java.util.Map;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;

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

package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.INDUCEMENT_PHASE)
@RulesCollection(Rules.COMMON)
public class InducementPhaseFactory implements INamedObjectFactory {

	public InducementPhase forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (InducementPhase phase : InducementPhase.values()) {
				if (phase.getName().equalsIgnoreCase(pName)) {
					return phase;
				}
			}
			// backwards compatibility (name change)
			if ("afterKickoffToOpponentResolved".equals(pName)) {
				return InducementPhase.AFTER_KICKOFF_TO_OPPONENT;
			}
		}
		return null;
	}

	public String getDescription(InducementPhase[] pPhases) {
		StringBuilder description = new StringBuilder();
		description.append("Play ");
		boolean firstPhase = true;
		for (InducementPhase phase : pPhases) {
			if (firstPhase) {
				firstPhase = false;
			} else {
				description.append(" or ");
			}
			description.append(phase.getDescription());
		}
		return description.toString();
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}

package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

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
	}

}

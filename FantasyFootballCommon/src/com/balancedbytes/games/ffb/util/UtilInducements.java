package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.*;

import java.util.ArrayList;
import java.util.List;

public final class UtilInducements {
	private static final List<String> ROSTERS_WITH_CHEAP_BRIBES = new ArrayList<String>() {{
		add("Goblin");
		add("Snotling");
	}};

	public static int findInducementCost(Roster pRoster, InducementType pInducement, GameOptions gameOptions) {

		IGameOption gameOption = gameOptions.getOptionWithDefault(inducementCostOption(pRoster.getName(), pInducement));

		if (gameOption instanceof GameOptionInt) {
			return ((GameOptionInt)gameOption).getValue();
		}

		return 0;
	}

	private static GameOptionId inducementCostOption(String rosterName, InducementType inducementType) {
		if ((InducementType.BRIBES == inducementType && ROSTERS_WITH_CHEAP_BRIBES.contains(rosterName)) ||
			(InducementType.MASTER_CHEF == inducementType && "Halfling".equals(rosterName))) {
			return inducementType.getReducedCostId();
		}

		return inducementType.getCostId();
	}


	public static int findInducementsAvailable(Roster pRoster, InducementType pInducement, GameOptions gameOptions) {
		if (InducementType.WIZARD == pInducement) {
			IGameOption wizardOption = gameOptions.getOptionWithDefault(InducementType.WIZARD.getMaxId());
			if (!wizardOption.isChanged()) {
				return ((GameOptionBoolean)gameOptions.getOptionWithDefault(GameOptionId.WIZARD_AVAILABLE)).isEnabled() ? 1 : 0;
			}
		}

		if (InducementType.IGOR == pInducement && pRoster.hasApothecary()) {
			return 0;
		}

		if (InducementType.WANDERING_APOTHECARIES == pInducement && !pRoster.hasApothecary()) {
			return 0;
		}

		IGameOption gameOption = gameOptions.getOptionWithDefault(pInducement.getMaxId());

		if (gameOption instanceof GameOptionInt) {
			return ((GameOptionInt) gameOption).getValue();
		}

		return 0;
	}
}
